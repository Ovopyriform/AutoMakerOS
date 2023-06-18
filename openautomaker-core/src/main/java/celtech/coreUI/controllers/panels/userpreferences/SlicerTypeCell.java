package celtech.coreUI.controllers.panels.userpreferences;

import javafx.scene.control.ListCell;
import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author George Salter
 */
public class SlicerTypeCell extends ListCell<SlicerType> {

	@Override
	protected void updateItem(SlicerType slicerType, boolean empty) {
		super.updateItem(slicerType, empty);
		setStyle("-fx-text-fill:black"); // TODO: This can probably go in the ml file now

		// Change to friendly name?
		setText(slicerType == null ? "" : slicerType.getFriendlyName());
	}
}
