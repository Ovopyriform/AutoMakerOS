package org.openautomaker.environment.preference;

import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

public abstract class AbsPreference<T> {

	private Preferences fUserNode = null;
	private Preferences fSystemNode = null;

	public AbsPreference() {

	}

	public Preferences getUserNode() {
		if (fUserNode == null)
			fUserNode = Preferences.userNodeForPackage(getClass());

		return fUserNode;
	}

	public Preferences getSystemNode() {
		if (fSystemNode == null)
			fSystemNode = Preferences.systemNodeForPackage(getClass());

		return fSystemNode;
	}

	public String getKey() {
		return getClass().getSimpleName().replace("Preference", "");
	}

	/**
	 * Wrapper method for Preference Change Listeners. Automatically uses the preferences key to determine if the change is relevant to the preference.
	 * 
	 * @param pcl - PreferenceChangeListener to handle the preference change.
	 */
	public void addChangeListener(PreferenceChangeListener pcl) {
		getUserNode().addPreferenceChangeListener(new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				if (getKey().equals(evt.getKey()))
					pcl.preferenceChange(evt);
			}
		});
	}

	// Get a list of the applicable values
	public abstract List<T> values();

	// Get the value
	public abstract T get();

	// Set the value
	public abstract void set(T value);

}
