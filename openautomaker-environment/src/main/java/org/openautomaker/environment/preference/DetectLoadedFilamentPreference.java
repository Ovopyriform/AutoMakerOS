package org.openautomaker.environment.preference;

/**
 * Preference to determine if we should detect loaded filament
 */
public class DetectLoadedFilamentPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}

}
