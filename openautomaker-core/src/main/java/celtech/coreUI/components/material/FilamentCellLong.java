/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.components.material;

import xyz.openautomaker.base.configuration.Filament;
import xyz.openautomaker.base.configuration.datafileaccessors.FilamentContainer;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author tony
 */
public class FilamentCellLong extends FilamentCell {
	public FilamentCellLong() {
		super();
	}

	@Override
	protected void updateItem(Filament item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null && !empty
				&& item != FilamentContainer.UNKNOWN_FILAMENT) {
			Filament filament = item;
			setGraphic(cellContainer);
			rectangle.setVisible(true);
			rectangle.setFill(filament.getDisplayColour());

			if (filament.getMaterial() != null) {
				label.setText(filament.getLongFriendlyName() + " "
						+ filament.getMaterial().getFriendlyName());
			}
			else {
				label.setText(filament.getLongFriendlyName());
			}
		}
		else if (item == FilamentContainer.UNKNOWN_FILAMENT) {
			Filament filament = item;
			setGraphic(cellContainer);
			rectangle.setVisible(false);

			label.setText(filament.getLongFriendlyName());
		}
		else {
			setGraphic(null);
			label.setText(OpenAutoMakerEnv.getI18N().t("materialComponent.unknown"));
		}
	}
}
