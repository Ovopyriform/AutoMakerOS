package xyz.openautomaker.base.postprocessor.nouveau;

import org.parboiled.Action;
import org.parboiled.Context;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.ObjectDelineationNode;

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
