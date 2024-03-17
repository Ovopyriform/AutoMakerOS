package org.openautomaker.environment.preference.virtual_printer;

import java.util.List;

import org.openautomaker.environment.PrinterType;
import org.openautomaker.environment.preference.AbsPreference;

public class VirtualPrinterTypePreference extends AbsPreference<PrinterType> {

	public VirtualPrinterTypePreference() {
		super();
	}

	@Override
	public List<PrinterType> values() {
		return List.of(PrinterType.values());
	}

	@Override
	public PrinterType get() {
		return PrinterType.valueOf(getUserNode().get(getKey(), PrinterType.ROBOX_PRO.name()));
	}

	@Override
	public void set(PrinterType printerType) {
		getUserNode().put(getKey(), printerType.name());
	}
}
