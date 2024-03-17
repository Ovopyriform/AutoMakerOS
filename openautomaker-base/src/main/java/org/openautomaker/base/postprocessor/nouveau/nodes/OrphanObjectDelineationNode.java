package org.openautomaker.base.postprocessor.nouveau.nodes;

import org.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class OrphanObjectDelineationNode extends GCodeEventNode implements Renderable
{
    private int potentialObjectNumber = -1;

    public int getPotentialObjectNumber()
    {
        return potentialObjectNumber;
    }

    public void setPotentialObjectNumber(int objectNumber)
    {
        this.potentialObjectNumber = objectNumber;
    }

    @Override
    public String renderForOutput()
    {
        return ";Orphan Object " + potentialObjectNumber;
    }
}
