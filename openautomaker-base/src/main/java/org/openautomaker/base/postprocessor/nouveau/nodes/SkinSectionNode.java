package org.openautomaker.base.postprocessor.nouveau.nodes;

import org.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class SkinSectionNode extends SectionNode implements Renderable
{

    public static final String designator = ";TYPE:SKIN";

    @Override
    public String renderForOutput()
    {
        StringBuilder stringToOutput = new StringBuilder();
        stringToOutput.append(designator);
        stringToOutput.append(' ');
        stringToOutput.append(getCommentText());
        return designator;
    }
}
