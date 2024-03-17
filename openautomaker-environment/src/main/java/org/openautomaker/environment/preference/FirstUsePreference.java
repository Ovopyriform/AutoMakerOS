package org.openautomaker.environment.preference;

/**
 * Preference representing if this is the first use of the package
 */
public class FirstUsePreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}

}
