package org.openautomaker.base.printerControl;

import org.openautomaker.base.printerControl.model.PrinterException;

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
