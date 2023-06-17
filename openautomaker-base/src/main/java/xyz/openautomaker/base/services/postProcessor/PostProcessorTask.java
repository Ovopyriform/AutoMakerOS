package xyz.openautomaker.base.services.postProcessor;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.base.configuration.datafileaccessors.HeadContainer;
import xyz.openautomaker.base.configuration.fileRepresentation.HeadFile;
import xyz.openautomaker.base.postprocessor.RoboxiserResult;
import xyz.openautomaker.base.postprocessor.nouveau.PostProcessor;
import xyz.openautomaker.base.postprocessor.nouveau.PostProcessorFeature;
import xyz.openautomaker.base.postprocessor.nouveau.PostProcessorFeatureSet;
import xyz.openautomaker.base.printerControl.PrintJob;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.utils.models.PrintableMeshes;

/**
 *
 * @author Ian
 */
public class PostProcessorTask extends Task<GCodePostProcessingResult>
{

	private static final Logger LOGGER = LogManager.getLogger();

    private final String printJobUUID;
    private final PrintableMeshes printableMeshes;
	private final Path printJobDirectory;
    private final Printer printerToUse;
    private final DoubleProperty taskProgress;
    private final SlicerType slicerType;

    public PostProcessorTask(
            String printJobUUID,
            PrintableMeshes printableMeshes,
			Path printJobDirectory,
            Printer printerToUse,
            DoubleProperty taskProgress,
            SlicerType slicerType)
    {
        this.printJobUUID = printJobUUID;
        this.printableMeshes = printableMeshes;
        this.printJobDirectory = printJobDirectory;
        this.printerToUse = printerToUse;
        this.taskProgress = taskProgress;
        this.slicerType = slicerType;
        updateTitle("Post Processor");
        updateProgress(0.0, 100.0);
    }

    @Override
    protected GCodePostProcessingResult call() throws Exception
    {  
        if (isCancelled())
        {
			LOGGER.debug("Slice cancelled");
            return null;
        }
        
        String headType;
        if (printerToUse != null && printerToUse.headProperty().get() != null)
        {
            headType = printerToUse.headProperty().get().typeCodeProperty().get();
        } else
        {
            headType = HeadContainer.defaultHeadID;
        }

        PrintJob printJob = new PrintJob(printJobUUID, printJobDirectory);
		Path gcodeFileToProcess = printJob.getGCodeFileLocation();
		Path gcodeOutputFile = printJob.getRoboxisedFileLocation();

        GCodePostProcessingResult postProcessingResult = new GCodePostProcessingResult(printJobUUID, gcodeOutputFile, printerToUse, new RoboxiserResult());

        PostProcessorFeatureSet ppFeatures = new PostProcessorFeatureSet();

        HeadFile headFileToUse;
        if (printerToUse == null
                || printerToUse.headProperty().get() == null)
        {
            headFileToUse = HeadContainer.getHeadByID(HeadContainer.defaultHeadID);
            ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
            ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
            ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
            ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
        } else
        {
            headFileToUse = HeadContainer.getHeadByID(printerToUse.headProperty().get().typeCodeProperty().get());
            if (!headFileToUse.getTypeCode().equals("RBX01-SL")
                    && !headFileToUse.getTypeCode().equals("RBX01-DL"))
            {
                ppFeatures.enableFeature(PostProcessorFeature.REMOVE_ALL_UNRETRACTS);
                ppFeatures.enableFeature(PostProcessorFeature.OPEN_AND_CLOSE_NOZZLES);
                ppFeatures.enableFeature(PostProcessorFeature.OPEN_NOZZLE_FULLY_AT_START);
                ppFeatures.enableFeature(PostProcessorFeature.REPLENISH_BEFORE_OPEN);
            }
        }

        if (printableMeshes.isCameraEnabled())
        {
            ppFeatures.enableFeature(PostProcessorFeature.INSERT_CAMERA_CONTROL_POINTS);
        }

        Map<Integer, Integer> objectToNozzleNumberMap = new HashMap<>();
        int objectIndex = 0;

        headFileToUse.getNozzles().get(0).getAssociatedExtruder();
        for (int extruderForModel : printableMeshes.getExtruderForModel())
        {
            Optional<Integer> nozzleForExtruder = headFileToUse.getNozzleNumberForExtruderNumber(extruderForModel);
            if (nozzleForExtruder.isPresent())
            {
                objectToNozzleNumberMap.put(objectIndex, nozzleForExtruder.get());
            } else
            {
				LOGGER.warn("Couldn't get extruder number for object " + objectIndex);
            }
            objectIndex++;
        }
        
        if (isCancelled())
        {
			LOGGER.debug("Slice cancelled");
            return null;
        }
        
        PostProcessor postProcessor = new PostProcessor(
                printJobUUID,
                printableMeshes.getProjectName(),
                printableMeshes.getUsedExtruders(),
                printerToUse,
                gcodeFileToProcess,
                gcodeOutputFile,
                headFileToUse,
                printableMeshes.getSettings(),
                printableMeshes.getPrintOverrides(),
                ppFeatures,
                headType,
                taskProgress,
                objectToNozzleNumberMap,
                printableMeshes.getCameraTriggerData(),
                printableMeshes.isSafetyFeaturesRequired(),
                slicerType);

        RoboxiserResult roboxiserResult = postProcessor.processInput(this);
        if (roboxiserResult.isSuccess())
        {
            roboxiserResult.getPrintJobStatistics().writeStatisticsToFile(printJob.getStatisticsFileLocation());
            postProcessingResult = new GCodePostProcessingResult(printJobUUID, gcodeOutputFile, printerToUse, roboxiserResult);
        }

        return postProcessingResult;
    }
}
