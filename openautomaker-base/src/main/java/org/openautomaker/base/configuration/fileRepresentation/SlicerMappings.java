package org.openautomaker.base.configuration.fileRepresentation;

import java.util.Map;

import org.openautomaker.environment.Slicer;

/**
 *
 * @author Ian
 */
public class SlicerMappings
{

    private Map<Slicer, SlicerMappingData> mappings;

    public Map<Slicer, SlicerMappingData> getMappings()
    {
        return mappings;
    }

    public void setMappings(Map<Slicer, SlicerMappingData> mappings)
    {
        this.mappings = mappings;
    }

    public boolean isMapped(Slicer slicerType, String variable)
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
