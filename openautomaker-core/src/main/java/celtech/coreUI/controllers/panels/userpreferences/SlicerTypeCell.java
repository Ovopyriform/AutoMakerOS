package celtech.coreUI.controllers.panels.userpreferences;

import javafx.scene.control.ListCell;
import xyz.openautomaker.base.ApplicationFeature;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author George Salter
 */
public class SlicerTypeCell extends ListCell<SlicerType> {

	@Override
	protected void updateItem(SlicerType slicerType, boolean empty) {
		super.updateItem(slicerType, empty);

		if (slicerType != null && (SlicerType.Cura4.equals(slicerType) || SlicerType.Cura5.equals(slicerType))) {
			if (BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.LATEST_CURA_VERSION)) {
				setStyle("-fx-text-fill:black");
			}
			else {
				setStyle("-fx-text-fill:grey");
			}
		}

		// Change to friendly name?
		setText(slicerType == null ? "" : slicerType.name());
	}
}
