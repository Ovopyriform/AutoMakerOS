package celtech.coreUI.controllers.panels.userpreferences;

import java.util.Locale;

import org.openautomaker.environment.I18N;
import org.openautomaker.environment.preference.LocalePreference;

import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.util.Callback;

/**
 *
 * @author Ian
 */
public class LanguagePreferenceController implements PreferencesInnerPanelController.Preference {

	private final ComboBox<Locale> fControl;

	private final LocalePreference fAppLocale;

	/**
	 * Control how locals are displayed
	 */
	private class LocaleListCell extends ListCell<Locale> {
		@Override
		protected void updateItem(Locale item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty)
				setText(item.getDisplayName(fAppLocale.get()));
		}
	}

	public LanguagePreferenceController() {

		fAppLocale = new LocalePreference();

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();

		fControl.setItems(FXCollections.observableList(fAppLocale.values()));

		// Set up display
		Callback<ListView<Locale>, ListCell<Locale>> cellFactory = (listView) -> new LocaleListCell();
		fControl.setButtonCell(cellFactory.call(null));
		fControl.setCellFactory(cellFactory);

		SelectionModel<Locale> selectionModel = fControl.getSelectionModel();

		// Set initial value
		selectionModel.select(fAppLocale.get());

		// Listen for changes
		selectionModel.selectedItemProperty()
				.addListener((ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) -> {
					fAppLocale.set(newValue);
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
		return new I18N().t("preferences.language");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}
}
