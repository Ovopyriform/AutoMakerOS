package xyz.openautomaker.base.configuration;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.CAMERA_PROFILES;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.FILAMENTS;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.HEADS;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.KEY;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.MODELS;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.PRINTERS;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.PRINT_PROFILES;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.SCRIPT;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.TEMP;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.TIMELAPSE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.ApplicationFeature;
import xyz.openautomaker.base.configuration.datafileaccessors.PrinterContainer;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

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

	private static final Set<ApplicationFeature> applicationFeatures = new HashSet<>();

	public static void initialise(Class<?> classToCheck) {
		getApplicationInstallDirectory(classToCheck);
		PrinterContainer.getCompletePrinterList();
	}

	public static void shutdown() {

	}

	//	public static boolean isWindows32Bit() {
	//		return System.getProperty("os.name").contains("Windows") && System.getenv("ProgramFiles(x86)") == null;
	//	}

	public static String getApplicationName() {
		return OpenAutoMakerEnv.get().getName();
	}

	public static String getApplicationShortName() {

		return OpenAutoMakerEnv.get().getShortName();
	}

	//TODO: This is fine for macos but windows we'll need to find where the class is and make the folder relative to the location.
	public static Path getApplicationInstallDirectory(Class<?> classToCheck) {

		LOGGER.info("CTH INFO LOCATION OF CLASS" + classToCheck.getProtectionDomain().getCodeSource().getLocation().getPath());

		return OpenAutoMakerEnv.get().getApplicationPath();
	}

	// This won't exist in the new structure as there's no CEL directory
	// All part of the package resources, overloads in the user directory.
	public static Path getCELInstallDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath();
		//		if (celInstallDirectory == null) {
		//			File p = new File(applicationInstallDirectory);
		//			celInstallDirectory = p.getParent() + File.separator;
		//		}
		//
		//		return celInstallDirectory;
	}

	// Not splitting the common and app directory any more.
	//	public static String getCommonApplicationDirectory() {
	//		return AutoMakerEnvironment.get().getApplicationPath().toString();
	//	}

	public static Path getApplicationHeadDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(HEADS);
	}

	public static Path getApplicationPrinterDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(PRINTERS);
	}

	public static Path getApplicationKeyDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(KEY);
	}

	// I imagine this is an external folder for *something* 
	public static String getExternalStaticDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath().toString();

		//		try {
		//			return configuration.getFilenameString(applicationConfigComponent, "ExternalStaticDirectory", null);
		//		} catch (ConfigNotLoadedException ex) {
		//			LOGGER.info("No external static directory specified");
		//			return null;
		//		}
	}

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

	//	private static void loadProjectProperties() {
	//		InputStream input = null;
	//
	//		try {
	//			input = new FileInputStream(applicationInstallDirectory + "application.properties");
	//
	//			// load a properties file
	//			installationProperties = new Properties();
	//			installationProperties.load(input);
	//		} catch (IOException ex) {
	//			LOGGER.warn("Couldn't load application.properties");
	//		} finally {
	//			if (input != null) {
	//				try {
	//					input.close();
	//				} catch (IOException ex) {
	//					LOGGER.error("Error closing properties file", ex);
	//				}
	//			}
	//		}
	//	}

	public static String getApplicationVersion() {
		return OpenAutoMakerEnv.get().getVersion();
	}

	public static String getApplicationLocale() {
		return OpenAutoMakerEnv.get().getLocale().toLanguageTag();
	}

	public static void setTitleAndVersion(String titleAndVersion) {
		applicationTitleAndVersion = titleAndVersion;
	}

	public static String getTitleAndVersion() {
		return applicationTitleAndVersion;
	}

	// This should be a temporary directory
	//	public static Path getPrintSpoolDirectory() {
	//		return AutoMakerEnvironment.get().getUserPath(PRINT_JOBS);
	//	}

	public static Path getTimelapseDirectory() {
		return OpenAutoMakerEnv.get().getUserPath(TIMELAPSE);
	}

	public static Path getUserStorageDirectory() {
		return OpenAutoMakerEnv.get().getUserPath();
	}

	public static Path getApplicationPrintProfileDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(PRINT_PROFILES);
	}

	public static Path getUserPrintProfileDirectory() {
		return OpenAutoMakerEnv.get().getUserPath(PRINT_PROFILES);
	}

	public static Path getUserPrintProfileDirectoryForSlicer(SlicerType slicerType) {
		Path userSlicerPrintProfileDirectory = OpenAutoMakerEnv.get().getUserPath(PRINT_PROFILES).resolve(slicerType.getPathModifier());


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

	public static Path getApplicationCameraProfilesDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(CAMERA_PROFILES);
	}

	public static Path getUserCameraProfilesDirectory() {
		return OpenAutoMakerEnv.get().getUserPath(CAMERA_PROFILES);
	}

	//	public static Path getPrintProfileSettingsFileLocation(SlicerType slicerType) {
	//		return getApplicationPrintProfileDirectoryForSlicer(slicerType).resolve(printProfileSettingsFileName);
	//	}

	public static Path getUserTempDirectory() {
		return OpenAutoMakerEnv.get().getUserPath(TEMP);
	}

	public static String getApplicationStorageDirectory() {

		applicationStorageDirectory = OpenAutoMakerEnv.get().getApplicationPath().toString();

		//		if (configuration != null && applicationStorageDirectory == null) {
		//			try {
		//				applicationStorageDirectory = configuration.getFilenameString(applicationConfigComponent, applicationStorageDirectoryComponent, null);
		//				LOGGER.debug("Application storage directory = " + applicationStorageDirectory);
		//			} catch (ConfigNotLoadedException ex) {
		//				LOGGER.error("Couldn't determine application storage location - the application will not run correctly");
		//			}
		//		}
		return applicationStorageDirectory;
	}

	public static Path getApplicationModelDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(MODELS);
	}

	//public static Properties getInstallationProperties() {
	//	return installationProperties;
	//}

	public static Path getApplicationFilamentDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(FILAMENTS);
	}

	public static Path getUserFilamentDirectory() {
		return OpenAutoMakerEnv.get().getUserPath(FILAMENTS);
	}

	public static String getApplicationInstallationLanguage() {
		return OpenAutoMakerEnv.get().getLocale().toLanguageTag();
	}

	public static Path getBinariesDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath(SCRIPT);
	}

	public static Path getGCodeViewerDirectory() {
		return OpenAutoMakerEnv.get().getApplicationPath().resolve("GCodeViewer");
	}

	public static void enableApplicationFeature(ApplicationFeature feature) {
		applicationFeatures.add(feature);
	}

	public static void disableApplicationFeature(ApplicationFeature feature) {
		applicationFeatures.remove(feature);
	}

	public static boolean isApplicationFeatureEnabled(ApplicationFeature feature) {
		return applicationFeatures.contains(feature);
	}
}
