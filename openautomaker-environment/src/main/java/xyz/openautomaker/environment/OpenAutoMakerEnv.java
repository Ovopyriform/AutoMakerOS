package xyz.openautomaker.environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import xyz.openautomaker.i18n.OpenAutoMakerI18N;

/**
 * Environment object for AutoMaker.
 * 
 * @author chris_henson
 *
 */
public class OpenAutoMakerEnv {

	/*-
	 * Original Structure for Automaker working directory
	 * 
	 * CEL
	 *  +- AutoMaker
	 *  |	+- application.properties
	 *  |	+- AutoMaker [app]
	 *  |	+- AutoMaker.configFile.xml
	 *  |	+- AutoMaker.jar
	 *  |	+- AutoMaker.png
	 *  |	+- Java (JVM)
	 *  |	+- Key
	 *  |	|	+- automaker-root.ssh
	 *  |	+ Language
	 *  |	|	+- Language*.properties
	 *  |	+- lib (java)
	 *  |	+- librxtxSerial.jnilib // This needs to change to JSerialCom  RXTX is about 10 years out of date.
	 *  |	+- README
	 *  |		+- colorFabSplash.jpg
	 *  |		+- READEME_AutoMaker.html
	 *  +- Common
	 *  	+- bin
	 *  	|	+- KillCuraEngine.mac.sh [OS Specific Scripts]
	 *  	|	+- RoboxDetector.mac.sh [OS Specific Scripts]
	 *  	+- CameraProfiles
	 *  	|	+- Logitech C920.cameraprofile
	 *  	+- Cura
	 *  	|	+- CuraEngine
	 *  	+- Cura4
	 *  	|	+- CuraEngine
	 *  	+- Filaments
	 *  	|	+- *.roboxfilament
	 *  	+- GCodeViewer [app]
	 *  	+- Heads
	 *  	|	+- *.roboxhead
	 *  	+- Language
	 *  	|	+- UI_LanguageData*.properties
	 *  	|	+- NoUI_LanguageData*.properties
	 *  	+- Macros
	 *  	|	+- *.gcode
	 *  	+- Models
	 *  	|	+- *.gcode [models and demo code]
	 *  	+- Printers
	 *  	|	+- RBX01.roboxprinter
	 *  	|	+- RBX02.roboxprinter
	 *  	|	+- RBX10.roboxprinter
	 * 		+- PrintProfiles
	 * 		|	+- Cura [print profiles for heads]
	 * 		|	+- Cura4 [print profiles for heads]
	 * 		|	+- slicermapping.dat
	 * 		+- robox_r776.bin
	 *  	+- robox_r780.bin
	 *   	+- robox_r781.bin
	 */

	/*-
	 * New folder structure
	 * ${jpackage.app-path}  
	 *  +- openautomaker.properties [contains original properties and configuration file info]
	 *  +- openautomaker
	 *  	+- script [Contents of Common/bin]
	 *  	|	+- KillCuraEngine.*.sh
	 *  	|	+- RoboxDetector.*.sh
	 *  	+- cura-engine - Should this just be bundled in the class path?
	 *  	|	+- cura
	 *  	|	+- cura4 (do I need Cura any more.  Why choose the older one)
	 *  	|	+- serial ??  [What is the name of the lib]
	 *  	+- language - these can be bundled.  There's no reason to have them separately.
	 *  	|	+- all language resources [current splitting of language resources is a bit mental and prevents use of vanilla PropertiesResourceBundle, requires custom code]
	 *  	+- key
	 *  	|	+- ssh key
	 *  	+- camera-profiles
	 *  	+- filaments
	 *  	+- heads
	 *  	+- macros
	 *		+- models
	 *		+- printers
	 *		+- print-profiles
	 *		+- firmware
	 *		+- cura-engine
	 *
	 * ${user.home}
	 *	+- openautomaker
	 *		+- automaker.properties [user overrides of base properties]
	 *		+- camera-profiles
	 *		+- filaments
	 *		+- heads
	 *		+- macros
	 *		+- printers
	 *		+- print-profiles
	 *		+- print-jobs
	 *		+- projects
	 *		+- timelapse
	 */

	public static final String OPENAUTOMAKER = "openautomaker";
	private static final String OPENAUTOMAKER_NAME = OPENAUTOMAKER + ".name";
	private static final String OPENAUTOMAKER_SHORT_NAME = OPENAUTOMAKER + ".short_name";
	private static final String OPENAUTOMAKER_VERSION = OPENAUTOMAKER + ".version";
	private static final String OPENAUTOMAKER_REQUIRED_FIRMWARE_VERSION = OPENAUTOMAKER + ".required_firmware_version";

