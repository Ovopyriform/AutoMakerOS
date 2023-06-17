package xyz.openautomaker.base.services.postProcessor;

import java.nio.file.Path;

import xyz.openautomaker.base.postprocessor.RoboxiserResult;
import xyz.openautomaker.base.printerControl.model.Printer;

/**
 *
 * @author ianhudson
 */
public class GCodePostProcessingResult
{
    private String printJobUUID = null;
	private Path outputFilename = null;
    private Printer printerToUse = null;
    private RoboxiserResult result = null;

    /**
     *
     * @param printJobUUID
     * @param outputFilename
     * @param printerToUse
     * @param result
     */
	public GCodePostProcessingResult(String printJobUUID, Path outputFilename, Printer printerToUse, RoboxiserResult result)
    {
        this.printJobUUID = printJobUUID;
        this.outputFilename = outputFilename;
        this.printerToUse = printerToUse;
        this.result = result;
    }

    /**
     *
     * @return
     */
    public String getPrintJobUUID()
    {
        return printJobUUID;
    }

    /**
     *
     * @return
     */
	public Path getOutputFilename()
    {
        return outputFilename;
    }

    /**
     *
     * @return
     */
    public Printer getPrinterToUse()
    {
        return printerToUse;
    }

    /**
     *
     * @return
     */
    public RoboxiserResult getRoboxiserResult()
    {
        return result;
    }

    /**
     *
     * @param result
     */
    public void setRoboxiserResult(RoboxiserResult result)
    {
        this.result = result;
    }
}
