package xyz.openautomaker.base.postprocessor.events;

/**
 *
 * @author Ian
 */
public class BlankLineEvent extends GCodeParseEvent
{

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        return "\n";
    }
}
