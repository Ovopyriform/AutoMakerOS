package org.openautomaker.environment.preference;

/**
 * Preference to determine if we should show the GCode preview panel
 */
public class ShowGCodePreviewPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
