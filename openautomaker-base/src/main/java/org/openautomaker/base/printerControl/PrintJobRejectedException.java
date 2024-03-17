package org.openautomaker.base.printerControl;

import org.openautomaker.base.printerControl.model.PrinterException;

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
