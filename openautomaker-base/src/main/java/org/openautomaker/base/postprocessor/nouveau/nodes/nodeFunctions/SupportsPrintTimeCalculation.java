package org.openautomaker.base.postprocessor.nouveau.nodes.nodeFunctions;

import org.openautomaker.base.postprocessor.nouveau.nodes.providers.MovementProvider;

/**
 *
 * @author Ian
 */
public interface SupportsPrintTimeCalculation
{
    public double timeToReach(MovementProvider destinationNode) throws DurationCalculationException;
}
