package org.openautomaker.base.configuration;

import static org.openautomaker.environment.OpenAutomakerEnv.PRINT_PROFILES;
import static org.openautomaker.environment.OpenAutomakerEnv.TIMELAPSE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.datafileaccessors.PrinterContainer;
import org.openautomaker.environment.OpenAutomakerEnv;
import org.openautomaker.environment.Slicer;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class BaseConfiguration {

	private static final Logger LOGGER = LogManager.getLogger();

	/*
	 * THINGS THAT SHOULD BE IN GUI ONLY
	 */
	public static final int NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP = 210;
	public static final float maxTempToDisplayOnGraph = 300;
	public static final float minTempToDisplayOnGraph = 35;
	/*
	 * END OF THINGS THAT SHOULD BE IN GUI ONLY
	 */

	/*
	 * CONSTANTS
	 */
	public static final float filamentDiameterToYieldVolumetricExtrusion = 1.1283791670955125738961589031215f;
	public static final float filamentDiameter = 1.75f;
	public static final int maxPermittedTempDifferenceForPurge = 15;

	public static final String applicationConfigComponent = "ApplicationConfiguration";

	public static final String userStorageDirectoryComponent = "UserDataStorageDirectory";
	private static String applicationStorageDirectory = null;

	public static final String applicationStorageDirectoryComponent = "ApplicationDataStorageDirectory";

	public static final String printerDirectoryPath = "Printers";
	public static final String printerFileExtension = ".roboxprinter";

	public static final String headDirectoryPath = "Heads";
	public static final String headFileExtension = ".roboxhead";

	public static final String modelStorageDirectoryPath = "Models";
	public static final String userTempDirectoryPath = "Temp";

	public static final String filamentDirectoryPath = "Filaments";
	public static final String filamentFileExtension = ".roboxfilament";

	public static final String applicationKeyPath = "Key";

	private static final String remotePrintJobDirectory = "/home/pi/CEL Root/PrintJobs/";
	private static final String remoteRootDirectory = "/home/pi/CEL Root/";
	private static final String remoteRootTimelapseDirectory = "/home/pi/CEL Root/Timelapse";

	private static boolean autoRepairHeads = true;

	private static boolean autoRepairReels = true;

	private static String applicationTitleAndVersion = null;

	public static final String printSpoolStorageDirectoryPath = "PrintJobs";

	public static final int mmOfFilamentOnAReel = 240000;

	/**
	 * The extension for statistics files in print spool directories
	 */
	public static String statisticsFileExtension = ".statistics";
	public static String cameraDataFileExtension = ".camera";
	public static final String gcodeTempFileExtension = ".gcode";
	public static final String stlTempFileExtension = ".stl";
	public static final String amfTempFileExtension = ".amf";

	public static final String gcodePostProcessedFileHandle = "_robox";
	public static final String printProfileFileExtension = ".roboxprofile";

	public static final String customSettingsProfileName = "Custom";

	public static final String draftSettingsProfileName = "Draft";

	public static final String normalSettingsProfileName = "Normal";

	public static final String fineSettingsProfileName = "Fine";

	public static final String macroFileExtension = ".gcode";

	public static final String printProfileDirectoryPath = "PrintProfiles";
	public static final int maxPrintSpoolFiles = 20;

	public static final String cameraProfilesDirectoryName = "CameraProfiles";
	public static final String cameraProfileFileExtention = ".cameraprofile";
	public static final String defaultCameraProfileName = "Default";

	//	private static final String printProfileSettingsFileName = "print_profile_settings.json";

	//private static final String fileMemoryItem = "FileMemory";


	public static void initialise(Class<?> classToCheck) {
		PrinterContainer.getCompletePrinterList();
	}

	public static void shutdown() {

	}

	//	public static Path getApplicationHeadDirectory() {
	//		return OpenAutomakerEnv.get().getApplicationPath(HEADS);
	//	}

	//	public static Path getApplicationPrinterDirectory() {
	//		return OpenAutomakerEnv.get().getApplicationPath(PRINTERS);
	//	}

	//	public static Path getApplicationKeyDirectory() {
	//		return OpenAutomakerEnv.get().getApplicationPath(KEY);
	//	}

	public static String getRemoteRootDirectory() {
		return remoteRootDirectory;
	}

	// This should be a restricted location on the root so we don't have to worry about the actual location here.
	public static String getRemotePrintJobDirectory() {
		return remotePrintJobDirectory;
	}

	public static String getRemoteTimelapseDirectory() {
		return remoteRootTimelapseDirectory;
	}

	public static boolean isAutoRepairHeads() {
		return autoRepairHeads;
	}

	public static void setAutoRepairHeads(boolean value) {
		autoRepairHeads = value;
	}

	public static boolean isAutoRepairReels() {
		return autoRepairReels;
	}

	public static void setAutoRepairReels(boolean value) {
		autoRepairReels = value;
	}

	//	public static String getApplicationVersion() {
	//		return OpenAutomakerEnv.get().getVersion();
	//	}

	//	public static String getApplicationLocale() {
	//		return OpenAutomakerEnv.get().getLocale().toLanguageTag();
	//	}

	public static Path getTimelapseDirectory() {
		return OpenAutomakerEnv.get().getUserPath(TIMELAPSE);
	}

	public static Path getUserStorageDirectory() {
		return OpenAutomakerEnv.get().getUserPath();
	}

	public static Path getApplicationPrintProfileDirectory() {
		return OpenAutomakerEnv.get().getApplicationPath(PRINT_PROFILES);
	}

	public static Path getUserPrintProfileDirectory() {
		return OpenAutomakerEnv.get().getUserPath(PRINT_PROFILES);
	}

	public static Path getUserPrintProfileDirectoryForSlicer(Slicer slicerType) {
		Path userSlicerPrintProfileDirectory = OpenAutomakerEnv.get().getUserPath(PRINT_PROFILES).resolve(slicerType.getPathModifier());


		if (Files.notExists(userSlicerPrintProfileDirectory))
			try {
				Files.createDirectories(userSlicerPrintProfileDirectory);
			} catch (IOException e) {
				LOGGER.warn("Could not create user print profile directory");
			}


		//		if (slicerType == SlicerType.Cura) {
		//			// Find any old .roboxprofiles hanging around and convert them to the new format
		//			// They are added to the correct head folder and the old file is archived
		//			try {
		//				Path userProfileDir = Paths.get(getUserPrintProfileDirectory());
		//
		//				List<Path> oldRoboxFiles = Files.list(userProfileDir).filter(file -> file.getFileName().toString().endsWith(printProfileFileExtension)).collect(Collectors.toList());
		//
		//				if (!oldRoboxFiles.isEmpty()) {
		//					for (Path file : oldRoboxFiles) {
		//						RoboxProfileUtils.convertOldProfileIntoNewFormat(file, dirHandle.toPath());
		//					}
		//				}
		//			} catch (IOException ex) {
		//				LOGGER.error("Failed to convert old robox profiles to the new format.", ex);
		//			}
		//		}

		return userSlicerPrintProfileDirectory;
	}

	public static Path getGCodeViewerDirectory() {
		return OpenAutomakerEnv.get().getApplicationPath().resolve("GCodeViewer");
	}
}
