package org.openautomaker.environment.preference;

/**
 * Preference to represent the users choice for show tooltips
 */
public class ShowTooltipsPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}

}
