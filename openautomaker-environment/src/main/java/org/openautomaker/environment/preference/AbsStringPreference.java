package org.openautomaker.environment.preference;

import java.util.List;

public abstract class AbsStringPreference extends AbsPreference<String> {

	public AbsStringPreference() {
		super();
	}

	@Override
	public List<String> values() {
		throw new UnsupportedOperationException("values not implemented for base string preference");
	}

	@Override
	public String get() {
		return getUserNode().get(getKey(), getDefault());
	}

	@Override
	public void set(String value) {
		getUserNode().put(getKey(), value);
	}

	public abstract String getDefault();

}
