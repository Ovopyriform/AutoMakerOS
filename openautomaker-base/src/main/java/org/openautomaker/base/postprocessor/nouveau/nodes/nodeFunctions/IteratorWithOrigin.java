package org.openautomaker.base.postprocessor.nouveau.nodes.nodeFunctions;

import java.util.Iterator;

import org.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public abstract class IteratorWithOrigin<T> implements Iterator<T>
{
    /**
     *
     * @param originNode
     */
    public abstract void setOriginNode(GCodeEventNode originNode);

}
