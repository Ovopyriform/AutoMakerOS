package xyz.openautomaker.base.postprocessor.nouveau;

import org.parboiled.Action;
import org.parboiled.Context;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.OrphanObjectDelineationNode;

/**
 *
 * @author Ian
 */
public class OrphanObjectSectionActionClass implements Action
{
    private OrphanObjectDelineationNode node = null;
    
    @Override
    public boolean run(Context context)
    {
        node = new OrphanObjectDelineationNode();
        return true;
    }

    public OrphanObjectDelineationNode getNode()
    {
        return node;
    }
}
