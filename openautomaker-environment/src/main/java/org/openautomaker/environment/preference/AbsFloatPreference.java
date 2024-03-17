package org.openautomaker.environment.preference;

import java.util.List;

public abstract class AbsFloatPreference extends AbsPreference<Float> {

	@Override
	public List<Float> values() {
		throw new UnsupportedOperationException("values not implemented for base float preference");
	}

	@Override
	public Float get() {
		return getUserNode().getFloat(getKey(), getDefault());
	}

	@Override
	public void set(Float value) {
		getUserNode().putFloat(getKey(), value);
	}

	public abstract Float getDefault();

}
