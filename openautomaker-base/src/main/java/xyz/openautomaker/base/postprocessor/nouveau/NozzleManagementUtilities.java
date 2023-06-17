package xyz.openautomaker.base.postprocessor.nouveau;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import xyz.openautomaker.base.configuration.RoboxProfile;
import xyz.openautomaker.base.configuration.fileRepresentation.HeadFile;
import xyz.openautomaker.base.postprocessor.NozzleProxy;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.FillSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.GCodeEventNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.InnerPerimeterSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.LayerNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.NozzleValvePositionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.OuterPerimeterSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.SectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.SkinSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.SkirtSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.SupportInterfaceSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.SupportSectionNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.ToolSelectNode;
import xyz.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class NozzleManagementUtilities
{

    private final List<NozzleProxy> nozzleProxies;
    private final RoboxProfile settingsProfile;
    private final HeadFile headFile;

    public NozzleManagementUtilities(List<NozzleProxy> nozzleProxies,
            RoboxProfile settingsProfile,
            HeadFile headFile)
    {
        this.nozzleProxies = nozzleProxies;
        this.settingsProfile = settingsProfile;
        this.headFile = headFile;
    }

    protected NozzleProxy chooseNozzleProxyForDifferentialSupportMaterial(final GCodeEventNode node,
            final NozzleProxy supportMaterialNozzle,
            final NozzleProxy nozzleForCurrentObject) throws UnableToFindSectionNodeException
    {
        NozzleProxy nozzleProxy = null;

        //Go up through the parents until we either reach the top or find a section node
        GCodeEventNode foundNode = null;
        GCodeEventNode searchNode = node;

        do
        {
            if (searchNode instanceof SectionNode)
            {
                foundNode = searchNode;
                break;
            } else
            {
                if (searchNode.hasParent())
                {
                    searchNode = searchNode.getParent().get();
                }
            }
        } while (searchNode.hasParent());

        if (foundNode == null)
        {
            String outputMessage;
            if (node instanceof Renderable)
            {
                outputMessage = "Unable to find section parent of " + ((Renderable) node).renderForOutput();
            } else
            {
                outputMessage = "Unable to find section parent of " + node.toString();
            }
            throw new UnableToFindSectionNodeException(outputMessage);
        }

        if (foundNode instanceof SupportSectionNode
                || foundNode instanceof SupportInterfaceSectionNode
                || foundNode instanceof SkirtSectionNode)
        {
            nozzleProxy = supportMaterialNozzle;
        } else
        {
            nozzleProxy = nozzleForCurrentObject;
        }

        return nozzleProxy;
    }

    protected NozzleProxy chooseNozzleProxyByTask(final GCodeEventNode node) throws UnableToFindSectionNodeException
    {
        NozzleProxy nozzleProxy = null;

        //Go up through the parents until we either reach the top or find a section node
        GCodeEventNode foundNode = null;
        GCodeEventNode searchNode = node;

        do
        {
            if (searchNode instanceof SectionNode)
            {
                foundNode = searchNode;
                break;
            } else
            {
                if (searchNode.hasParent())
                {
                    searchNode = searchNode.getParent().get();
                }
            }
        } while (searchNode.hasParent());

        if (foundNode == null)
        {
            String outputMessage;
            if (node instanceof Renderable)
            {
                outputMessage = "Unable to find section parent of " + ((Renderable) node).renderForOutput();
            } else
            {
                outputMessage = "Unable to find section parent of " + node.toString();
            }
            throw new UnableToFindSectionNodeException(outputMessage);
        }

        if (foundNode instanceof FillSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        } else if (foundNode instanceof OuterPerimeterSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("perimeterNozzle"));
        } else if (foundNode instanceof InnerPerimeterSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("perimeterNozzle"));
        } else if (foundNode instanceof SupportSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("supportNozzle"));
        } else if (foundNode instanceof SupportInterfaceSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("supportInterfaceNozzle"));
        } else if (foundNode instanceof SkinSectionNode)
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        } else
        {
            nozzleProxy = nozzleProxies.get(settingsProfile.getSpecificIntSetting("fillNozzle"));
        }

        return nozzleProxy;
    }

    protected Optional<NozzleProxy> determineNozzleStateAtEndOfLayer(LayerNode layerNode)
    {
        Optional<NozzleProxy> nozzleInUse = Optional.empty();

        Iterator<GCodeEventNode> layerIterator = layerNode.childBackwardsIterator();

        search:
        while (layerIterator.hasNext())
        {
            GCodeEventNode potentialToolSelectNode = layerIterator.next();

            if (potentialToolSelectNode instanceof ToolSelectNode)
            {
                ToolSelectNode lastToolSelect = (ToolSelectNode) potentialToolSelectNode;

                Iterator<GCodeEventNode> toolSelectChildIterator = lastToolSelect.childrenAndMeBackwardsIterator();
                while (toolSelectChildIterator.hasNext())
                {
                    GCodeEventNode potentialNozzleValvePositionNode = toolSelectChildIterator.next();

                    if (potentialNozzleValvePositionNode instanceof NozzleValvePositionNode)
                    {
                        NozzleValvePositionNode nozzleNode = (NozzleValvePositionNode) potentialNozzleValvePositionNode;
                        NozzleProxy proxy = nozzleProxies.get(lastToolSelect.getToolNumber());
                        proxy.setCurrentPosition(nozzleNode.getNozzlePosition().getB());
                        nozzleInUse = Optional.of(proxy);
                        break search;
                    }
                }
            }
        }

        return nozzleInUse;
    }
}
