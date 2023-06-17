package xyz.openautomaker.base.importers.twod.svg;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.importers.twod.svg.metadata.RenderSVGToStylusMetaResult;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGClosePath;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGEndPath;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGMetaLine;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGMetaPart;
import xyz.openautomaker.base.importers.twod.svg.metadata.SVGStartPath;
import xyz.openautomaker.base.importers.twod.svg.metadata.dragknife.StylusMetaPart;

/**
 *
 * @author ianhudson
 */
public class SVGToStylusMetaEngine
{

	private static final Logger LOGGER = LogManager.getLogger();

    public static List<StylusMetaPart> convertToStylusMetaParts(List<SVGMetaPart> metaparts)
    {
        List<StylusMetaPart> dragknifemetaparts = new ArrayList<>();

        double currentX = 0;
        double currentY = 0;
        dragknifemetaparts.clear();

        double startX = 0, startY = 0;
        RenderSVGToStylusMetaResult renderResult = null;

        for (SVGMetaPart part : metaparts)
        {
            if (part instanceof SVGStartPath)
            {
                startX = currentX;
                startY = currentY;
            } else if (part instanceof SVGClosePath)
            {
                SVGMetaLine closeLine = new SVGMetaLine(startX, startY, true);
                renderResult = closeLine.renderToDragKnifeMetaParts(currentX, currentY);
            } else if (!(part instanceof SVGEndPath))
            {
                renderResult = part.renderToDragKnifeMetaParts(currentX, currentY);
            }

            if (renderResult != null)
            {
                currentX = renderResult.getResultantX();
                currentY = renderResult.getResultantY();
                for (StylusMetaPart dragknifemetapart : renderResult.getDragKnifeMetaParts())
                {
                    dragknifemetaparts.add(dragknifemetapart);
                }

                renderResult = null;
            }
        }
        
        return dragknifemetaparts;
    }
}
