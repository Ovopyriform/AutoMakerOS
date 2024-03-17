package org.openautomaker.environment.preference.virtual_printer;

import java.util.List;

import org.openautomaker.environment.preference.AbsStringPreference;

public class VirtualPrinterHeadPreference extends AbsStringPreference {

	/**
	 * TODO: This should evaluate to the list of heads loaded from the config files.
	 * 
	 * Loading of heads should probably be a separate module.
	 */
	@Override
	public List<String> values() {
		return List.of();
	}

	@Override
	public String getDefault() {
		return "RBX01-SM";
	}
}
