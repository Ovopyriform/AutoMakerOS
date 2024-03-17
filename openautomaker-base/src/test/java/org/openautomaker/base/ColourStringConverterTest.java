
package org.openautomaker.base;

import javafx.scene.paint.Color;

import org.junit.Test;
import org.openautomaker.base.utils.BaseEnvironmentConfiguredTest;

import static org.junit.Assert.*;
import static org.openautomaker.base.utils.ColourStringConverter.colourToString;
import static org.openautomaker.base.utils.ColourStringConverter.stringToColor;

/**
 *
 * @author tony
 */
public class ColourStringConverterTest extends BaseEnvironmentConfiguredTest
{

    @Test
    public void testColourToString()
    {
        Color colour = Color.rgb(0x10, 0x20, 0x30);
        String strColour = colourToString(colour);
        assertEquals("102030", strColour);
    }

    @Test
    public void testStringToColor()
    {
        String colourStr = "102030";
        assertEquals(Color.rgb(0x10, 0x20, 0x30), stringToColor(colourStr));
    }

    @Test
    public void testColourToStringAndBack()
    {
        Color colour = Color.valueOf("0x1ed32fff");
        System.out.println("color is " + colour.toString());
        String strColour = colourToString(colour);
        assertEquals("1ED32F", strColour);
        Color readColor = stringToColor(strColour);
        assertEquals(Color.valueOf("0x1ed32fff"), readColor);
    }

}
