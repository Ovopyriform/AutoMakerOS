/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.printerControl.model.statetransitions.calibration;

import javafx.beans.property.ReadOnlyDoubleProperty;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;

/**
 *
 * @author tony
 */
public class NozzleHeightStateTransitionManager extends StateTransitionManager<NozzleHeightCalibrationState>
{

    public NozzleHeightStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, NozzleHeightCalibrationState.IDLE,
              NozzleHeightCalibrationState.CANCELLING, NozzleHeightCalibrationState.CANCELLED,
              NozzleHeightCalibrationState.FAILED);
    }

    public ReadOnlyDoubleProperty getZcoProperty()
    {
        return ((CalibrationNozzleHeightActions) actions).getZcoGUITProperty();
    }

}
