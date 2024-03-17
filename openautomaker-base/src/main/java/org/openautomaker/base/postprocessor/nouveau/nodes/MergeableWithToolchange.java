package org.openautomaker.base.postprocessor.nouveau.nodes;

/**
 *
 * @author Ian
 */
public interface MergeableWithToolchange
{
    public void changeToolDuringMovement(int toolNumber);
}
