package org.openautomaker.base.services.slicer;

import static org.openautomaker.environment.OpenAutomakerEnv.CURA_ENGINE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.BaseConfiguration;
import org.openautomaker.base.printerControl.model.Head;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.utils.TimeUtils;
import org.openautomaker.base.utils.exporters.MeshExportResult;
import org.openautomaker.base.utils.exporters.MeshFileOutputConverter;
import org.openautomaker.base.utils.exporters.STLOutputConverter;
import org.openautomaker.base.utils.models.PrintableMeshes;
import org.openautomaker.environment.MachineType;
import org.openautomaker.environment.OpenAutomakerEnv;
import org.openautomaker.environment.Slicer;

import javafx.concurrent.Task;

/**
 *
 * @author ianhudson
 */
public class SlicerTask extends Task<SliceResult> implements ProgressReceiver {
	private static final Logger LOGGER = LogManager.getLogger();

	private static final TimeUtils TIME_UTILS = new TimeUtils();
	private static final String SLICER_TIMER_NAME = "Slicer";

	private final String printJobUUID;
	private final PrintableMeshes printableMeshes;
	private final String printJobDirectory;
	private final Printer printerToUse;
	private final ProgressReceiver progressReceiver;

	public SlicerTask(String printJobUUID,
			PrintableMeshes printableMeshes,
			String printJobDirectory,
			Printer printerToUse,
			ProgressReceiver progressReceiver) {
		this.printJobUUID = printJobUUID;
		this.printableMeshes = printableMeshes;
		this.printJobDirectory = printJobDirectory;
		this.printerToUse = printerToUse;
		this.progressReceiver = progressReceiver;
		updateProgress(0.0, 100.0);
	}

	@Override
	protected SliceResult call() throws Exception {
		if (isCancelled()) {
			LOGGER.debug("Slice cancelled");
			return null;
		}

		LOGGER.debug("slice " + printableMeshes.getSettings().getName());
		updateTitle("Slicer");
		updateMessage("Preparing model for conversion");
		updateProgress(0.0, 100.0);

		LOGGER.debug("Starting slicing");
		String timerUUID = UUID.randomUUID().toString();
		TIME_UTILS.timerStart(timerUUID, SLICER_TIMER_NAME);

		Slicer slicerType = printableMeshes.getDefaultSlicerType();

		MeshFileOutputConverter outputConverter = null;

		outputConverter = new STLOutputConverter();

		MeshExportResult meshExportResult = null;

		// Output multiple files if we are using Cura
		if (printerToUse == null
				|| printerToUse.headProperty().get() == null
				|| printerToUse.headProperty().get().headTypeProperty().get() == Head.HeadType.SINGLE_MATERIAL_HEAD) {
			meshExportResult = outputConverter.outputFile(printableMeshes.getMeshesForProcessing(), printJobUUID, Paths.get(printJobDirectory),
					true);
		}
		else {
			meshExportResult = outputConverter.outputFile(printableMeshes.getMeshesForProcessing(), printJobUUID, Paths.get(printJobDirectory),
					false);
		}

		if (isCancelled()) {
			LOGGER.debug("Slice cancelled");
			return null;
		}

		Vector3D centreOfPrintedObject = meshExportResult.getCentre();

		boolean succeeded = sliceFile(printJobUUID,
				printJobDirectory,
				slicerType,
				meshExportResult.getCreatedFiles(),
				printableMeshes.getExtruderForModel(),
				centreOfPrintedObject,
				progressReceiver,
				printableMeshes.getNumberOfNozzles());

		try {
			TIME_UTILS.timerStop(timerUUID, SLICER_TIMER_NAME);
			LOGGER.debug("Slicer Timer Report");
			LOGGER.debug("============");
			LOGGER.debug(SLICER_TIMER_NAME + " " + 0.001 * TIME_UTILS.timeTimeSoFar_ms(timerUUID, SLICER_TIMER_NAME) + " seconds");
			LOGGER.debug("============");
			TIME_UTILS.timerDelete(timerUUID, SLICER_TIMER_NAME);
		}
		catch (TimeUtils.TimerNotFoundException ex) {
			// This really should not happen!
			LOGGER.debug("Slicer Timer Report - timer not found!");
		}

		return new SliceResult(printJobUUID, printableMeshes, printerToUse, succeeded);
	}

