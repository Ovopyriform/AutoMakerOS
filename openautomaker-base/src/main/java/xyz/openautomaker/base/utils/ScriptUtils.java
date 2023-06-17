package xyz.openautomaker.base.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Ian & George Salter
 */
public class ScriptUtils
{
	private static final Logger LOGGER = LogManager.getLogger();
    
    public static String runScript(String pathToScript, int timeout, String... parameters)
    {
        List<String> command = new ArrayList<>();
        command.add(pathToScript);

        command.addAll(Arrays.asList(parameters));

        String c = new String();
        for(String s : command)
            c = c + " " + s;
		LOGGER.debug("Running script \"" + c + "\"");

        ProcessBuilder builder = new ProcessBuilder(command);

        String data = "";
            
        try
        {
            Process scriptProcess = builder.start();
            StringConsumer outputConsumer = new StringConsumer(scriptProcess.getInputStream());
            outputConsumer.start();
            if (timeout > 0) {
                if (scriptProcess.waitFor(timeout, TimeUnit.SECONDS)) {
                    if (scriptProcess.exitValue() == 0)
                        data = outputConsumer.getString();
                    else
						LOGGER.error("Script error");
                }
                else {
					LOGGER.error("Script timeout");
                    scriptProcess.destroyForcibly();
                }
            }
            else if (scriptProcess.waitFor() == 0)
                data = outputConsumer.getString();
            else
				LOGGER.error("Script error");
       } 
        catch (IOException | InterruptedException ex)
        {
			LOGGER.error("Error " + ex);
        }

        return data;
    }

    public static byte[] runByteScript(String pathToScript, int timeout, String... parameters)
    {
        List<String> command = new ArrayList<>();
        command.add(pathToScript);
        command.addAll(Arrays.asList(parameters));

        String c = new String();
        for(String s : command)
            c = c + " " + s;
		LOGGER.debug("Running script(B) \"" + c + "\"");
            
        ProcessBuilder builder = new ProcessBuilder(command);
        byte[] data = null;

		LOGGER.debug("Reading script output");
        try {
            Process scriptProcess = builder.start();
            ByteConsumer outputConsumer = new ByteConsumer(scriptProcess.getInputStream());
            outputConsumer.start();
            if (timeout > 0) {
                if (scriptProcess.waitFor(timeout, TimeUnit.SECONDS)) {
                    if (scriptProcess.exitValue() == 0)
                        data = outputConsumer.getBytes();
                    else
						LOGGER.error("Byte script error");
                }
                else {
					LOGGER.error("Byte script timeout");
                    scriptProcess.destroyForcibly();
                }
            }
            else if (scriptProcess.waitFor() == 0)
                data = outputConsumer.getBytes();
            else
				LOGGER.error("Byte script error");
        } 
        catch (IOException | InterruptedException ex)
        {
			LOGGER.error("Error " + ex);
        }

        return data;
    }
}