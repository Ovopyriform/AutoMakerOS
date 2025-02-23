package org.openautomaker.base.postprocessor.nouveau;

import org.openautomaker.base.postprocessor.NozzleProxy;
import org.openautomaker.base.postprocessor.nouveau.nodes.RetractNode;

/**
 *
 * @author Ian
 */
public class RetractHolder
{

    private final RetractNode node;
    private final NozzleProxy nozzle;

    public RetractHolder(RetractNode node, NozzleProxy nozzle)
    {
        this.node = node;
        this.nozzle = nozzle;
    }

    public RetractNode getNode()
    {
        return node;
    }

    public NozzleProxy getNozzle()
    {
        return nozzle;
    }

}
