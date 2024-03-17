package org.openautomaker.base.postprocessor;

/**
 *
 * @author Ian
 */
public class NotEnoughAvailableExtrusionException extends Exception
{
    public NotEnoughAvailableExtrusionException(String exceptionInformation)
    {
        super(exceptionInformation);
    }
}
