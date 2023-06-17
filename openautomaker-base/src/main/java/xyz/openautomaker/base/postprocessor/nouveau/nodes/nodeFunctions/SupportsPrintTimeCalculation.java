package xyz.openautomaker.base.postprocessor.nouveau.nodes.nodeFunctions;

import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.MovementProvider;

/**
 *
 * @author Ian
 */
public interface SupportsPrintTimeCalculation
{
    public double timeToReach(MovementProvider destinationNode) throws DurationCalculationException;
}
