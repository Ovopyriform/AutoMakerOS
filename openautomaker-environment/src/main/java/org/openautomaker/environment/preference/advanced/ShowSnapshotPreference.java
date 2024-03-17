package org.openautomaker.environment.preference.advanced;

import org.openautomaker.environment.preference.AbsBooleanPreference;

/**
 * Preference to determine is we show the snapshot panel in advanced mode
 */
public class ShowSnapshotPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
