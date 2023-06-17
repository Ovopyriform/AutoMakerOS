package xyz.openautomaker.base.printerControl;

import xyz.openautomaker.base.printerControl.model.PrinterException;

/**
 *
 * @author Ian
 */
public class PrintJobRejectedException extends PrinterException
{

    public PrintJobRejectedException(String loggingMessage)
    {
        super(loggingMessage);
    }
    
}
