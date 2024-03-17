
package org.openautomaker.base.printerControl.model.statetransitions.calibration;

import org.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;

import javafx.beans.property.ReadOnlyFloatProperty;

/**
 *
 * @author tony
 */
public class NozzleOpeningStateTransitionManager extends StateTransitionManager<NozzleOpeningCalibrationState>
{

    public NozzleOpeningStateTransitionManager(
        StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, NozzleOpeningCalibrationState.IDLE,
              NozzleOpeningCalibrationState.CANCELLING, NozzleOpeningCalibrationState.CANCELLED,
              NozzleOpeningCalibrationState.FAILED);
    }

    public ReadOnlyFloatProperty getBPositionProperty()
    {
        return ((CalibrationNozzleOpeningActions) actions).getBPositionGUITProperty();
    }

}
