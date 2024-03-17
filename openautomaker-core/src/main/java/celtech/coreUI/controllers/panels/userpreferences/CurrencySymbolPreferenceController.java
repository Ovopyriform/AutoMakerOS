package celtech.coreUI.controllers.panels.userpreferences;

import org.openautomaker.environment.CurrencySymbol;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.preference.CurrencySymbolPreference;

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
public class CurrencySymbolPreferenceController implements PreferencesInnerPanelController.Preference {

	private final ComboBox<CurrencySymbol> fControl;

	private final CurrencySymbolPreference fCurrencySymbolPreference;

	private class CurrencySymbolListCell extends ListCell<CurrencySymbol> {
		@Override
		protected void updateItem(CurrencySymbol symbol, boolean empty) {
			super.updateItem(symbol, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			}
			else {
				setText(symbol.getDisplayString());
			}
		}
	}

	public CurrencySymbolPreferenceController() {

		fCurrencySymbolPreference = new CurrencySymbolPreference();

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();

		fControl.setItems(FXCollections.observableList(fCurrencySymbolPreference.values()));

		// Setup display
		Callback<ListView<CurrencySymbol>, ListCell<CurrencySymbol>> cellFactory = (listView) -> new CurrencySymbolListCell();
		fControl.setCellFactory(cellFactory);
		fControl.setButtonCell(cellFactory.call(null));
		
		SelectionModel<CurrencySymbol> selectionModel = fControl.getSelectionModel();

		//Set initial value
		selectionModel.select(fCurrencySymbolPreference.get());

		//Listen for changes
		selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			fCurrencySymbolPreference.set(newValue);
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
		return new I18N().t("preferences.currencySymbol");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}
}
