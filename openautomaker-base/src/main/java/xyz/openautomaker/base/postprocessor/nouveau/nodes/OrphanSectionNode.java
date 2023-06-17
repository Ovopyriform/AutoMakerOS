package xyz.openautomaker.base.postprocessor.nouveau.nodes;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class OrphanSectionNode extends SectionNode implements Renderable
{
    @Override
    public String renderForOutput()
    {
        return ";Orphan section";
    }
}
