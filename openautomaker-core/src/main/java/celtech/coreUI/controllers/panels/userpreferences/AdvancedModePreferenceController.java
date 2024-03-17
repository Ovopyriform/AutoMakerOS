package celtech.coreUI.controllers.panels.userpreferences;

import org.openautomaker.base.BaseLookup;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.preference.advanced.AdvancedModePreference;

import celtech.coreUI.controllers.panels.PreferencesInnerPanelController.Preference;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;

public class AdvancedModePreferenceController implements Preference {

	private final AdvancedModePreference fAdvancedModePreference;
	private final CheckBox fControl;

	public AdvancedModePreferenceController() {
		fAdvancedModePreference = new AdvancedModePreference();

		fControl = new CheckBox();
		fControl.setPrefWidth(150);
		fControl.setMinWidth(fControl.getPrefWidth());

		BooleanProperty booleanProperty = fControl.selectedProperty();
		booleanProperty.setValue(fAdvancedModePreference.get());

		// Confirm if they user wants to go to advanced mode.
		booleanProperty.addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				fAdvancedModePreference.set(newValue);
				return;
			}

			// Ask the user whether they really want to do this..
			boolean confirmAdvancedMode = BaseLookup.getSystemNotificationHandler().confirmAdvancedMode();

			// If we're switching, set the preference
			if (confirmAdvancedMode)
				fAdvancedModePreference.set(confirmAdvancedMode);

			// If we're cancelling, set the control back to false
			if (!confirmAdvancedMode)
				booleanProperty.setValue(confirmAdvancedMode);
		});
	}

	@Override
	public void updateValueFromControl() {
	}

	@Override
	public void populateControlWithCurrentValue() {
	}

	@Override
	public Control getControl() {
		return fControl;
	}

	@Override
	public String getDescription() {
		return new I18N().t("preferences.advancedMode");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}

	public BooleanProperty getSelectedProperty() {
		return fControl.selectedProperty();
	}
}
