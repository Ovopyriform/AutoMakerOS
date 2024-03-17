package org.openautomaker.base.postprocessor.events;

/**
 *
 * @author Ian
 */
public class CommentEvent extends GCodeParseEvent
{

    /**
     *
     * @return
     */
    @Override
    public String renderForOutput()
    {
        return " ; " + getComment() + "\n";
    }
}
