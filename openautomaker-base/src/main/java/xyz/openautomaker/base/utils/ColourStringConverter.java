/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.paint.Color;
import xyz.openautomaker.base.PrinterColourMap;

/**
 *
 * @author tony
 */
public class ColourStringConverter
{

	private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Convert the RGB elements of a colour to a UTF8 string eg "102030". If null is passed then return the value for Color.WHITE.
     */
    public static String colourToString(Color colour)
    {
        if (colour == null)
        {
            colour = Color.WHITE;
        }
        int redValue = (int) (255 * colour.getRed());
        String redString = String.format("%02X", redValue);
        int greenValue = (int) (255 * colour.getGreen());
        String greenString = String.format("%02X", greenValue);
        int blueValue = (int) (255 * colour.getBlue());
        String blueString = String.format("%02X", blueValue);
        return redString + greenString + blueString;
    }

    public static Color stringToColor(String string)
    {
        Color colour;
        try
        {
            String redDigits = string.substring(0, 2);
            int redIntValue = Integer.parseInt(redDigits, 16);
            double redValue = (double) redIntValue / 255;

            String greenDigits = string.substring(2, 4);
            int greenIntValue = Integer.parseInt(greenDigits, 16);
            double greenValue = (double) greenIntValue / 255;

            String blueDigits = string.substring(4, 6);
            int blueIntValue = Integer.parseInt(blueDigits, 16);
            double blueValue = (double) blueIntValue / 255;

            colour = new Color(redValue, greenValue, blueValue, 1);
        } catch (NumberFormatException ex)
        {
			LOGGER.error("Failed to convert colour information");
            colour = PrinterColourMap.getInstance().getPrinterColours().get(0);
        }
        return colour;
    }

}
