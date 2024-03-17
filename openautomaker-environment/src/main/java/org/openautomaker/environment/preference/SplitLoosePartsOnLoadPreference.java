package org.openautomaker.environment.preference;

/**
 * Preference to determine if loose mode parts should be split on load
 */
public class SplitLoosePartsOnLoadPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}

}
