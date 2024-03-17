package org.openautomaker.base.importers.twod.svg.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.openautomaker.base.importers.twod.svg.metadata.dragknife.DragKnifeMetaCut;
import org.openautomaker.base.importers.twod.svg.metadata.dragknife.StylusMetaPart;

/**
 *
 * @author ianhudson
 */
public class SVGMetaPolygon extends SVGMetaPart
{

    private List<Vector2D> points = new ArrayList<>();

    public SVGMetaPolygon()
    {
    }
    
    public void addPoint(double x, double y)
    {
        points.add(new Vector2D(x, y));
    }
    
    public List<Vector2D> getPoints()
    {
        return points;
    }

    @Override
    public RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY)
    {
        double resultantX = currentX, resultantY = currentY;
        List<StylusMetaPart> cuts = new ArrayList<>();

        for (Vector2D point : points)
        {
            DragKnifeMetaCut cut = new DragKnifeMetaCut(resultantX, resultantY, point.getX(), point.getY(), "Poly");
            cuts.add(cut);
            resultantX = point.getX();
            resultantY = point.getY();
        }
        
        RenderSVGToStylusMetaResult result = new RenderSVGToStylusMetaResult(resultantX, resultantY, cuts);
        return result;
    }
}
