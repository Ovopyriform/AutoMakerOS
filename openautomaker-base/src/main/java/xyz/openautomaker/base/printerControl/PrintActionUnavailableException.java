package xyz.openautomaker.base.printerControl;

import xyz.openautomaker.base.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PrintActionUnavailableException extends PrinterException
{
    public PrintActionUnavailableException(String loggingMessage)
    {
        super(loggingMessage);
    }
}
