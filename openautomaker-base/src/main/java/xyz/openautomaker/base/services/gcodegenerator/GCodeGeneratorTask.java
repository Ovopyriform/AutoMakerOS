package xyz.openautomaker.base.services.gcodegenerator;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import xyz.openautomaker.base.MaterialType;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.RoboxProfile;
import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.base.configuration.slicer.Cura4PlusConfigConvertor;
import xyz.openautomaker.base.configuration.slicer.SlicerConfigWriter;
import xyz.openautomaker.base.configuration.slicer.SlicerConfigWriterFactory;
import xyz.openautomaker.base.printerControl.PrintJob;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.services.postProcessor.GCodePostProcessingResult;
import xyz.openautomaker.base.services.postProcessor.PostProcessorTask;
import xyz.openautomaker.base.services.slicer.PrintQualityEnumeration;
import xyz.openautomaker.base.services.slicer.ProgressReceiver;
import xyz.openautomaker.base.services.slicer.SliceResult;
import xyz.openautomaker.base.services.slicer.SlicerTask;
import xyz.openautomaker.base.services.slicer.SlicerUtils;
import xyz.openautomaker.base.utils.models.PrintableMeshes;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Tony Aldhous
 */
public class GCodeGeneratorTask extends Task<GCodeGeneratorResult> implements ProgressReceiver {
	private static final Logger LOGGER = LogManager.getLogger();

	private Printer printerToUse = null;
	private PrintableMeshes meshesToUse = null;
	private PrintableMeshes meshesToPrint = null;
	private Path gcodePath = null;

	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	/**
	 *
	 */
	public GCodeGeneratorTask() {
	}

	/**
	 *
	 * @param printerToUse
	 * @param meshSupplier
	 * @param gcodePath
	 * 
	 */
	public void initialise(Printer printerToUse, Supplier<PrintableMeshes> meshSupplier, Path gcodePath) {
		this.printerToUse = printerToUse;
		this.meshesToUse = meshSupplier.get();
		this.gcodePath = gcodePath;
		updateProgress(0.0, 100.0);
		this.updateMessage("Preparing to slice ...");
	}

	@Override
	protected GCodeGeneratorResult call() {
		GCodeGeneratorResult result = new GCodeGeneratorResult();

		if (isCancelled()) {
			result.setCancelled(true);
			return result;
		}

		prepareSettingsForSlicing();
		updateProgress(10.0, 100.0);
		if (isCancelled()) {
			result.setCancelled(true);
			return result;
		}
		updateMessage(OpenAutoMakerEnv.getI18N().t("printerStatus.slicing"));
		PrintJob printJob = new PrintJob(meshesToPrint.getPrintQuality().getFriendlyName(), gcodePath);
		Path slicerOutputFilePath = printJob.getGCodeFileLocation();
		Path postProcOutputFilePath = printJob.getRoboxisedFileLocation();

		SlicerTask slicerTask = new SlicerTask(meshesToPrint.getPrintQuality().getFriendlyName(), meshesToPrint, gcodePath.toString(), printerToUse, this);
		executorService.execute(slicerTask);

		SliceResult slicerResult = null;
		try {
			slicerResult = slicerTask.get();
		} catch (InterruptedException ex) {
			LOGGER.debug("GCode Generation interrupted, probably due to a cancel");
			LOGGER.debug("Cancelling Slicer Task");
			slicerTask.cancel(false);
			LOGGER.debug("Killing Slicer");
			SlicerUtils.killSlicing(meshesToPrint.getDefaultSlicerType());
		} catch (ExecutionException ex) {
			LOGGER.warn("Slicing task failed with exception " + ex);
			SlicerUtils.killSlicing(meshesToPrint.getDefaultSlicerType());
		}

		result.setSlicerResult(slicerResult, slicerOutputFilePath.toString());
		updateProgress(60.0, 100.0);

		if (isCancelled()) {
			result.setCancelled(true);
			return result;
		}

		if (slicerResult != null && slicerResult.isSuccess()) {
			updateMessage(OpenAutoMakerEnv.getI18N().t("printerStatus.postProcessing"));
			DoubleProperty progress = new SimpleDoubleProperty();
			progress.addListener((n, ov, nv) -> this.updateProgress(60.0 + 0.4 * nv.doubleValue(), 100.0));

			PostProcessorTask postProcessorTask = new PostProcessorTask(meshesToPrint.getPrintQuality().getFriendlyName(), meshesToPrint, gcodePath, printerToUse, progress, meshesToPrint.getDefaultSlicerType());
			executorService.execute(postProcessorTask);

			GCodePostProcessingResult postProcessingResult = null;
			try {
				postProcessingResult = postProcessorTask.get();
			} catch (InterruptedException ex) {
				LOGGER.debug("GCode Generation interrupted, probably due to a cancel");
				LOGGER.debug("Cancelling Post Processor");
				postProcessorTask.cancel(false);
			} catch (ExecutionException ex) {
				LOGGER.warn("Post Processor task failed with exception " + ex);
			}

			result.setPostProcessingResult(postProcessingResult, postProcOutputFilePath.toString());
		}

		if (isCancelled()) {
			result.setCancelled(true);
			return result;
		}

		updateMessage("Done");
		updateProgress(100.0, 100.0);

		return result;
	}

