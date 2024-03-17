
package celtech.coreUI.controllers.panels;

import org.openautomaker.base.printerControl.model.Head;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.Lookup;
import celtech.coreUI.components.VerticalMenu;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author tony
 */
public class CalibrationMenuConfiguration {

	public BooleanProperty nozzleOpeningCalibrationEnabled = new SimpleBooleanProperty(false);
	public BooleanProperty nozzleHeightCalibrationEnabled = new SimpleBooleanProperty(false);
	public BooleanProperty xyAlignmentCalibrationEnabled = new SimpleBooleanProperty(false);
	public BooleanProperty nozzleOpeningCalibrationRequired = new SimpleBooleanProperty(false);
	public BooleanProperty xyAlignmentCalibrationRequired = new SimpleBooleanProperty(false);

	private final boolean displayOpening;
	private final boolean displayHeight;
	private final boolean displayAlignment;

	public Printer currentlySelectedPrinter;

	public CalibrationMenuConfiguration(boolean displayOpening,
			boolean displayHeight,
			boolean displayAlignment) {
		this.displayOpening = displayOpening;
		this.displayHeight = displayHeight;
		this.displayAlignment = displayAlignment;
	}

	public void configureCalibrationMenu(VerticalMenu calibrationMenu,
			CalibrationInsetPanelController calibrationInsetPanelController) {

		if (currentlySelectedPrinter != null) {
			if (currentlySelectedPrinter.headProperty().get() != null) {
				nozzleOpeningCalibrationRequired.bind(currentlySelectedPrinter.headProperty().get().valveTypeProperty().isEqualTo(Head.ValveType.FITTED));
				xyAlignmentCalibrationRequired.bind(currentlySelectedPrinter.headProperty().get().valveTypeProperty().isEqualTo(Head.ValveType.FITTED));
			}
			else {
				nozzleOpeningCalibrationRequired.set(false);
				xyAlignmentCalibrationRequired.set(false);
			}

			nozzleOpeningCalibrationEnabled.bind(
					currentlySelectedPrinter.canCalibrateNozzleOpeningProperty());
			nozzleHeightCalibrationEnabled.bind(
					currentlySelectedPrinter.canCalibrateNozzleHeightProperty());
			xyAlignmentCalibrationEnabled.bind(
					currentlySelectedPrinter.canCalibrateXYAlignmentProperty());
		}

		calibrationMenu.setTitle(OpenAutomakerEnv.getI18N().t("calibrationPanel.title"));

		if (displayOpening) {
			VerticalMenu.NoArgsVoidFunc doOpeningCalibration = () -> {
				calibrationInsetPanelController.setCalibrationMode(
						CalibrationMode.NOZZLE_OPENING);
			};
			calibrationMenu.addItem(OpenAutomakerEnv.getI18N().t("calibrationMenu.nozzleOpening"),
					doOpeningCalibration, nozzleOpeningCalibrationEnabled);
		}

		if (displayHeight) {
			VerticalMenu.NoArgsVoidFunc doHeightCalibration = () -> {
				calibrationInsetPanelController.setCalibrationMode(
						CalibrationMode.NOZZLE_HEIGHT);
			};
			calibrationMenu.addItem(OpenAutomakerEnv.getI18N().t("calibrationMenu.nozzleHeight"),
					doHeightCalibration, nozzleHeightCalibrationEnabled);
		}

		if (displayAlignment) {
			VerticalMenu.NoArgsVoidFunc doXYAlignmentCalibration = () -> {
				calibrationInsetPanelController.setCalibrationMode(
						CalibrationMode.X_AND_Y_OFFSET);
			};
			calibrationMenu.addItem(OpenAutomakerEnv.getI18N().t("calibrationMenu.nozzleAlignment"),
					doXYAlignmentCalibration, xyAlignmentCalibrationEnabled);
		}

		Lookup.getSelectedPrinterProperty().addListener(selectedPrinterListener);
	}

	private ChangeListener<Printer> selectedPrinterListener = (ObservableValue<? extends Printer> observable, Printer oldValue, Printer newPrinter) -> {
		currentlySelectedPrinter = newPrinter;
		nozzleOpeningCalibrationRequired.unbind();
		nozzleOpeningCalibrationRequired.set(false);
		xyAlignmentCalibrationRequired.unbind();
		xyAlignmentCalibrationRequired.set(false);
		nozzleOpeningCalibrationEnabled.unbind();
		nozzleOpeningCalibrationEnabled.set(false);
		nozzleHeightCalibrationEnabled.unbind();
		nozzleHeightCalibrationEnabled.set(false);
		xyAlignmentCalibrationEnabled.unbind();
		xyAlignmentCalibrationEnabled.set(false);

		if (newPrinter != null) {
			if (newPrinter.headProperty().get() != null) {
				nozzleOpeningCalibrationRequired.bind(newPrinter.headProperty().get().valveTypeProperty().isEqualTo(Head.ValveType.FITTED));
				xyAlignmentCalibrationRequired.bind(newPrinter.headProperty().get().valveTypeProperty().isEqualTo(Head.ValveType.FITTED));
			}
			nozzleOpeningCalibrationEnabled.bind(newPrinter.canCalibrateNozzleOpeningProperty());
			nozzleHeightCalibrationEnabled.bind(newPrinter.canCalibrateNozzleHeightProperty());
			xyAlignmentCalibrationEnabled.bind(newPrinter.canCalibrateXYAlignmentProperty());
		}
	};

}
