package celtech.utils.threed.importers.svg;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import javafx.scene.Group;
import javafx.scene.shape.Polygon;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGMetaPart;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGMetaPolygon;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGStartPath;

/**
 *
 * @author ianhudson
 */
public class SVGMetaToRenderableEngine
{

	public static Group renderSVG(List<SVGMetaPart> metaparts)
	{
		Group createdGroup = new Group();

		for (SVGMetaPart part : metaparts)
		{
			if (part instanceof SVGMetaPolygon)
			{
				Polygon polygon = new Polygon();
				List<Vector2D> points = ((SVGMetaPolygon) part).getPoints();
				for (Vector2D point : points)
				{
					polygon.getPoints().add(point.getX());
					polygon.getPoints().add(point.getY());
				}

				createdGroup.getChildren().add(polygon);
			}
			else if (part instanceof SVGStartPath)
			{
			}
		}

		return createdGroup;
	}
}
