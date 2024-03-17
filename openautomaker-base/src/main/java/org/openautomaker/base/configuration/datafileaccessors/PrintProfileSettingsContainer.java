package org.openautomaker.base.configuration.datafileaccessors;

import static org.openautomaker.environment.OpenAutomakerEnv.PRINT_PROFILES;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.profilesettings.PrintProfileSetting;
import org.openautomaker.base.configuration.profilesettings.PrintProfileSettings;
import org.openautomaker.environment.OpenAutomakerEnv;
import org.openautomaker.environment.Slicer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import javafx.util.Pair;

/**
 *
 * @author George Salter
 */
public class PrintProfileSettingsContainer {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String PRINT_PROFILE_SETTINGS_JSON = "print_profile_settings.json";

	private static PrintProfileSettingsContainer instance;

	private static Map<Slicer, PrintProfileSettings> printProfileSettings;
	private static Map<Slicer, PrintProfileSettings> defaultPrintProfileSettings;

	private PrintProfileSettingsContainer() {
		printProfileSettings = new HashMap<>();
		defaultPrintProfileSettings = new HashMap<>();
		loadPrintProfileSettingsFile();
	}

	public static PrintProfileSettingsContainer getInstance() {
		if (instance == null) {
			instance = new PrintProfileSettingsContainer();
		}
		return instance;
	}

	public PrintProfileSettings getPrintProfileSettingsForSlicer(Slicer slicerType) {
		return printProfileSettings.get(slicerType);
	}

	public PrintProfileSettings getDefaultPrintProfileSettingsForSlicer(Slicer slicerType) {
		return defaultPrintProfileSettings.get(slicerType);
	}

	public Map<String, List<PrintProfileSetting>> compareAndGetDifferencesBetweenSettings(PrintProfileSettings originalSettings, PrintProfileSettings newSettings) {
		Map<String, List<PrintProfileSetting>> changedValuesMap = new HashMap<>();

		List<Pair<PrintProfileSetting, String>> originalSettingsList = originalSettings.getAllEditableSettingsWithSections();
		List<Pair<PrintProfileSetting, String>> newSettingsList = newSettings.getAllEditableSettingsWithSections();

		originalSettingsList.forEach(settingToSection -> {
			String sectionTitle = OpenAutomakerEnv.getI18N().t(settingToSection.getValue());
			PrintProfileSetting originalSetting = settingToSection.getKey();

			// From the new settings find one with the same id and different vaue from the old setting
			Optional<PrintProfileSetting> possibleChangedSetting = newSettingsList.stream()
					.map(newSettingToSection -> {
						return newSettingToSection.getKey();
					})
					.filter(newSetting -> originalSetting.getId().equals(newSetting.getId()))
					.filter(newSetting -> !originalSetting.getValue().equals(newSetting.getValue()))
					.findFirst();

			// If we have a changed value, add the setting to the map in the correct section
			if (possibleChangedSetting.isPresent()) {
				if (changedValuesMap.containsKey(sectionTitle)) {
					changedValuesMap.get(sectionTitle).add(possibleChangedSetting.get());
				}
				else {
					changedValuesMap.put(sectionTitle, new ArrayList(Arrays.asList(possibleChangedSetting.get())));
				}
			}
		});

		return changedValuesMap;
	}

	public static void loadPrintProfileSettingsFile() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new Jdk8Module());

		//Loop through enumeration and create all print profile settings.
		for (Slicer slicerType : Slicer.values()) {

			File profileSettingsFile = OpenAutomakerEnv.get()
					.getApplicationPath(PRINT_PROFILES)
					.resolve(slicerType.getPathModifier())
					.resolve(PRINT_PROFILE_SETTINGS_JSON)
					.toFile();

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Loading print profile '" + slicerType.getFriendlyName() + "' from [" + profileSettingsFile.toString() + "]");

			try {
				PrintProfileSettings profileSettings = objectMapper.readValue(profileSettingsFile, PrintProfileSettings.class);
				printProfileSettings.put(slicerType, profileSettings);
				defaultPrintProfileSettings.put(slicerType, profileSettings);
			}
			catch (IOException e) {
				LOGGER.error("Could not load profile for '" + slicerType.getFriendlyName() + "'", e);
			}
		}
	}
}
