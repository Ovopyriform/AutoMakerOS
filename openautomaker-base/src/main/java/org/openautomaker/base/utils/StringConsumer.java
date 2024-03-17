package org.openautomaker.base.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Tony Aldhous
 */
class StringConsumer extends Thread
{
    private final InputStream is;

	private static final Logger LOGGER = LogManager.getLogger();

    private final StringBuilder stringBuilder = new StringBuilder();
    
    public StringConsumer(InputStream is)
    {
        this.is = is;
    }

    public String getString()
    {
        try {
            this.join();
        }
        catch (InterruptedException ex) {
        }
        return stringBuilder.toString();
    }

    @Override
    public void run()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (stringBuilder.length() > 0)
                {
                    stringBuilder.append(System.getProperty("line.separator"));
                }
                stringBuilder.append(line);
            }
        } catch (IOException ioe)
        {
			LOGGER.error(ioe.getMessage());
        }
    }
}
