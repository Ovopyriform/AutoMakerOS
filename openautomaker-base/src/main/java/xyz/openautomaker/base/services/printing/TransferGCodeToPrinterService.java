package xyz.openautomaker.base.services.printing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import xyz.openautomaker.base.configuration.fileRepresentation.CameraSettings;
import xyz.openautomaker.base.postprocessor.PrintJobStatistics;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.services.ControllableService;

/**
 *
 * @author ianhudson
 */
public class TransferGCodeToPrinterService extends Service<GCodePrintResult> implements ControllableService
{

    private Printer printerToUse = null;
    private final StringProperty modelFileToPrint = new SimpleStringProperty();
    private final StringProperty currentPrintJobID = new SimpleStringProperty();
    private final IntegerProperty linesInGCodeFile = new SimpleIntegerProperty(1);

	private static final Logger LOGGER = LogManager.getLogger();

    private boolean printUsingSDCard = true;
    private int startFromSequenceNumber = 0;
    private boolean canBeReprinted = true;
    private boolean dontInitiatePrint = false;
    private PrintJobStatistics printJobStatistics = null;
    private CameraSettings cameraData = null;

    /**
     *
     * @param printerToUse
     */
    public void setPrinterToUse(Printer printerToUse)
    {
        this.printerToUse = printerToUse;
    }

    private final Printer getPrinterToUse()
    {
        return printerToUse;
    }

    /**
     *
     * @param value
     */
    public final void setModelFileToPrint(String value)
    {
        modelFileToPrint.set(value);
    }

    /**
     *
     * @return
     */
    public final String getModelFileToPrint()
    {
        return modelFileToPrint.get();
    }

    /**
     *
     * @return
     */
    public final StringProperty modelFileToPrintProperty()
    {
        return modelFileToPrint;
    }

    /**
     *
     * @param value
     */
    public final void setCurrentPrintJobID(String value)
    {
        currentPrintJobID.set(value);
    }

    /**
     *
     * @return
     */
    public final String getCurrentPrintJobID()
    {
        return currentPrintJobID.get();
    }

    /**
     *
     * @return
     */
    public final StringProperty currentPrintJobIDProperty()
    {
        return currentPrintJobID;
    }

    /**
     *
     * @param value
     */
    public final void setLinesInGCodeFile(int value)
    {
        linesInGCodeFile.set(value);
    }

    /**
     *
     * @return
     */
    public final int getLinesInGCodeFile()
    {
        return linesInGCodeFile.get();
    }

    /**
     *
     * @return
     */
    public final IntegerProperty linesInGCodeFileProperty()
    {
        return linesInGCodeFile;
    }

    /**
     *
     * @param useSDCard
     */
    public void setPrintUsingSDCard(boolean useSDCard)
    {
        printUsingSDCard = useSDCard;
    }

    /**
     *
     * @param dontInitiatePrint
     */
    public void dontInitiatePrint(boolean dontInitiatePrint)
    {
        this.dontInitiatePrint = dontInitiatePrint;
    }

    public void setPrintJobStatistics(PrintJobStatistics printJobStatistics)
    {
        this.printJobStatistics = printJobStatistics;
    }

    public void setCameraData(CameraSettings cameraData)
    {
        this.cameraData = cameraData;
    }

    @Override
    protected Task<GCodePrintResult> createTask()
    {
        return new TransferGCodeToPrinterTask(getPrinterToUse(),
                                              getModelFileToPrint(),
                                              getCurrentPrintJobID(),
                                              linesInGCodeFileProperty(),
                                              printUsingSDCard,
                                              startFromSequenceNumber,
                                              canBeReprinted,
                                              dontInitiatePrint,
                                              printJobStatistics,
                                              cameraData);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean cancelRun()
    {
		LOGGER.info("Print transfer service cancelled - job " + getCurrentPrintJobID());
        return cancel();
    }

    @Override
    public void reset()
    {
        if (stateProperty().get() == State.SUCCEEDED
                || stateProperty().get() == State.FAILED
                || stateProperty().get() == State.CANCELLED
                || stateProperty().get() == State.READY)
        {
            super.reset();
            printerToUse = null;
            modelFileToPrint.setValue("");
            currentPrintJobID.setValue("");
            linesInGCodeFile.setValue(0);
            printUsingSDCard = true;
            startFromSequenceNumber = 0;
            canBeReprinted = true;
            dontInitiatePrint = false;
            printJobStatistics = null;
        }
    }

    public void setStartFromSequenceNumber(int startFromSequenceNumber)
    {
        this.startFromSequenceNumber = startFromSequenceNumber;
    }

    public void setThisCanBeReprinted(boolean thisJobCanBeReprinted)
    {
        canBeReprinted = thisJobCanBeReprinted;
    }
}
