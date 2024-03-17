package org.openautomaker.environment.preference.advanced;

import org.openautomaker.environment.preference.AbsBooleanPreference;

/**
 * Preference to determine if we should show diagnostic info
 */
public class ShowDiagnosticsPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
