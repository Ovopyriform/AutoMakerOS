package org.openautomaker.base.postprocessor.nouveau;

import java.util.List;

import org.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;

/**
 *
 * @author Ian
 */
public class InScopeEvents
{

    private final List<GCodeEventNode> inScopeEvents;
    private final double availableExtrusion;

    public InScopeEvents(List<GCodeEventNode> inScopeEvents, double availableExtrusion)
    {
        this.inScopeEvents = inScopeEvents;
        this.availableExtrusion = availableExtrusion;
    }

    public double getAvailableExtrusion()
    {
        return availableExtrusion;
    }

    public List<GCodeEventNode> getInScopeEvents()
    {
        return inScopeEvents;
    }
}