	private boolean sliceFile(String printJobUUID,
			String printJobDirectory,
			Slicer slicerType,
			List<String> createdMeshFiles,
			List<Integer> extrudersForMeshes,
			Vector3D centreOfPrintedObject,
			ProgressReceiver progressReceiver,
			int numberOfNozzles) {
		// Heads with a single nozzle are anomalous because
		// tool zero uses the "E" extruder, which is usually
		// extruder number 1. So for these kinds of head, the
		// extruder number needs to be reset to 0, hence the
		// need for the numberOfNozzles parameter.
		// This hack is closely related to the hack in
		// CuraDefaultSettingsEditor that also sets the extruder
		// number to zero for single nozzle heads.

		boolean succeeded = false;

		String tempGcodeFilename = printJobUUID + BaseConfiguration.gcodeTempFileExtension;

		String configFile = printJobUUID + BaseConfiguration.printProfileFileExtension;
		String jsonSettingsFile = "fdmprinter_robox.def.json";

		MachineType machineType = OpenAutomakerEnv.get().getMachineType();
		ArrayList<String> commands = new ArrayList<>();

		String windowsSlicerCommand = "";
		String macSlicerCommand = "";
		String linuxSlicerCommand = "";
		String configLoadCommand = "";
		String configLoadFile = "";
		//The next variable is only required for Cura4
		String actionCommand = "";
		//The next variable is only required for Slic3r
		String printCenterCommand = "";
		String verboseOutputCommand = "";
		String progressOutputCommand = "";
		String modelFileCommand = "";
		String extruderTrainCommand = "";
		String settingCommand = "-s";
		String extruderSettingFormat = "extruder_nr=%d";

		windowsSlicerCommand = "\"" + OpenAutomakerEnv.get().getApplicationPath(CURA_ENGINE).resolve(slicerType.getPathModifier()).resolve("CuraEngine.exe").toString() + "\"";
		macSlicerCommand = slicerType.getPathModifier().resolve("CuraEngine").toString();
		linuxSlicerCommand = slicerType.getPathModifier().resolve("CuraEngine").toString();

		switch (slicerType) {
			case CURA:
				verboseOutputCommand = "-v";
				configLoadCommand = "-c";
				configLoadFile = configFile;
				progressOutputCommand = "-p";
				break;
			case CURA_4:
			case CURA_5:
				actionCommand = "slice";
				verboseOutputCommand = "-v";
				configLoadCommand = "-j";
				configLoadFile = jsonSettingsFile;
				progressOutputCommand = "-p";
				modelFileCommand = "-l";
				extruderTrainCommand = "-e";
				break;
		}

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Selected slicer is " + slicerType + ": " + Thread.currentThread().getName());

		int previousExtruder;
		int extruderNo;
		switch (machineType) {
			//            case WINDOWS_95:
			//                commands.add("command.com");
			//                commands.add("/S");
			//                commands.add("/C");
			//                String win95PrintCommand = "\"pushd \""
			//                        + printJobDirectory
			//                        + "\" && "
			//                        + windowsSlicerCommand
			//                        + " ";
			//
			//                if (!actionCommand.isEmpty())
			//                    win95PrintCommand += actionCommand + " ";
			//
			//                win95PrintCommand += verboseOutputCommand
			//                        + " "
			//                        + progressOutputCommand
			//                        + " "
			//                        + configLoadCommand
			//                        + " \"" + configLoadFile + "\""
			//                        + " -o "
			//                        + "\"" + tempGcodeFilename + "\"";
			//
			//				for (String fileName : createdMeshFiles) {
			//                    win95PrintCommand += " \"";
			//                    win95PrintCommand += fileName;
			//                    win95PrintCommand += "\"";
			//                }
			//
			//                win95PrintCommand += " && popd\"";
			//                commands.add(win95PrintCommand);
			//                break;
			case WINDOWS:
				commands.add("cmd.exe");
				commands.add("/S");
				commands.add("/C");
				String windowsPrintCommand = "\"pushd \""
						+ printJobDirectory
						+ "\" && "
						+ windowsSlicerCommand
						+ " ";

				if (!actionCommand.isEmpty())
					windowsPrintCommand += actionCommand + " ";

				windowsPrintCommand += verboseOutputCommand
						+ " "
						+ progressOutputCommand
						+ " "
						+ configLoadCommand
						+ " \"" + configLoadFile + "\"";

				windowsPrintCommand += " -o "
						+ "\"" + tempGcodeFilename + "\"";

				if (!printCenterCommand.isEmpty()) {
					windowsPrintCommand += " " + printCenterCommand;
					windowsPrintCommand += " "
							+ String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
							+ ","
							+ String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ());
				}

				previousExtruder = -1;
				extruderNo = 0;
				for (int i = 0; i < createdMeshFiles.size(); i++) {
					if (slicerType != Slicer.CURA && previousExtruder != extrudersForMeshes.get(i)) {
						if (numberOfNozzles > 1) {
							// Extruder needs swapping... just because
							extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
						}

						windowsPrintCommand += " " + extruderTrainCommand + extruderNo;
					}
					windowsPrintCommand += " " + modelFileCommand;
					windowsPrintCommand += " \"";
					windowsPrintCommand += createdMeshFiles.get(i);
					windowsPrintCommand += "\"";

					if (slicerType != Slicer.CURA) {
						windowsPrintCommand += " " + settingCommand;
						windowsPrintCommand += " " + String.format(extruderSettingFormat, extruderNo);
					}

					previousExtruder = extrudersForMeshes.get(i);
				}
				windowsPrintCommand += " && popd\"";
				LOGGER.debug(windowsPrintCommand);
				commands.add(windowsPrintCommand);
				break;
			case MAC:
				commands.add(OpenAutomakerEnv.get().getApplicationPath(CURA_ENGINE).resolve(macSlicerCommand).toString());

				if (!actionCommand.isEmpty())
					commands.add(actionCommand);

				if (!verboseOutputCommand.isEmpty())
					commands.add(verboseOutputCommand);

				if (!progressOutputCommand.isEmpty())
					commands.add(progressOutputCommand);

				commands.add(configLoadCommand);
				commands.add(configLoadFile);
				commands.add("-o");
				commands.add(tempGcodeFilename);

				if (!printCenterCommand.isEmpty()) {
					commands.add(printCenterCommand);
					commands.add(String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
							+ ","
							+ String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ()));
				}