	private static final String OPENAUTOMAKER_DEBUG = OPENAUTOMAKER + ".debug";
	private static final String OPENAUTOMAKER_MODE = OPENAUTOMAKER + ".mode";
	private static final String OPENAUTOMAKER_LOCALE = OPENAUTOMAKER + ".locale";
	private static final String OPENAUTOMAKER_CURA_VERSION = OPENAUTOMAKER + ".cura.version";

	private static final String OPENAUTOMAKER_LAST = OPENAUTOMAKER + ".last";
	public static final String OPENAUTOMAKER_LAST_VERSION_RUN = OPENAUTOMAKER_LAST + ".version_run";
	public static final String OPENAUTOMAKER_LAST_PRINTER_FIRMWARE = OPENAUTOMAKER_LAST + ".printer.firmware";
	public static final String OPENAUTOMAKER_LAST_PRINTER_SERIAL = OPENAUTOMAKER_LAST + ".printer.serial";
	public static final String OPENAUTOMAKER_LAST_NEWS_RETRIEVAL = OPENAUTOMAKER_LAST + ".news.retrieval";

	private static final String OPENAUTOMAKER_ROOT = OPENAUTOMAKER + ".root";
	public static final String OPENAUTOMAKER_ROOT_CONNECTED = OPENAUTOMAKER_ROOT + ".connected";

	public static final String OPENAUTOMAKER_LEGACY = OPENAUTOMAKER + ".legacy";

	// Properties specific to ROOT
	// TODO: Consider separate ROOT environment
	private static final String ROOT = "root";
	public static final String ROOT_SERVER_NAME = ROOT + ".server.name";
	public static final String ROOT_ACCESS_PIN = ROOT + ".access.pin";


	// Directory structures
	public static final String BIN = "bin";
	public static final String SCRIPT = "script";
	public static final String CURA_ENGINE = "cura-engine";
	public static final String KEY = "key";
	public static final String CAMERA_PROFILES = "camera-profiles";
	public static final String FILAMENTS = "filaments";
	public static final String HEADS = "heads";
	public static final String MACROS = "macros";
	public static final String MODELS = "models";
	public static final String PRINTERS = "printers";
	public static final String PRINT_JOBS = "print-jobs"; // Probably just for Root??
	public static final String PRINT_PROFILES = "print-profiles";
	public static final String PROJECTS = "projects";
	public static final String FIRMWARE = "firmware";
	//public static final String PRINT_SPOOL = "print-spool";
	public static final String TIMELAPSE = "timelapse"; // Probably just for Root??
	public static final String TEMP = "temp"; //Should be in temp folder

	private static final String OPENAUTOMAKER_TEST_ENVIRONMENT = "openautomaker-test-environment";
	private static final String BACK = "..";
	private static final String ENV = "env";
	private static final String APP = "app";
	private static final String USR = "usr";

	/**
	 * Properties file name
	 */
	private static final String AUTOMAKER_PROPERTIES = OPENAUTOMAKER + ".properties";

	/**
	 * Fail-safe properties distributed as part of this package. Potentially look copying fail-safe properties folder if one doesn't exist.
	 */
	private static final String OPENAUTOMAKER_FAILSAFE = OPENAUTOMAKER + ".failsafe";
	private static final String OPENAUTOMAKER_FAILSAFE_PROPERTIES = OPENAUTOMAKER_FAILSAFE + ".properties";

	//@formatter:off
	
	/**
	 * List of properties that the user cannot override
	 */
	private List<String> overrideExcludes = List.of(
			OPENAUTOMAKER_VERSION,
			OPENAUTOMAKER_REQUIRED_FIRMWARE_VERSION,
			OPENAUTOMAKER_NAME,
			OPENAUTOMAKER_SHORT_NAME
		);
	//@formater:on
	
	private static OpenAutoMakerEnv env = null;

	private static final Logger LOGGER = LogManager.getLogger();

	private OpenAutoMakerI18N i18n = null;
	
	private Properties systemProps = null;
	private Properties applicationProps = null;
	private Properties userProps = null;

	//private Path workingDir = null;
	//private Path homeDir = null;

	private Map<String, Path> applicationPaths = null;
	private Map<String, Path> userPaths = null;

	
	private MachineType machineType = null;
	
