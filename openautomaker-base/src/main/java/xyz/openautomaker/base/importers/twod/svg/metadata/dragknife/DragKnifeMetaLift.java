package xyz.openautomaker.base.importers.twod.svg.metadata.dragknife;

import java.util.ArrayList;
import java.util.List;

import xyz.openautomaker.base.importers.twod.svg.SVGConverterConfiguration;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.TravelNode;

/**
 *
 * @author ianhudson
 */
public class DragKnifeMetaLift extends StylusMetaPart
{

    public DragKnifeMetaLift()
    {
        super(0, 0, 0, 0, null);
    }

    public DragKnifeMetaLift(double startX, double startY, String comment)
    {
        super(startX, startY, 0, 0, comment);
    }

    @Override
    public String renderToGCode()
    {
        String gcodeLine = generateLift(getComment());
        return gcodeLine;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();

        TravelNode travelNode = new TravelNode();
        travelNode.setCommentText("Lift " + getComment());
        travelNode.getFeedrate().setFeedRate_mmPerMin(SVGConverterConfiguration.getInstance().getPlungeFeedrate());
        travelNode.getMovement().setZ(SVGConverterConfiguration.getInstance().getLiftDepth());

        gcodeNodes.add(travelNode);
        return gcodeNodes;
    }
}