	private void prepareSettingsForSlicing() {
		RoboxProfile settingsToUse = new RoboxProfile(meshesToUse.getSettings());

		SlicerType slicerTypeToUse = meshesToUse.getDefaultSlicerType();

		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(slicerTypeToUse);

		if (printerToUse != null) {
			// This is a hack to force the fan speed to 100% when using PLA
			if (printerToUse.reelsProperty().containsKey(0)) {
				if (printerToUse.reelsProperty().get(0).materialProperty().get() == MaterialType.PLA) {
					settingsToUse.addOrOverride("enableCooling", "true");
					settingsToUse.addOrOverride("minFanSpeed_percent", "100");
					settingsToUse.addOrOverride("maxFanSpeed_percent", "100");
				}
			}

			if (printerToUse.reelsProperty().containsKey(1)) {
				if (printerToUse.reelsProperty().get(1).materialProperty().get() == MaterialType.PLA) {
					settingsToUse.addOrOverride("enableCooling", "true");
					settingsToUse.addOrOverride("minFanSpeed_percent", "100");
					settingsToUse.addOrOverride("maxFanSpeed_percent", "100");
				}
			}
			// End of hack

			// Hack to change raft related settings for Draft ABS prints
			if (meshesToUse.getPrintQuality() == PrintQualityEnumeration.DRAFT && ((printerToUse.effectiveFilamentsProperty().get(0) != null && printerToUse.effectiveFilamentsProperty().get(0).getMaterial() == MaterialType.ABS)
					|| (printerToUse.effectiveFilamentsProperty().get(1) != null && printerToUse.effectiveFilamentsProperty().get(0).getMaterial() == MaterialType.ABS))) {
				settingsToUse.addOrOverride("raftBaseLinewidth_mm", "1.250");
				settingsToUse.addOrOverride("raftAirGapLayer0_mm", "0.285");
				settingsToUse.addOrOverride("interfaceLayers", "1");
			}

			// Hack to change raft related settings for Normal ABS prints
			if (meshesToUse.getPrintQuality() == PrintQualityEnumeration.NORMAL && ((printerToUse.effectiveFilamentsProperty().get(0) != null && printerToUse.effectiveFilamentsProperty().get(0).getMaterial() == MaterialType.ABS)
					|| (printerToUse.effectiveFilamentsProperty().get(1) != null && printerToUse.effectiveFilamentsProperty().get(1).getMaterial() == MaterialType.ABS))) {
				settingsToUse.addOrOverride("raftAirGapLayer0_mm", "0.4");
			}
			// End of hack
		}

		// Create a new set of meshes with the updated settings. 
		meshesToPrint = new PrintableMeshes(meshesToUse.getMeshesForProcessing(), meshesToUse.getUsedExtruders(), meshesToUse.getExtruderForModel(), meshesToUse.getProjectName(), meshesToUse.getRequiredPrintJobID(), settingsToUse,
				meshesToUse.getPrintOverrides(), meshesToUse.getPrintQuality(), meshesToUse.getDefaultSlicerType(), meshesToUse.getCentreOfPrintedObject(), meshesToUse.isSafetyFeaturesRequired(), meshesToUse.isCameraEnabled(),
				meshesToUse.getCameraTriggerData());

		configWriter.setPrintCentre((float) (meshesToUse.getCentreOfPrintedObject().getX()), (float) (meshesToUse.getCentreOfPrintedObject().getZ()));

		Path configFilePath = gcodePath.resolve(meshesToUse.getPrintQuality() + BaseConfiguration.printProfileFileExtension);

		configWriter.generateConfigForSlicer(settingsToUse, configFilePath);

		if (slicerTypeToUse != SlicerType.Cura) {
			Cura4PlusConfigConvertor cura4ConfigConvertor = new Cura4PlusConfigConvertor(printerToUse, meshesToPrint, slicerTypeToUse);
			cura4ConfigConvertor.injectConfigIntoCura4SettingsFile(configFilePath, gcodePath);
		}
	}

	@Override
	public void progressUpdateFromSlicer(String message, float workDone) {
		updateProgress(10.0 + 0.5 * workDone, 100.0);
	}
}
