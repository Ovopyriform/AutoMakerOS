package org.openautomaker.base.configuration;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.slicer.NozzleParameters;

/**
 *
 * @author George Salter
 */
public class RoboxProfile {

	private static final Logger LOGGER = LogManager.getLogger();

	// Special nozzle parameter settings
	private static final String EJECTION_VOLUME = "ejectionVolume";
	private static final String PARTIAL_B_MINIMUM = "partialBMinimum";

	private final String name;
	private final String headType;
	private final boolean standardProfile;

	private Map<String, String> settings;
	private List<NozzleParameters> nozzleParameters;

	public RoboxProfile(String name, String headType, boolean standardProfile, Map<String, String> settings) {
		this.name = name;
		this.headType = headType;
		this.standardProfile = standardProfile;
		this.settings = settings;
		createNozzleParameters();
	}

	/**
	 * Copy constructor
	 * 
	 * @param roboxProfile
	 */
	public RoboxProfile(RoboxProfile roboxProfile) {
		this.name = roboxProfile.getName();
		this.headType = roboxProfile.getHeadType();
		this.standardProfile = roboxProfile.isStandardProfile();
		this.settings = new HashMap<>(roboxProfile.getSettings());
		this.nozzleParameters = roboxProfile.getNozzleParameters().stream()
				.map(nozzleParam -> new NozzleParameters(nozzleParam))
				.collect(toList());
	}

	public String getName() {
		return name;
	}

	public String getHeadType() {
		return headType;
	}

	public boolean isStandardProfile() {
		return standardProfile;
	}

	public Map<String, String> getSettings() {
		return settings;
	}

	public void setSettings(Map<String, String> settings) {
		this.settings = settings;
		createNozzleParameters();
	}

	public List<NozzleParameters> getNozzleParameters() {
		return this.nozzleParameters;
	}

	public void addOrOverride(String settingId, String value) {
		settings.put(settingId, value);
	}

	public float getSpecificFloatSetting(String settingId) {
		String value = settings.get(settingId);
		if (value == null) {
			LOGGER.error("Setting with id: " + settingId + " does not exist. Returning 0.");
			return 0f;
		}
		value = sanitiseValue(value);
		float floatValue = Float.valueOf(value);
		return floatValue;
	}

	public int getSpecificIntSetting(String settingId) {
		String value = settings.get(settingId);
		if (value == null) {
			LOGGER.error("Setting with id: " + settingId + " does not exist. Returning 0.");
			return 0;
		}
		int intValue = Integer.valueOf(value);
		return intValue;
	}

	public boolean getSpecificBooleanSetting(String settingId) {
		String value = settings.get(settingId);
		if (value == null) {
			LOGGER.error("Setting with id: " + settingId + " does not exist. Returning false.");
			return false;
		}
		boolean booleanValue = Boolean.valueOf(value);
		return booleanValue;
	}

	public String getSpecificSettingAsString(String settingId) {
		String value = settings.get(settingId);
		if (value == null) {
			LOGGER.error("Setting with id: " + settingId + " does not exist. Returning empty String.");
			return "";
		}
		return value;
	}

	public float getSpecificFloatSettingWithDefault(String settingId, int defaultValue) {
		float floatValue = defaultValue;
		String settingsValue = settings.get(settingId);
		if (settingsValue != null) {
			settingsValue = sanitiseValue(settingsValue);
			floatValue = Float.valueOf(settingsValue);
		}
		return floatValue;
	}

	public int getSpecificIntSettingWithDefault(String settingId, int defaultValue) {
		int intValue = defaultValue;
		String settingsValue = settings.get(settingId);
		if (settingsValue != null)
			intValue = Integer.valueOf(settingsValue);
		return intValue;
	}

	public boolean getSpecificBooleanSettingWithDefault(String settingId, boolean defaultValue) {
		boolean booleanValue = defaultValue;
		String settingsValue = settings.get(settingId);
		if (settingsValue != null)
			booleanValue = Boolean.valueOf(settingsValue);
		return booleanValue;
	}

	public String getSpecificSettingAsStringWithDefault(String settingId, String defaultValue) {
		return settings.getOrDefault(settingId, defaultValue);
	}

	@Override
	public String toString() {
		return name;
	}

	// TODO: There is a lot that could go wrong here, need to think about how to handle unexpected values.
	private void createNozzleParameters() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Creating nozzle parameters for RoboxProfile: " + name + "[" + headType + "]");

		List<NozzleParameters> createdNozzleParameters = new ArrayList<>();

		if (settings.containsKey(EJECTION_VOLUME)) {
			String ejectionVolume = settings.get(EJECTION_VOLUME);
			ejectionVolume = sanitiseValue(ejectionVolume);
			for (String value : ejectionVolume.split(":")) {
				NozzleParameters nozzleParameters = new NozzleParameters();
				nozzleParameters.setEjectionVolume(Float.parseFloat(value));
				createdNozzleParameters.add(nozzleParameters);
			}
		}

		if (settings.containsKey(PARTIAL_B_MINIMUM)) {
			String partialBMinimum = settings.get(PARTIAL_B_MINIMUM);
			partialBMinimum = sanitiseValue(partialBMinimum);
			String[] values = partialBMinimum.split(":");
			for (int i = 0; i < values.length; i++) {
				createdNozzleParameters.get(i).setPartialBMinimum(Float.parseFloat(values[i]) / 100);
			}
		}

		nozzleParameters = createdNozzleParameters;
	}

	/**
	 * Some languages use , as a decimal point delimiter, we need to sanitise the string to make sure we are replacing these with decimal points before we attempt to do anything with them.
	 * 
	 * @param value
	 * @return String representing the value with any , replaced with .
	 */
	private String sanitiseValue(String value) {
		String sanitisedValue = value.replaceAll(",", ".");
		return sanitisedValue;
	}
}
