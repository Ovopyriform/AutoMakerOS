package celtech.roboxbase.comms.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.roboxbase.comms.CommandInterface;
import celtech.roboxbase.comms.exceptions.ConnectionLostException;
import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.RoboxRxPacket;
import celtech.roboxbase.comms.rx.RoboxRxPacketFactory;
import celtech.roboxbase.comms.rx.RxPacketTypeEnum;
import celtech.roboxbase.comms.tx.TxPacketTypeEnum;

/**
 *
 * @author Ian
 */
public class AsyncWriteThread extends Thread {
	// The timeout here has to be at least greater than the sum of the connect and read time out
	// values in detected server, as it can wait at least as long as that. 
	private static final int NUMBER_OF_SIMULTANEOUS_COMMANDS = 50;

	// The thread can retry sending a command if it fails due to a timeout. Currently the MAX_COMMAND_RETRIES
	// is one, which disables the mechanism.
	private static final int MAX_COMMAND_RETRIES = 1;

	// The poll timeout must be longer than the total timeout of the detected server. Otherwise
	// this thread can timeout before the server to which it is connected times out.
	// It is 12 seconds because the remote server can pause for several seconds, for reasons unknown.
	private static final int POLL_TIMEOUT = 30000;

	private static final Logger LOGGER = LogManager.getLogger();

	private final BlockingQueue<CommandHolder> inboundQueue = new ArrayBlockingQueue<>(NUMBER_OF_SIMULTANEOUS_COMMANDS);
	private final List<BlockingQueue<RoboxRxPacket>> outboundQueues;

	private final CommandInterface commandInterface;
	private boolean keepRunning = true;
	private boolean[] queueInUse = new boolean[NUMBER_OF_SIMULTANEOUS_COMMANDS];

	private static CommandHolder poisonedPill = new CommandHolder(-1, null);

