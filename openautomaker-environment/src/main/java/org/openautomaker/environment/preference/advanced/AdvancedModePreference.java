package org.openautomaker.environment.preference.advanced;

import org.openautomaker.environment.preference.AbsBooleanPreference;

/**
 * Preference representing if the user has selected the Advanced Mode
 */
public class AdvancedModePreference extends AbsBooleanPreference {

	public AdvancedModePreference() {
		super();

		// Chain the other advanced preferences from this preference
		addChangeListener((evt) -> {
			Boolean value = get();
			new ShowDiagnosticsPreference().set(value);
			new ShowGCodeConsolePreference().set(value);
			new ShowAdjustmentsPreference().set(value);
			new ShowSnapshotPreference().set(value);
		});
	}

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}
}