	/**
	 * Get the instance of the environment. Will initialise the environment on the first call
	 * 
	 * @return AutoMakerEnvironment instance
	 */
	public static OpenAutoMakerEnv get() {
		if (env != null)
			return env;

		env = new OpenAutoMakerEnv();

		return env;
	}
	
	/**
	 * Gets the configured I18N object based.
	 * 
	 * @return AutoMakerI18N object for the configured locale
	 */
	public static OpenAutoMakerI18N getI18N() {
		return get().i18n();
	}

	/**
	 * Private constructor for 'most cases' singleton enforcement
	 */
	private OpenAutoMakerEnv() {
		systemProps = System.getProperties();
		buildPaths();
		loadProperties();
		configureLogging();
		configureI18N();

		if (isDebugEnabled())
			dumpEnvironment();
		
		checkUserDirectory();
	}
	
	/**
	 * Build the path objects for the application and the home directories
	 */
	private void buildPaths() {
		
		//We need to determine if we're in a test environment or in the live environment.  In a live environment, the jpackage.app-path will be set.
		// Configure Application path
		String appHome = systemProps.getProperty("jpackage.app-path");
		boolean isPackaged = appHome != null;
		
		String userHome = systemProps.getProperty("user.home");
		if (!isPackaged) {
			appHome = Paths.get(systemProps.getProperty("user.dir"), BACK, OPENAUTOMAKER_TEST_ENVIRONMENT, ENV, APP).toString();
			userHome = Paths.get(systemProps.getProperty("user.dir"), BACK, OPENAUTOMAKER_TEST_ENVIRONMENT, ENV, USR).toString();
		}	
		
		Path appPath = Paths.get(appHome);
		Path userPath = Paths.get(userHome, OPENAUTOMAKER);
		
		userPaths = new HashMap<>();
		userPaths.put(OPENAUTOMAKER, userPath);
		
		List.of(AUTOMAKER_PROPERTIES, CAMERA_PROFILES, FILAMENTS, HEADS, MACROS, PRINT_PROFILES, PRINT_JOBS, PROJECTS, TIMELAPSE, TEMP)
		.forEach((relativePath) -> {
			userPaths.put(relativePath, userPath.resolve(relativePath));
		});
		
		// If it's a packaged mac app, everything is in Contents.  jpackage.app-path -> /Applications/OpenAutoMaker.app/Contents/MacOS/OpenAutoMaker (Script executable)
		if (isPackaged && getMachineType() == MachineType.MAC)
			appPath = appPath.subpath(0, appPath.getNameCount() - 2);
		
		LOGGER.info("Using App Path: " + appPath.toString());
		LOGGER.info("Using Usr Path: " + userPath.toString());
		
		Path resourcePath = appPath.resolve(OPENAUTOMAKER);
		applicationPaths = new HashMap<>();
		applicationPaths.put(OPENAUTOMAKER, resourcePath);
		applicationPaths.put(AUTOMAKER_PROPERTIES, appPath.resolve(AUTOMAKER_PROPERTIES));
		
		List.of(BIN, SCRIPT, CURA_ENGINE, KEY, CAMERA_PROFILES, FILAMENTS, HEADS, MACROS, MODELS, PRINTERS, PRINT_PROFILES, FIRMWARE)
		.forEach((relativeDir) -> {
			applicationPaths.put(relativeDir, resourcePath.resolve(relativeDir));
		});
	}
	
	/**
	 * Load default properties from working directory (package) and override with user defined properties
	 */
	private void loadProperties() {
		applicationProps = null;
		try (InputStream is = Files.newInputStream(applicationPaths.get(AUTOMAKER_PROPERTIES))) {
			applicationProps = new Properties();
			applicationProps.load(is);
		} catch (IOException e) {
			LOGGER.info("Application properties not found");
		}
		
		// If we don't have the application props then use the fail-safe props.
		if (applicationProps == null) {
			try(InputStream is = getClass().getResourceAsStream("/"+OPENAUTOMAKER_FAILSAFE_PROPERTIES)) {
				applicationProps = new Properties();
				applicationProps.load(is);
			} catch (IOException e) {
				LOGGER.fatal("Failsafe properties file not found exiting.");
				System.exit(0);  // Double tap.
			}
		}
		
		// Overrides. Defaults to app props.  Override entrySet to write out entries in alpha order
		userProps = new Properties(applicationProps) {
				private static final long serialVersionUID = 4830131224067626316L;

				@Override
				public synchronized Set<Map.Entry<Object, Object>> entrySet() {
		            return Collections.synchronizedSet(
		                    super.entrySet()
		                    .stream()
		                    .sorted(Comparator.comparing(e -> e.getKey().toString()))
		                    .collect(Collectors.toCollection(LinkedHashSet::new)));
		        }
	    };

		try (InputStream is = Files.newInputStream(userPaths.get(AUTOMAKER_PROPERTIES))) {
			userProps.load(is);
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("No user properties found");
		}
	}

