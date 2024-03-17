package org.openautomaker.base.postprocessor.nouveau.nodes;

import org.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public class GCodeEventNodeTestImpl extends GCodeEventNode
{

    private String name;

    public GCodeEventNodeTestImpl()
    {
    }

    public GCodeEventNodeTestImpl(String name)
    {
        this.name = name;
    }
}
