package celtech.roboxbase.comms.interapp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;

/**
 *
 * @author ianhudson
 */
public class InterAppCommsThread extends Thread
{

	private static final Logger LOGGER = LogManager.getLogger();
    private boolean keepRunning = true;
    private ServerSocket initialServerSocket;
    private Socket serverSocket = null;
    private final ObjectMapper mapper = new ObjectMapper();
    private InterAppCommsConsumer commsConsumer = null;

    public InterAppCommsThread()
    {
        this.setName("InterAppComms");
    }

    @Override
    public void run()
    {
        while (keepRunning)
        {
            try
            {
                serverSocket = initialServerSocket.accept();

                InterAppRequest interAppRequest = mapper.readValue(serverSocket.getInputStream(), InterAppRequest.class);
                if (interAppRequest != null)
                {
					LOGGER.info("Was InterApp data:" + interAppRequest.toString());

                    if (commsConsumer != null)
                    {
                        commsConsumer.incomingComms(interAppRequest);
                    }
                }

            } catch (IOException ex)
            {
                // Get a "SocketException - socket closed" when the thread is terminated.
                if (keepRunning)
					LOGGER.error("Error trying to listen for InterApp comms");
            }
        }
    }

    public InterAppStartupStatus letUsBegin(InterAppRequest interAppCommsRequest, InterAppCommsConsumer commsConsumer)
    {
        this.commsConsumer = commsConsumer;

        InterAppStartupStatus status = InterAppStartupStatus.OTHER_ERROR;

        try
        {
            //Bind to localhost adapter with a zero connection queue 
            initialServerSocket = new ServerSocket(InterAppConfiguration.PORT, 0, InetAddress.getLoopbackAddress());

            status = InterAppStartupStatus.STARTED_OK;
            this.start();

        } catch (BindException e)
        {
            // If we had any load params then
			LOGGER.info("AutoMaker asked to start but instance is already running.");
            status = InterAppStartupStatus.ALREADY_RUNNING_COULDNT_CONTACT;

            try
            {
                Socket clientSocket = new Socket(InetAddress.getLoopbackAddress(), InterAppConfiguration.PORT);
                OutputStreamWriter out = new OutputStreamWriter(clientSocket.getOutputStream(), InterAppConfiguration.charSetToUse);

                String dataToOutput = mapper.writeValueAsString(interAppCommsRequest);

                out.write(dataToOutput);
                out.flush();

                clientSocket.close();
				LOGGER.debug("Told my sibling about the params I was passed");
                status = InterAppStartupStatus.ALREADY_RUNNING_CONTACT_MADE;
            } catch (IOException ex)
            {
				LOGGER.error("IOException when contacting sibling:" + ex.getMessage());
            } finally
            {
                Platform.exit();
            }
        } catch (IOException e)
        {
			LOGGER.error("Unexpected error whilst attempting to check if another app is running");
            e.printStackTrace();
            Platform.exit();
        }

        return status;
    }

    public void shutdown()
    {
        keepRunning = false;
        try
        {
            initialServerSocket.close();
        } catch (IOException ex)
        {
			LOGGER.error("Error whilst closing inter app comms socket", ex);
        }
    }
}
