package org.openautomaker.environment.preference.advanced;

import org.openautomaker.environment.preference.AbsBooleanPreference;

/**
 * Preference to determine if we should show the GCode console in advanced mode
 */
public class ShowGCodeConsolePreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
