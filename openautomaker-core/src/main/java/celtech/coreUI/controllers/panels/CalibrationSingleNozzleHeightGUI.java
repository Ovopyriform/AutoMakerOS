/*
 * Copyright 2014 CEL UK
 */
package celtech.coreUI.controllers.panels;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.configuration.ApplicationConfiguration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Region;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransition;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager.GUIName;
import xyz.openautomaker.base.printerControl.model.statetransitions.calibration.SingleNozzleHeightCalibrationState;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author tony
 */
public class CalibrationSingleNozzleHeightGUI
{

	private static final Logger LOGGER = LogManager.getLogger(
			CalibrationSingleNozzleHeightGUI.class.getName());

	private CalibrationInsetPanelController controller;
	StateTransitionManager<SingleNozzleHeightCalibrationState> stateManager;
	Map<GUIName, Region> namesToButtons = new HashMap<>();

	public CalibrationSingleNozzleHeightGUI(CalibrationInsetPanelController controller,
			StateTransitionManager<SingleNozzleHeightCalibrationState> stateManager)
	{
		this.controller = controller;
		this.stateManager = stateManager;

		stateManager.stateGUITProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				setState((SingleNozzleHeightCalibrationState) newValue);
			}
		});
		populateNamesToButtons(controller);
	}

	private void showAppropriateButtons(SingleNozzleHeightCalibrationState state)
	{
		controller.hideAllInputControlsExceptStepNumber();
		if (state.showCancelButton())
		{
			controller.cancelCalibrationButton.setVisible(true);
		}
		for (StateTransition<SingleNozzleHeightCalibrationState> allowedTransition : this.stateManager.getTransitions())
		{
			if (namesToButtons.containsKey(allowedTransition.getGUIName()))
			{
				namesToButtons.get(allowedTransition.getGUIName()).setVisible(true);
			}
		}
	}

	public void setState(SingleNozzleHeightCalibrationState state)
	{
		LOGGER.debug("GUI going to state " + state);
		controller.calibrationStatus.replaceText(state.getStepTitle());
		showAppropriateButtons(state);
		if (state.getDiagramName().isPresent())
		{
			URL fxmlURL = getClass().getResource(
					ApplicationConfiguration.fxmlDiagramsResourcePath
					+ "singlenozzleheight" + "/" + state.getDiagramName().get());

			controller.showDiagram(fxmlURL);
		}
		int stepNo = 0;
		switch (state)
		{
		case IDLE:
			break;
		case INITIALISING:
			controller.calibrationMenu.disableNonSelectedItems();
			stepNo = 1;
			break;
		case HEATING:
			controller.showSpinner();
			stepNo = 2;
			break;
		case HEAD_CLEAN_CHECK:
			stepNo = 3;
			break;
		case INSERT_PAPER:
			stepNo = 4;
			break;
		case PROBING:
			stepNo = 5;
			break;
		case BRING_BED_FORWARD:
			break;
		case REPLACE_PEI_BED:
			stepNo = 6;
			break;
		case DONE:
			break;
		case CANCELLED:
			controller.resetMenuAndGoToChoiceMode();
			break;
		case FINISHED:
			controller.calibrationMenu.reset();
			break;
		case FAILED:
			controller.calibrationMenu.enableNonSelectedItems();
			break;
		}
		if (stepNo != 0)
		{
			controller.stepNumber.setText(String.format(OpenAutoMakerEnv.getI18N().t("calibrationPanel.stepXOfY"), stepNo, 6));
		}
	}

	private void populateNamesToButtons(CalibrationInsetPanelController controller)
	{
		namesToButtons.put(GUIName.YES, controller.buttonA);
		namesToButtons.put(GUIName.NO, controller.buttonB);
		namesToButtons.put(GUIName.NEXT, controller.nextButton);
		namesToButtons.put(GUIName.RETRY, controller.retryPrintButton);
		namesToButtons.put(GUIName.START, controller.startCalibrationButton);
		namesToButtons.put(GUIName.BACK, controller.backToStatus);
	}

}
