package xyz.openautomaker.base.importers.twod.svg.metadata.dragknife;

import java.util.List;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author ianhudson
 */
public class StylusMetaUnhandled extends StylusMetaPart
{
    private final String comment;

    public StylusMetaUnhandled()
    {
        super(0, 0, 0, 0, null);
        this.comment = null;
    }
    
    public StylusMetaUnhandled(double startX, double startY, double endX, double endY, String comment)
    {
        super(startX, startY, endX, endY, comment);
        this.comment = comment;
    }
    
    @Override
    public String getComment()
    {
        return comment;
    }
    
    @Override
    public String renderToGCode()
    {
        return "; Unhandled - " + comment;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        return null;
    }
}
