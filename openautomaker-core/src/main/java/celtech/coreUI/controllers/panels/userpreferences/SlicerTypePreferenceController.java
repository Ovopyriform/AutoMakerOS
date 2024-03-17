package celtech.coreUI.controllers.panels.userpreferences;

import org.openautomaker.environment.I18N;
import org.openautomaker.environment.Slicer;
import org.openautomaker.environment.preference.SlicerPreference;

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
public class SlicerTypePreferenceController implements PreferencesInnerPanelController.Preference {

	private final ComboBox<Slicer> fControl;

	private final SlicerPreference slicerType;

	/**
	 * Custom display for combo box cell
	 */
	private class SlicerListCell extends ListCell<Slicer> {
		@Override
		protected void updateItem(Slicer item, boolean empty) {
			super.updateItem(item, empty);
			if (!empty)
				setText(item.getFriendlyName());
		}
	}

	public SlicerTypePreferenceController() {
		slicerType = new SlicerPreference();

		fControl = new ComboBox<>();
		fControl.getStyleClass().add("cmbCleanCombo");
		fControl.setMinWidth(200);
		fControl.autosize();

		fControl.setItems(FXCollections.observableList(slicerType.values()));

		Callback<ListView<Slicer>, ListCell<Slicer>> cellFactory = (listView) -> new SlicerListCell();
		fControl.setButtonCell(cellFactory.call(null));
		fControl.setCellFactory(cellFactory);

		SelectionModel<Slicer> selectionModel = fControl.getSelectionModel();

		// Set initial value
		selectionModel.select(slicerType.get());

		//Listen for Changes.
		selectionModel.selectedItemProperty()
				.addListener((ObservableValue<? extends Slicer> observable, Slicer oldValue, Slicer newValue) -> {
					slicerType.set(newValue);
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
		return new I18N().t("preferences.slicerType");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty) {
		fControl.disableProperty().unbind();
		fControl.disableProperty().bind(disableProperty);
	}
}
