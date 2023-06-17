package xyz.openautomaker.base.postprocessor.nouveau.nodes;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class OuterPerimeterSectionNode extends SectionNode implements Renderable
{

    public static final String designator = ";TYPE:WALL-OUTER";

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();
        stringToOutput.append(designator);
        stringToOutput.append(getCommentText());
        return designator;
    }
}
