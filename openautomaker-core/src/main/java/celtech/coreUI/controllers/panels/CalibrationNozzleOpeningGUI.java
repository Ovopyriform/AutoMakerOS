
package celtech.coreUI.controllers.panels;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.printerControl.model.statetransitions.StateTransition;
import org.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;
import org.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import org.openautomaker.base.printerControl.model.statetransitions.calibration.NozzleOpeningCalibrationState;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.configuration.ApplicationConfiguration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;

/**
 *
 * @author tony
 */
public class CalibrationNozzleOpeningGUI {

	private static final Logger LOGGER = LogManager.getLogger(
			CalibrationNozzleOpeningGUI.class.getName());

	private final CalibrationInsetPanelController controller;
	StateTransitionManager<NozzleOpeningCalibrationState> stateManager;
	Map<GUIName, Region> namesToButtons = new HashMap<>();

	public CalibrationNozzleOpeningGUI(CalibrationInsetPanelController controller,
			StateTransitionManager<NozzleOpeningCalibrationState> stateManager) {
		this.controller = controller;
		this.stateManager = stateManager;

		stateManager.stateGUITProperty().addListener(new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue) {
				setState((NozzleOpeningCalibrationState) newValue);
			}
		});
		populateNamesToButtons(controller);
	}

	private void showAppropriateButtons(NozzleOpeningCalibrationState state) {
		controller.hideAllInputControlsExceptStepNumber();
		if (state.showCancelButton()) {
			controller.cancelCalibrationButton.setVisible(true);
		}
		for (StateTransition<NozzleOpeningCalibrationState> allowedTransition : this.stateManager.getTransitions()) {
			if (namesToButtons.containsKey(allowedTransition.getGUIName())) {
				namesToButtons.get(allowedTransition.getGUIName()).setVisible(true);
			}
		}
	}

	public void setState(NozzleOpeningCalibrationState state) {
		LOGGER.debug("GUI going to state " + state);
		controller.calibrationStatus.replaceText(state.getStepTitle());
		showAppropriateButtons(state);
		if (state.getDiagramName().isPresent()) {
			URL fxmlURL = getClass().getResource(
					ApplicationConfiguration.fxmlDiagramsResourcePath
							+ "nozzleopening" + "/" + state.getDiagramName().get());

			controller.showDiagram(fxmlURL);
		}
		int stepNo = 0;
		switch (state) {
			case IDLE:
				break;
			case HEATING:
				controller.showSpinner();
				controller.calibrationMenu.disableNonSelectedItems();
				stepNo = 1;
				break;
			case HEAD_CLEAN_CHECK_BEFORE_LEAK_TEST:
				stepNo = 2;
				break;
			case NO_MATERIAL_CHECK:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("misc.Yes"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("misc.No"));
				stepNo = 3;
				break;
			case T0_EXTRUDING:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("misc.Yes"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("misc.No"));
				stepNo = 4;
				break;
			case T1_EXTRUDING:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("misc.Yes"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("misc.No"));
				stepNo = 5;
				break;
			case HEAD_CLEAN_CHECK_AFTER_EXTRUDE:
				stepNo = 6;
				break;
			case PRE_CALIBRATION_PRIMING_FINE:
				break;
			case CALIBRATE_FINE_NOZZLE:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("calibrationPanel.present"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("calibrationPanel.notPresent"));
				stepNo = 7;
				break;
			case CALIBRATE_FILL_NOZZLE:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("calibrationPanel.present"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("calibrationPanel.notPresent"));
				stepNo = 8;
				break;
			case HEAD_CLEAN_CHECK_FILL_NOZZLE:
				stepNo = 9;
				break;
			case CONFIRM_NO_MATERIAL_NO_YESNO_BUTTONS:
			case CONFIRM_NO_MATERIAL:
				controller.buttonA.setText(OpenAutomakerEnv.getI18N().t("misc.Yes"));
				controller.buttonB.setText(OpenAutomakerEnv.getI18N().t("misc.No"));
				stepNo = 10;
				break;
			case FINISHED:
				controller.calibrationMenu.reset();
				break;
			case CANCELLED:
				controller.resetMenuAndGoToChoiceMode();
				break;
			case FAILED:
				controller.calibrationMenu.enableNonSelectedItems();
				break;
		}
		if (stepNo != 0) {
			controller.stepNumber.setText(String.format(OpenAutomakerEnv.getI18N().t("calibrationPanel.stepXOf10"), stepNo));
		}
	}

	private void populateNamesToButtons(CalibrationInsetPanelController controller) {
		namesToButtons.put(GUIName.A_BUTTON, controller.buttonA);
		namesToButtons.put(GUIName.B_BUTTON, controller.buttonB);
		namesToButtons.put(GUIName.NEXT, controller.nextButton);
		namesToButtons.put(GUIName.RETRY, controller.retryPrintButton);
		namesToButtons.put(GUIName.START, controller.startCalibrationButton);
		namesToButtons.put(GUIName.BACK, controller.backToStatus);
	}

}
