package org.openautomaker.base.postprocessor.nouveau.nodes;

import java.util.Locale;

import org.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class StylusPlungeNode extends GCodeEventNode implements Renderable
{
    private float plungeValue = 0;

    public StylusPlungeNode(float plungeValue)
    {
        this.plungeValue = plungeValue;
    }

    public float getLiftValue()
    {
        return plungeValue;
    }

    public void setLiftValue(float plungeValue)
    {
        this.plungeValue = plungeValue;
    }
    
    @Override
    public String renderForOutput()
    {
        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("G0 ");
        stringToReturn.append(String.format("Z %.2f", plungeValue, Locale.UK));
        stringToReturn.append(' ');
        stringToReturn.append(getCommentText());

        return stringToReturn.toString();
    }
}