	public AsyncWriteThread(CommandInterface commandInterface, String ciReference) {
		this.commandInterface = commandInterface;
		this.setDaemon(true);
		this.setName("AsyncCommandProcessor|" + ciReference);
		this.setPriority(Thread.MAX_PRIORITY);

		outboundQueues = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_SIMULTANEOUS_COMMANDS; i++) {
			outboundQueues.add(new ArrayBlockingQueue<>(1));
			queueInUse[i] = false;
		}
	}

	private int addCommandToQueue(CommandPacket command) throws RoboxCommsException {
		int queueNumber = -1;

		// Look for an empty outbound queue
		for (int queueIndex = 0; queueIndex < outboundQueues.size(); queueIndex++) {
			if (!queueInUse[queueIndex]) {
				outboundQueues.get(queueIndex).clear(); // Clear out any junk in the queue.
				CommandHolder commandHolder = new CommandHolder(queueIndex, command);
				inboundQueue.add(commandHolder);
				queueNumber = queueIndex;
				queueInUse[queueIndex] = true;
				break;
			}
		}

		if (queueNumber < 0) {
			LOGGER.info("Message queue full; can not add command:" + command.getCommand().getPacketType());
			throw new RoboxCommsException("Message queue full");
		}

		return queueNumber;
	}

	public synchronized RoboxRxPacket sendCommand(CommandPacket command) throws RoboxCommsException {
		RoboxRxPacket response = null;

		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("**** Sending command:" + command.getCommand().getPacketType());

			if (command.getCommand().getPacketType() == TxPacketTypeEnum.DATA_FILE_CHUNK)
				LOGGER.trace(" sequence number = " + command.getCommand().getSequenceNumber());
		}

		for (int retryCount = 0; response == null && retryCount < MAX_COMMAND_RETRIES; ++retryCount) {

			if (LOGGER.isTraceEnabled()) {
				if (retryCount == 0)
					LOGGER.trace("@@@@ Adding command " + command.getCommand().getPacketType() + " to queue");
				else
					LOGGER.trace("@@@@ requeuing (" + retryCount + ") command " + command.getCommand().getPacketType());
			}

			int queueNumber = addCommandToQueue(command);

			try {
				if (LOGGER.isTraceEnabled())
					LOGGER.info(" Awaiting response on queue " + queueNumber);

				// If the async command processor writes to
				// the queue after the listener has timed out, it used to cause the queue to
				// be permanantly lost, because it contained an entry. Now it clears the queue.
				// However, there is still a risk that if a timed-out queue is used, it could
				// get the response intended for the previous queue. This is quite a tricky problem.
				long t1 = System.currentTimeMillis();

				// The timeout here has to be at least greater than the sum of the connect and read time out
				// values and 
				response = outboundQueues.get(queueNumber).poll(POLL_TIMEOUT, TimeUnit.MILLISECONDS);
				long t2 = System.currentTimeMillis();

				if (LOGGER.isTraceEnabled()) {
					if (response == null) {
						LOGGER.trace(" No response on queue " + queueNumber);
					}
					else {
						LOGGER.trace(" Received response on queue " + queueNumber);
						LOGGER.trace(" response:" + response.getPacketType());
					}
				}

				// Null response packet, make response null
				if (response != null && response.getPacketType() == RxPacketTypeEnum.NULL_PACKET)
					response = null;

				if (LOGGER.isTraceEnabled()) {
					long dt = t2 - t1;
					if (dt > 500) {
						LOGGER.trace("Long wait (" + Long.toString(dt) + ") for response to command " + command.getCommand().getPacketType());

						if (command.getCommand().getPacketType() == TxPacketTypeEnum.DATA_FILE_CHUNK)
							LOGGER.trace("    sequence number = " + command.getCommand().getSequenceNumber());

						if (retryCount > 0)
							LOGGER.trace("    retryCount = " + retryCount);

					}

					LOGGER.trace(" Time taken = " + Long.toString(t2 - t1));
				}
			}
			catch (InterruptedException ex) {
				LOGGER.debug("**** Throwing RoboxCommsException('Interrupted waiting for response') on queue " + queueNumber);
				throw new RoboxCommsException("Interrupted waiting for response");
			}
			finally {
				queueInUse[queueNumber] = false;
			}
		}

		if (response == null || response.getPacketType() == RxPacketTypeEnum.NULL_PACKET) {
			LOGGER.debug("**** Throwing RoboxCommsException('No response to message from command " + command + "')");
			throw new RoboxCommsException("No response to message from command " + command);
		}

		// LOGGER.info("**** Returning response " + response.getPacketType() + " for command " + command);
		return response;
	}

	@Override
	public void run() {
		while (keepRunning) {
			boolean createNullPacket = true;
			CommandHolder commandHolder = null;
			try {
				// LOGGER.info("++++ Taking a command");
				commandHolder = inboundQueue.take();
				if (commandHolder != poisonedPill) {
					// LOGGER.info("++++ Processing command for queue " + commandHolder.getQueueIndex() + " : " + commandHolder.getCommandPacket().getCommand().getPacketType());
					RoboxRxPacket response = processCommand(commandHolder.getCommandPacket());
					// LOGGER.info("++++ Got response for queue " + commandHolder.getQueueIndex());

					if (response != null) {
						createNullPacket = false;
						// LOGGER.info("++++ sending response to queue " + commandHolder.getQueueIndex());
						if (outboundQueues.get(commandHolder.getQueueIndex()).offer(response)) {
							// LOGGER.info("++++ sent response to queue " + commandHolder.getQueueIndex());
						}
						else {
							// Queue is full. Nothing is waiting for the response to this queue, so empty the queue.
							BlockingQueue<RoboxRxPacket> q = outboundQueues.get(commandHolder.getQueueIndex());
							LOGGER.warn("++++ Unable to send response to queue " + commandHolder.getQueueIndex());
							// LOGGER.warning("++++ Queue already contains " + Integer.toString(q.size()) + "responses");
							//if (q.size() > 0)
							//{
							//RoboxRxPacket[] r = q.toArray(new RoboxRxPacket[0]);
							// for (int rIndex = 0; rIndex < r.length; rIndex++)
							//{
							// LOGGER.warning("++++ Response " + Integer.toString(rIndex) + " = " + r[rIndex].getPacketType());
							//}
							//}
							q.clear();
						}
					}
				}
				else {
					// LOGGER.info("++++ Got poisoned pill");
					//Just drop out - we got the poisoned pill
					createNullPacket = false;
				}
			}
			catch (ConnectionLostException ex) {
				// This is ok - the printer has probably been unplugged
				LOGGER.info("Connection lost - " + getName());
			}
			catch (RoboxCommsException | InterruptedException ex) {
				LOGGER.warn("Unexpected error during write", ex);
			}
			finally {
				if (createNullPacket) {
					// LOGGER.info("++++ sending null response to queue " + commandHolder.getQueueIndex());
					if (outboundQueues.get(commandHolder.getQueueIndex()).offer(RoboxRxPacketFactory.createNullPacket())) {
						// LOGGER.info("++++ sent null response to queue " + commandHolder.getQueueIndex());
					}
					else {
						// Nothing is waiting for the response to this queue.
						BlockingQueue<RoboxRxPacket> q = outboundQueues.get(commandHolder.getQueueIndex());
						// LOGGER.warning("++++ Unable to send null response to queue " + commandHolder.getQueueIndex());
						// LOGGER.warning("++++ Queue already contains " + Integer.toString(q.size()) + "responses");
						//if (q.size() > 0)
						//{
						//    RoboxRxPacket[] r = q.toArray(new RoboxRxPacket[0]);
						//    for (int rIndex = 0; rIndex < r.length; rIndex++)
						//    {
						// LOGGER.warning("++++ Response " + Integer.toString(rIndex) + " = " + r[rIndex].getPacketType());
						//    }
						//}
						q.clear();
					}
				}
			}
		}
	}

	private RoboxRxPacket processCommand(CommandPacket command) throws RoboxCommsException {
		RoboxRxPacket response = commandInterface.writeToPrinterImpl(command.getCommand(), command.getDontPublish());
		return response;
	}

	public void shutdown() {
		keepRunning = false;
		inboundQueue.add(poisonedPill);
	}
}
