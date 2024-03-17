package org.openautomaker.environment.preference.advanced;

import org.openautomaker.environment.preference.AbsBooleanPreference;

/**
 * Preference to determine if we show the adjustments panel in advanced mode
 */
public class ShowAdjustmentsPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
