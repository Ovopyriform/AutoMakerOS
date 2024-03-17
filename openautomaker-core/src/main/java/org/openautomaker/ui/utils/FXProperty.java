package org.openautomaker.ui.utils;

import org.openautomaker.environment.preference.AbsBooleanPreference;
import org.openautomaker.environment.preference.AbsFloatPreference;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;

public final class FXProperty {

	/**
	 * Binds a Boolean Preference to a JavaFX property
	 * 
	 * @param booleanPreference - The preference to bind
	 * @return BooleanProperty bound to preference
	 */
	public static BooleanProperty bind(AbsBooleanPreference booleanPreference) {
		BooleanProperty booleanProperty = new SimpleBooleanProperty(booleanPreference.get());

		booleanPreference.addChangeListener((evt) -> {
			booleanProperty.set(booleanPreference.get());
		});

		booleanProperty.addListener((observable, oldValue, newValue) -> {
			booleanPreference.set(newValue);
		});

		return booleanProperty;
	}

	/**
	 * Binds a Float Preference to a JavaFX property
	 * 
	 * @param floatPreference - the preference to bind
	 * @return FloatProperty bound to preference
	 */
	public static FloatProperty bind(AbsFloatPreference floatPreference) {
		FloatProperty floatProperty = new SimpleFloatProperty(floatPreference.get());

		floatPreference.addChangeListener((evt) -> {
			floatProperty.set(floatPreference.get());
		});

		floatProperty.addListener((observable, oldValue, newValue) -> {
			floatPreference.set((Float) newValue);
		});

		return floatProperty;
	}
}
