package org.openautomaker.environment.preference;

/**
 * Preference to determine if we should search for remote cameras
 */
public class SearchForRemoteCamerasPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.TRUE;
	}

}
