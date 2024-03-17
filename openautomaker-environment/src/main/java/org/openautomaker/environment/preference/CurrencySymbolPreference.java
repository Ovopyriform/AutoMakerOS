package org.openautomaker.environment.preference;

import java.util.List;

import org.openautomaker.environment.CurrencySymbol;

public class CurrencySymbolPreference extends AbsPreference<CurrencySymbol> {

	public CurrencySymbolPreference() {
		super();
	}

	@Override
	public List<CurrencySymbol> values() {
		return List.of(CurrencySymbol.values());
	}

	@Override
	public CurrencySymbol get() {
		return CurrencySymbol.valueOf(getUserNode().get(getKey(), CurrencySymbol.POUND.name()));
	}

	@Override
	public void set(CurrencySymbol value) {
		getUserNode().put(getKey(), value.name());
	}

}
