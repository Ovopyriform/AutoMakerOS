package org.openautomaker.base.postprocessor.nouveau.nodes;

import java.util.Locale;

import org.openautomaker.base.postprocessor.nouveau.nodes.providers.Renderable;

/**
 *
 * @author Ian
 */
public class StylusLiftNode extends GCodeEventNode implements Renderable
{
    private float liftValue = 0;

    public StylusLiftNode(float liftValue)
    {
        this.liftValue = liftValue;
    }

    public float getLiftValue()
    {
        return liftValue;
    }

    public void setLiftValue(float liftValue)
    {
        this.liftValue = liftValue;
    }
    
    @Override
    public String renderForOutput()
    {
        StringBuilder stringToReturn = new StringBuilder();

        stringToReturn.append("G0 ");
        stringToReturn.append(String.format("Z %.2f", liftValue, Locale.UK));
        stringToReturn.append(' ');
        stringToReturn.append(getCommentText());

        return stringToReturn.toString();
    }
}
