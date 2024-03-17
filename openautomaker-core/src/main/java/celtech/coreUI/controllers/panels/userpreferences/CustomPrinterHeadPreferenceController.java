package celtech.coreUI.controllers.panels.userpreferences;

import org.openautomaker.base.configuration.datafileaccessors.HeadContainer;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterHeadPreference;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.util.Callback;

/**
 *
 * @author George Salter
 */
public class CustomPrinterHeadPreferenceController implements PreferencesInnerPanelController.Preference {

	private final ComboBox<String> fControl;

	private final VirtualPrinterHeadPreference fVirtualPrinterHeadPreference;

	private final BiMap<String, String> fHeadDisplayNameMap;

	/**
	 * Implementation of ListCell to control displayed text
	 */
	private class CustomerPrinterHeadListCell extends ListCell<String> {
		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty)
				if (fHeadDisplayNameMap.containsKey(item))
					setText(fHeadDisplayNameMap.get(item));
				else
					setText(item);
		}
	}

	//TODO: These shouldn't be defined in the combo box.  Should be a separate head loader module which sort out all this stuff.
	public CustomPrinterHeadPreferenceController() {
		fVirtualPrinterHeadPreference = new VirtualPrinterHeadPreference();

		//TODO: Known heads.  Shouldn't be defined here
		fHeadDisplayNameMap = HashBiMap.create();
		fHeadDisplayNameMap.put("RBX01-SM", "QuickFill\u2122");
		fHeadDisplayNameMap.put("RBX01-S2", "QuickFill\u2122 v2");
		fHeadDisplayNameMap.put("RBX01-DM", "DualMaterial\u2122");
		fHeadDisplayNameMap.put("RBXDV-S1", "SingleX\u2122 Experimental\u2122");
		fHeadDisplayNameMap.put("RBXDV-S3", "SingleLite\u2122");

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();

		HeadContainer.getCompleteHeadList().forEach(headFile -> fControl.getItems().add(headFile.getTypeCode()));

		// Setup display
		Callback<ListView<String>, ListCell<String>> cellFactory = (listView) -> new CustomerPrinterHeadListCell();
		fControl.setButtonCell(cellFactory.call(null));
		fControl.setCellFactory(cellFactory);

		SelectionModel<String> selectionModel = fControl.getSelectionModel();

		// Set up initial value
		selectionModel.select(fVirtualPrinterHeadPreference.get());

		// Listen for changes
		selectionModel.selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					fVirtualPrinterHeadPreference.set(newValue);
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
		return new I18N().t("preferences.printerHead");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}

}
