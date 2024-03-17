package org.openautomaker.environment.preference;

import java.util.List;

/**
 * Abstract boolean preference
 */
public abstract class AbsBooleanPreference extends AbsPreference<Boolean> {

	public AbsBooleanPreference() {
		super();
	}

	@Override
	public List<Boolean> values() {
		return List.of(Boolean.TRUE, Boolean.FALSE);
	}

	@Override
	public Boolean get() {
		return Boolean.valueOf(getUserNode().getBoolean(getKey(), getDefault()));
	}

	@Override
	public void set(Boolean value) {
		getUserNode().putBoolean(getKey(), value);
	}

	/**
	 * The default value for this boolean preference
	 * 
	 * @return Boolean default value.
	 */
	protected abstract Boolean getDefault();
}
