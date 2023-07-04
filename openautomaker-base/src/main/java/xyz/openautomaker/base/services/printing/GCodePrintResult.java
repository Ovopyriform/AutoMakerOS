
package xyz.openautomaker.base.services.printing;

/**
 *
 * @author Ian
 */
public class GCodePrintResult
{
    private boolean success = false;
    private String printJobID = null;

    /**
     *
     * @return
     */
    public boolean isSuccess()
    {
        return success;
    }

    /**
     *
     * @param success
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     *
     * @return
     */
    public String getPrintJobID()
    {
        return printJobID;
    }

    /**
     *
     * @param printJobID
     */
    public void setPrintJobID(String printJobID)
    {
        this.printJobID = printJobID;
    }
}
