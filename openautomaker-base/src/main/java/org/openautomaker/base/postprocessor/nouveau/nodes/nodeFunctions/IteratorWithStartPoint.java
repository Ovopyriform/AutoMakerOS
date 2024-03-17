package org.openautomaker.base.postprocessor.nouveau.nodes.nodeFunctions;

import java.util.Iterator;
import java.util.List;

import org.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public abstract class IteratorWithStartPoint<T> implements Iterator<T>
{

    public IteratorWithStartPoint(List<GCodeEventNode> startNodeHierarchy)
    {
        initialiseWithList(startNodeHierarchy);
    }

    public abstract void initialiseWithList(List<GCodeEventNode> startNodeHierarchy);
}
