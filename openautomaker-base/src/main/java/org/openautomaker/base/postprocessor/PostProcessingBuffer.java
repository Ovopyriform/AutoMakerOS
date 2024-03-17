package org.openautomaker.base.postprocessor;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.postprocessor.events.ExtrusionEvent;
import org.openautomaker.base.postprocessor.events.GCodeParseEvent;
import org.openautomaker.base.postprocessor.events.NozzleOpenFullyEvent;

/**
 *
 * @author Ian
 */
public class PostProcessingBuffer extends ArrayList<GCodeParseEvent>
{

	private static Logger LOGGER = LogManager.getLogger();

    int indexOfFirstExtrusionEvent = -1;
    double eExtrusionVolume = 0;
    double dExtrusionVolume = 0;

    public void emptyBufferToOutput(GCodeOutputWriter outputWriter) throws IOException
    {
        for (GCodeParseEvent event : this)
        {
            outputWriter.writeOutput(event.renderForOutput());
        }
        outputWriter.flush();
        clear();
    }

    public void closeNozzle(String comment, GCodeOutputWriter outputWriter)
    {

    }

    /**
     * This method inserts a NozzleOpenFully event just before the first
     * ExtrusionEvent
     */
    void openNozzleFullyBeforeExtrusion()
    {
        if (extrusionEventsArePresent())
        {
            add(indexOfFirstExtrusionEvent, new NozzleOpenFullyEvent());
            indexOfFirstExtrusionEvent++;
        }
    }

    @Override
    public boolean add(GCodeParseEvent e)
    {
        boolean added = super.add(e);

        if (e instanceof ExtrusionEvent)
        {
            
            if (added && !extrusionEventsArePresent())
            {
                indexOfFirstExtrusionEvent = size() - 1;
            }
        }

        return added;
    }

    @Override
    public void clear()
    {
        super.clear();
        indexOfFirstExtrusionEvent = -1;
        eExtrusionVolume = 0;
        dExtrusionVolume = 0;
    }

    private boolean extrusionEventsArePresent()
    {
        return indexOfFirstExtrusionEvent >= 0;
    }
}