	/**
	 * Saves any user properties
	 */
	private void saveProperties() {		
		try (OutputStream os = Files.newOutputStream(userPaths.get(AUTOMAKER_PROPERTIES))) {
			userProps.store(os, null);
		} catch (IOException e) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Could not write user properties");
		}
	}

	/**
	 * Check logging properties and set up logger appropriately
	 */
	private void configureLogging() {
		if (isDebugEnabled()) {
			Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.DEBUG);
			LOGGER.debug("Debugging Enabled (" + OPENAUTOMAKER_DEBUG + "=true)");
		}
	}
	
	/**
	 * Configure the i18n based on the configuration
	 */
	private void configureI18N() {
		i18n = new OpenAutoMakerI18N(getLocale());
	}

	/**
	 * Checks if the user storage directories exist and creates if they don't.
	 */
	private void checkUserDirectory() {
		userPaths.keySet().forEach((key) -> {
			Path path = userPaths.get(key);
			//Ignore the properties file path
			if (!path.endsWith(AUTOMAKER_PROPERTIES) && Files.notExists(path)) {
				try {
					Files.createDirectories(path);
				} catch (IOException e) {
					LOGGER.error("Error creating user directory: " + path.toString(), e);
				}
			}
		});
	}

	/**
	 * Gets the configured i18n object.
	 * 
	 * @return AutoMakerI18N for the configured locale
	 */
	private OpenAutoMakerI18N i18n() {
		return i18n;
	}
	
	/**
	 * Dump all the properties to the log
	 */
	private void dumpEnvironment() {
		LOGGER.debug("System Properties -----------------------------------------");
		systemProps.stringPropertyNames().forEach((key) -> {
			LOGGER.debug(key + ": " + systemProps.getProperty(key));
		});
		
		LOGGER.debug("Applicaton Paths ------------------------------------------");
		applicationPaths.keySet().forEach((key) -> {
			LOGGER.debug(key + ": " + applicationPaths.get(key).toString());
		});

		
		LOGGER.debug("Application Properties ------------------------------------");
		applicationProps.stringPropertyNames().forEach((key) -> {
			LOGGER.debug(key + ": " + applicationProps.getProperty(key));
		});
		
		LOGGER.debug("User Paths ------------------------------------------------");
		userPaths.keySet().forEach((key) -> {
			LOGGER.debug(key + ": " + userPaths.get(key));
		});
		
		if (userProps.size() == 0) {
			LOGGER.debug("No User Properties");
			return;
		}

		LOGGER.debug("User Properties -------------------------------------------");
		userProps.keySet().forEach((key) -> {
			if (overrideExcludes.contains(key)) {
				LOGGER.debug(key + " Ignored");
				return;
			}

			LOGGER.debug(key + ": " + userProps.getProperty(key.toString()));
		});
	}
	
	/**
	 * Get the required firmware version. Cannot be overridden.
	 * 
	 * @return String representation of the firmware version.
	 */
	public String getRequiredFirmwareVersion() {
		return applicationProps.getProperty(OPENAUTOMAKER_REQUIRED_FIRMWARE_VERSION).trim();
	}

	/**
	 * Returns the application locale
	 * 
	 * @return java.util.Locale for the configured locale
	 */
	public Locale getLocale() {
		// Needs to be a bit more clever as the language could me a locale code.
		String automakerLanguage = userProps.getProperty(OPENAUTOMAKER_LOCALE);

		if (automakerLanguage == null)
			return Locale.getDefault();

		// Treat it as a Locale code if it contains a '-'
		if (automakerLanguage.contains("-"))
			return Locale.forLanguageTag(automakerLanguage);

		return new Locale(automakerLanguage, systemProps.getProperty("user.country"));
	}

	/**
	 * Sets the locale based on a Locale object
	 * 
	 * @param locale - Locale object to set the locale on.
	 */
	public void setLocale(Locale locale) {
		setLocale(locale.toLanguageTag());
	}
	
	/**
	 * Sets the locale and stores to the user properties
	 * 
	 * @param languageTag - String language tag to the standard output by Locale.toLanguageTag();
	 */
	public void setLocale(String languageTag) {
		if (languageTag == null || languageTag.isBlank())
			return;
		
		userProps.setProperty(OPENAUTOMAKER_LOCALE, languageTag);
		configureI18N();
		saveProperties();
	}
	
	/**
	 * Gets the AutoMaker version
	 * 
	 * @return String version number
	 */
	public String getVersion() {
		return applicationProps.getProperty(OPENAUTOMAKER_VERSION).trim();
	}

	/**
	 * If the debug flag is set for automaker via the automaker.debug property
	 * 
	 * @return boolean true/false
	 */
	public boolean isDebugEnabled() {
		String debugPropVal = userProps.getProperty(OPENAUTOMAKER_DEBUG).trim();
		return "true".equals(debugPropVal);
	}

	/**
	 * Gets the automaker directory of the application
	 * 
	 * @return Path representing the working directory. Will work fine with a mac app. Need to check for windows.
	 */
	public Path getApplicationPath() {
		return applicationPaths.get(OPENAUTOMAKER);
	}
	
	/**
	 * Returns a names path from the application directory
	 * 
	 * @param key - Path key
	 * @return Path representing the requested path or null
	 */
	public Path getApplicationPath(String key) {
		return applicationPaths.get(key);
	}
	
	/**
	 * Gets the user's automaker directory
	 * 
	 * @return Path representing the home directory
	 */
	public Path getUserPath() {
		return userPaths.get(OPENAUTOMAKER);
	}
	
	/**
	 * Returns a names path from the user directory
	 * 
	 * @param key - Path key
	 * @return Path representing the requested path or null
	 */
	public Path getUserPath(String key) {
		return userPaths.get(key);
	}

	/**
	 * Gets the configured name
	 * 
	 * @return String
	 */
	public String getName() {
		return userProps.getProperty(OPENAUTOMAKER_NAME);
	}

	/**
	 * Gets the configured Short Name
	 * 
	 * @return String
	 */
	public String getShortName() {
		return userProps.getProperty(OPENAUTOMAKER_SHORT_NAME);
	}
	
	/**
	 * Returns the value of a property
	 * 
	 * @return
	 */
	public String getProperty(String key) {
		if (key == null || key.isBlank())
			return null;
		
		return overrideExcludes.contains(key) ? applicationProps.getProperty(key) : userProps.getProperty(key);
	}
	
	
	
	/**
	 * Sets a user space property and persists to the user properties file
	 * 
	 * @param key - The property to set
	 * @param value - The value of the property
	 */
	public void setProperty(String key, String value) {
		if (key == null || key.isBlank() || overrideExcludes.contains(key))
			return;
		
		userProps.setProperty(key, value);
		saveProperties();
	}
	
	/**
	 * Deletes a property from the user space and saves the properties to the file
	 * 
	 * @param key - The property to remove
	 * @return String - value of the removed property
	 */
	public String removeProperty(String key) {
		if (key == null || key.isBlank()|| overrideExcludes.contains(key))
			return null;
		
		String value = (String) userProps.remove(key);
		saveProperties();
		return value;
	}
	
	/**
	 * Searches for the system property with the specified key. The method returns null if the property is not found.
	 * 
	 * @param key - Property key
	 * @return String property value or null 
	 */
	public String getSystemProperty(String key) {
		return systemProps.getProperty(key);
	}
	
	/**
	 * MachineType, used for UI oddities
	 * 
	 * @return Detected MachineType
	 */
	public MachineType getMachineType() {
		if (machineType != null)
			return machineType;
		
		String osName = systemProps.getProperty("os.name");
		for (MachineType mt : MachineType.values()) {
			if (osName.matches(mt.getRegex())) {
				machineType = mt;
				return machineType;
			}
		}
		
		// Case where there's an unknown OS.
		return null;
	}
	
	/**
	 * Checks for 3D support.
	 * 
	 * @return true if supported
	 */
	public boolean has3DSupport() {
		String forceGPU = systemProps.getProperty("prism.forceGPU");
		if (forceGPU != null && forceGPU.equalsIgnoreCase("true"))
			return true;

		if (Platform.isSupported(ConditionalFeature.SCENE3D))
			return true;
		
		return false;
	}
}
