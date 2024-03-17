package org.openautomaker.environment.preference;

/**
 * Boolean Preference to represent if safety features are enabled
 */
public class SafetyFeaturesPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}
}
