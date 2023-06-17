package xyz.openautomaker.root;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.parboiled.common.FileUtils;

import xyz.openautomaker.base.printerControl.PrintJob;

/**
 *
 * @author Ian
 */
public class PrintJobPersister
{

	private static final Logger LOGGER = LogManager.getLogger();

	private static PrintJobPersister instance = null;
	private String printJobIDBeingPersisted = null;
	private BufferedWriter printJobWriter = null;

	private PrintJobPersister()
	{
	}

	public static PrintJobPersister getInstance()
	{
		if (instance == null)
		{
			instance = new PrintJobPersister();
		}

		return instance;
	}

	public void startFile(String printJobID)
	{
		try
		{
			if (printJobWriter != null)
			{
				//Let's close this file and flag the unusual circumstance...
				printJobWriter.close();
				LOGGER.error("Found a partially persisted remote file for job " + printJobIDBeingPersisted);
			}

			//                        LOGGER.info("Receiving print job " + remoteTx.getMessagePayload());
			printJobIDBeingPersisted = printJobID;
			PrintJob pj = new PrintJob(printJobIDBeingPersisted);

			FileUtils.forceMkdir(pj.getRoboxisedFileLocation().toFile().getParentFile());
			FileWriter fw = new FileWriter(pj.getRoboxisedFileLocation().toFile());
			printJobWriter = new BufferedWriter(fw);
		} catch (IOException ex)
		{
			LOGGER.error("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
		}
	}

	public void writeSegment(String segment)
	{
		//                    LOGGER.info("Got chunk " + payload);
		if (printJobWriter != null)
		{
			try
			{
				printJobWriter.write(segment);
			} catch (IOException ex)
			{
				LOGGER.error("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
			}
		} else
		{
			LOGGER.error("Unable to process remote file segment - no local file open.");
		}
	}

	public void closeFile(String segment)
	{
		LOGGER.info("End of print job " + printJobIDBeingPersisted);

		if (printJobWriter != null)
		{
			try
			{
				printJobWriter.write(segment);
				printJobWriter.flush();
				printJobWriter.close();
				printJobWriter = null;
				printJobIDBeingPersisted = null;
			} catch (IOException ex)
			{
				LOGGER.error("Error when attempting to persist remote file " + printJobIDBeingPersisted, ex);
			}
		} else
		{
			LOGGER.error("Unable to process end of remote file - no local file open.");
		}
	}

	public String getPrintJobID()
	{
		return printJobIDBeingPersisted;
	}
}