				previousExtruder = -1;
				extruderNo = 0;
				for (int i = 0; i < createdMeshFiles.size(); i++) {
					if (slicerType != Slicer.CURA && previousExtruder != extrudersForMeshes.get(i)) {
						if (numberOfNozzles > 1) {
							// Extruder needs swapping... just because
							extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
						}
						commands.add(extruderTrainCommand + extruderNo);
					}

					if (!modelFileCommand.isEmpty())
						commands.add(modelFileCommand);

					commands.add(createdMeshFiles.get(i));

					if (slicerType != Slicer.CURA) {
						commands.add(settingCommand);
						commands.add(String.format(extruderSettingFormat, extruderNo));
					}

					previousExtruder = extrudersForMeshes.get(i);
				}

				break;
			case LINUX:
				commands.add(OpenAutomakerEnv.get().getApplicationPath(CURA_ENGINE).resolve(linuxSlicerCommand).toString());
				if (!actionCommand.isEmpty())
					commands.add(actionCommand);
				if (!verboseOutputCommand.isEmpty()) {
					commands.add(verboseOutputCommand);
				}
				if (!progressOutputCommand.isEmpty()) {
					commands.add(progressOutputCommand);
				}
				commands.add(configLoadCommand);
				commands.add(configLoadFile);
				commands.add("-o");
				commands.add(tempGcodeFilename);
				if (!printCenterCommand.isEmpty()) {
					commands.add(printCenterCommand);
					commands.add(String.format(Locale.UK, "%.3f", centreOfPrintedObject.getX())
							+ ","
							+ String.format(Locale.UK, "%.3f", centreOfPrintedObject.getZ()));
				}
				previousExtruder = -1;
				extruderNo = 0;
				for (int i = 0; i < createdMeshFiles.size(); i++) {
					if (slicerType != Slicer.CURA && previousExtruder != extrudersForMeshes.get(i)) {
						if (numberOfNozzles > 1) {
							// Extruder needs swapping... just because
							extruderNo = extrudersForMeshes.get(i) > 0 ? 0 : 1;
						}
						commands.add(extruderTrainCommand + extruderNo);
					}
					if (!modelFileCommand.isEmpty())
						commands.add(modelFileCommand);
					commands.add(createdMeshFiles.get(i));

					if (slicerType != Slicer.CURA) {
						commands.add(settingCommand);
						commands.add(String.format(extruderSettingFormat, extruderNo));
					}

					previousExtruder = extrudersForMeshes.get(i);
				}
				break;
			default:
				LOGGER.error("Couldn't determine how to run slicer");
		}

		if (commands.size() > 0) {
			// LOGGER.debug("Slicer command is " + String.join(" ", commands));
			ProcessBuilder slicerProcessBuilder = new ProcessBuilder(commands);
			if (machineType != MachineType.WINDOWS) {
				LOGGER.debug("Set working directory (Non-Windows) to " + printJobDirectory);
				slicerProcessBuilder.directory(new File(printJobDirectory));
			}
			LOGGER.info("Slicer command is " + String.join(" ", slicerProcessBuilder.command()));

			Process slicerProcess = null;

			if (isCancelled()) {
				LOGGER.debug("Slice cancelled");
				return false;
			}

			try {
				slicerProcess = slicerProcessBuilder.start();
				// any error message?
				SlicerOutputGobbler errorGobbler = new SlicerOutputGobbler(progressReceiver, slicerProcess.getErrorStream(), "ERROR",
						slicerType);

				// any output?
				SlicerOutputGobbler outputGobbler = new SlicerOutputGobbler(progressReceiver, slicerProcess.getInputStream(),
						"OUTPUT", slicerType);

				// kick them off
				errorGobbler.start();
				outputGobbler.start();

				int exitStatus = slicerProcess.waitFor();

				if (isCancelled()) {
					LOGGER.debug("Slice cancelled");
					return false;
				}

				switch (exitStatus) {
					case 0:
						LOGGER.debug("Slicer terminated successfully ");
						succeeded = true;
						break;
					default:
						LOGGER.error("Failure when invoking slicer with command line: " + String.join(
								" ", slicerProcessBuilder.command()));
						LOGGER.error("Slicer terminated with exit code " + exitStatus);
						break;
				}
			}
			catch (IOException ex) {
				LOGGER.error("Exception whilst running slicer: " + ex);
			}
			catch (InterruptedException ex) {
				LOGGER.warn("Interrupted whilst waiting for slicer to complete");
				if (slicerProcess != null) {
					slicerProcess.destroyForcibly();
				}
			}
		}
		else {
			LOGGER.error("Couldn't run slicer - no commands for OS ");
		}

		return succeeded;
	}

	@Override
	public void progressUpdateFromSlicer(String message, float workDone) {
		updateMessage(message);
		updateProgress(workDone, 100.0);
	}
}
