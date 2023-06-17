
package xyz.openautomaker.base.postprocessor;

import xyz.openautomaker.base.postprocessor.events.GCodeParseEvent;

/**
 *
 * @author Ian
 */
public interface GCodeTranslationEventHandler
{    

    /**
     *
     * @param event
     * @throws NozzleCloseSettingsError
     */
    public void processEvent(GCodeParseEvent event) throws PostProcessingError;

    /**
     *
     * @param line
     */
    public void unableToParse(String line);
}
