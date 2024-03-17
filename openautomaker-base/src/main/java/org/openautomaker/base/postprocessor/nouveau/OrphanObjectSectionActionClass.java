package org.openautomaker.base.postprocessor.nouveau;

import org.openautomaker.base.postprocessor.nouveau.nodes.OrphanObjectDelineationNode;
import org.parboiled.Action;
import org.parboiled.Context;

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
