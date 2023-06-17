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
public class DragKnifeMetaTravel extends StylusMetaPart
{

    public DragKnifeMetaTravel()
    {
        super(0, 0, 0, 0, null);
    }

    public DragKnifeMetaTravel(double startX, double startY, double endX, double endY, String comment)
    {
        super(startX, startY, endX, endY, comment);
    }

    @Override
    public String renderToGCode()
    {
        String gcodeLine = generateXYMove(getEnd().getX(), getEnd().getY(), SVGConverterConfiguration.getInstance().getTravelFeedrate(), "Travel - " + getComment());
        return gcodeLine;
    }

    @Override
    public List<GCodeEventNode> renderToGCodeNode()
    {
        List<GCodeEventNode> gcodeNodes = new ArrayList<>();

        TravelNode travelNode = new TravelNode();
        travelNode.setCommentText("Travel " + getComment());
        travelNode.getFeedrate().setFeedRate_mmPerMin(SVGConverterConfiguration.getInstance().getTravelFeedrate());
        travelNode.getMovement().setX(getEnd().getX());
        travelNode.getMovement().setY(getEnd().getY());

        gcodeNodes.add(travelNode);
        return gcodeNodes;
    }    
}
