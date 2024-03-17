package celtech.coreUI.controllers.panels.userpreferences;

import org.apache.logging.log4j.Level;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.preference.LogLevelPreference;

import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.SelectionModel;

/**
 *
 */
public class LogLevelPreferenceController implements PreferencesInnerPanelController.Preference {


	private final ComboBox<Level> fControl;
	private final LogLevelPreference fLogLevel;

	public LogLevelPreferenceController() {
		fLogLevel = new LogLevelPreference();

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();
		fControl.setItems(FXCollections.observableList(fLogLevel.values()));

		SelectionModel<Level> selectionModel = fControl.getSelectionModel();

		// Set initial value
		selectionModel.select(fLogLevel.get());

		//Listen for changes
		selectionModel.selectedItemProperty()
				.addListener((ObservableValue<? extends Level> observable, Level oldValue, Level newValue) -> {
					fLogLevel.set(newValue);
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
		return new I18N().t("preferences.logLevel");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}
}
