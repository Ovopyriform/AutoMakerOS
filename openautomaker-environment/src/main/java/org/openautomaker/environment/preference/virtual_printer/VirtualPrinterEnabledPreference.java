package org.openautomaker.environment.preference.virtual_printer;

import org.openautomaker.environment.preference.AbsBooleanPreference;

public class VirtualPrinterEnabledPreference extends AbsBooleanPreference {

	@Override
	protected Boolean getDefault() {
		return Boolean.FALSE;
	}

}
