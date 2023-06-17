package xyz.openautomaker.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Tony Aldhous
 */
class ByteConsumer extends Thread
{
    private final InputStream is;

	private static final Logger LOGGER = LogManager.getLogger();

    private final ByteArrayOutputStream scriptOutput = new ByteArrayOutputStream();
    
    public ByteConsumer(InputStream is)
    {
        this.is = is;
    }

    public byte[] getBytes()
    {
        try {
            this.join();
        }
        catch (InterruptedException ex) {
        }

        return scriptOutput.toByteArray();
    }

    @Override
    public void run()
    {
        try
        {
            byte[] data = new byte[1024];
            int bytesRead = is.read(data);
            while(bytesRead != -1) {
                //STENO.debug("Read " + bytesRead + " from input stream");
                scriptOutput.write(data, 0, bytesRead);
                bytesRead = is.read(data);
            }
        } catch (IOException ioe)
        {
			LOGGER.error(ioe.getMessage());
        }
    }
}
