
package xyz.openautomaker.base.printerControl.model.statetransitions.calibration;

import javafx.beans.property.ReadOnlyDoubleProperty;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;

/**
 *
 * @author tony
 */
public class SingleNozzleHeightStateTransitionManager extends StateTransitionManager<SingleNozzleHeightCalibrationState>
{

    public SingleNozzleHeightStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, SingleNozzleHeightCalibrationState.IDLE,
              SingleNozzleHeightCalibrationState.CANCELLING, SingleNozzleHeightCalibrationState.CANCELLED,
              SingleNozzleHeightCalibrationState.FAILED);
    }

    public ReadOnlyDoubleProperty getZcoProperty()
    {
        return ((CalibrationSingleNozzleHeightActions) actions).getZcoGUITProperty();
    }

}
