package org.openautomaker.base.postprocessor.nouveau.spiralPrint;

import java.util.ArrayList;
import java.util.List;

import org.openautomaker.base.postprocessor.nouveau.LayerPostProcessResult;
import org.openautomaker.base.postprocessor.nouveau.nodes.ExtrusionNode;
import org.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;
import org.openautomaker.base.postprocessor.nouveau.nodes.TravelNode;
import org.openautomaker.base.postprocessor.nouveau.nodes.nodeFunctions.IteratorWithStartPoint;

/**
 *
 * @author Ian
 */
public class CuraSpiralPrintFixer
{

    public void fixSpiralPrint(List<LayerPostProcessResult> allLayerPostProcessResults)
    {
        boolean beginRemovingTravels = false;
        List<GCodeEventNode> travelNodesToDelete = new ArrayList<>();

        for (int layerCounter = 0; layerCounter < allLayerPostProcessResults.size(); layerCounter++)
        {
            LayerPostProcessResult layerPostProcessResult = allLayerPostProcessResults.get(layerCounter);

            IteratorWithStartPoint<GCodeEventNode> layerIterator = layerPostProcessResult.getLayerData().treeSpanningIterator(null);

            while (layerIterator.hasNext())
            {
                GCodeEventNode node = layerIterator.next();

                if (beginRemovingTravels
                        && node instanceof TravelNode)
                {
                    travelNodesToDelete.add(node);
                }
 
                if (!beginRemovingTravels
                        && node instanceof ExtrusionNode
                        && ((ExtrusionNode) node).getMovement().isZSet())
                {
                    beginRemovingTravels = true;
                }
            }
        }
        
        for (GCodeEventNode nodeToDelete : travelNodesToDelete)
        {
            nodeToDelete.removeFromParent();
        }
    }
}
