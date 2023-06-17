package xyz.openautomaker.base.postprocessor;

/**
 *
 * @author Ian
 */
public class PostProcessingError extends Exception
{
    public PostProcessingError(String exceptionInformation)
    {
        super(exceptionInformation);
    }
}
