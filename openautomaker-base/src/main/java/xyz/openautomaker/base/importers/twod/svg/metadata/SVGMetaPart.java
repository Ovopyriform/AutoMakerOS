package xyz.openautomaker.base.importers.twod.svg.metadata;

/**
 *
 * @author ianhudson
 */
public abstract class SVGMetaPart
{
    /**
     *
     * @param currentX
     * @param currentY
     * @return 
     */
    public abstract RenderSVGToStylusMetaResult renderToDragKnifeMetaParts(double currentX, double currentY);
}
