package xyz.openautomaker.base.printerControl;

import xyz.openautomaker.base.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PurgeRequiredException extends PrinterException
{
    public PurgeRequiredException(String loggingMessage)
    {
        super(loggingMessage);
    }
}
