package celtech.coreUI.controllers.panels.userpreferences;

import org.openautomaker.environment.I18N;
import org.openautomaker.environment.PrinterType;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterTypePreference;

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
 */
public class CustomPrinterTypePreferenceController implements PreferencesInnerPanelController.Preference {

	private final ComboBox<PrinterType> fControl;

	private final VirtualPrinterTypePreference fVirtualPrinterTypePreference;

	/**
	 * Custom ListCell implementation to display the correct text
	 */
	private class VirtualPrinterTypeListCell extends ListCell<PrinterType> {
		@Override
		protected void updateItem(PrinterType item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty)
				setText(item.getDisplayName());
		}

	}

	public CustomPrinterTypePreferenceController() {

		fVirtualPrinterTypePreference = new VirtualPrinterTypePreference();

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();

		fControl.setItems(FXCollections.observableList(fVirtualPrinterTypePreference.values()));

		// Setup display
		Callback<ListView<PrinterType>, ListCell<PrinterType>> cellFactory = (listView) -> new VirtualPrinterTypeListCell();
		fControl.setButtonCell(cellFactory.call(null));
		fControl.setCellFactory(cellFactory);

		SelectionModel<PrinterType> selectionModel = fControl.getSelectionModel();

		// Set initial value
		selectionModel.select(fVirtualPrinterTypePreference.get());

		// Listen for changes
		selectionModel.selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					fVirtualPrinterTypePreference.set(newValue);
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
		return new I18N().t("preferences.printerType");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}

}
