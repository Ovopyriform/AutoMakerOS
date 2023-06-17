package xyz.openautomaker.base.configuration.fileRepresentation;

import java.util.Map;

import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author Ian
 */
public class SlicerMappings
{

    private Map<SlicerType, SlicerMappingData> mappings;

    public Map<SlicerType, SlicerMappingData> getMappings()
    {
        return mappings;
    }

    public void setMappings(Map<SlicerType, SlicerMappingData> mappings)
    {
        this.mappings = mappings;
    }

    public boolean isMapped(SlicerType slicerType, String variable)
    {
        boolean isMapped = false;
        for (String formula : mappings.get(slicerType).getMappingData().values())
        {
            String[] elements = formula.split(":");
            if (elements.length == 0 && formula.equals(variable))
            {
                isMapped = true;
                break;
            } else if (elements[0].equals(variable))
            {
                isMapped = true;
                break;
            }
        }
        return isMapped;
    }
}
