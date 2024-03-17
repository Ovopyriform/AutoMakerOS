package org.openautomaker.base.printerControl;

import org.openautomaker.base.printerControl.model.PrinterException;

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
