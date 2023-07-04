

package celtech.roboxbase.comms;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class PrinterID
{
    private String printerID = "";

    /**
     *
     */
    public PrinterID()
    {
    }

    /**
     *
     * @return
     */
    public String getPrinterID()
    {
        return printerID;
    }

    /**
     *
     * @param printerID
     */
    public void setPrinterID(String printerID)
    {
        this.printerID = printerID;
    }
    
    /**
     *
     * @return
     */
    public String toString()
    {
        return printerID;
    }
}
