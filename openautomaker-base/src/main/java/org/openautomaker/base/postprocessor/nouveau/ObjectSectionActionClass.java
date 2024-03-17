package org.openautomaker.base.postprocessor.nouveau;

import org.openautomaker.base.postprocessor.nouveau.nodes.ObjectDelineationNode;
import org.parboiled.Action;
import org.parboiled.Context;

/**
 *
 * @author Ian
 */
public class ObjectSectionActionClass implements Action
{
    private ObjectDelineationNode node = null;
    
    @Override
    public boolean run(Context context)
    {
        node = new ObjectDelineationNode();
        return true;
    }

    public ObjectDelineationNode getNode()
    {
        return node;
    }
}
