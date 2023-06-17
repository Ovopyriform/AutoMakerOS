/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.printerControl.model.statetransitions.calibration;

import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionManager;

/**
 *
 * @author tony
 */
public class XAndYStateTransitionManager extends StateTransitionManager<CalibrationXAndYState>
{

    public XAndYStateTransitionManager(StateTransitionActionsFactory stateTransitionActionsFactory,
        TransitionsFactory transitionsFactory)
    {
        super(stateTransitionActionsFactory, transitionsFactory, CalibrationXAndYState.IDLE, 
                      CalibrationXAndYState.CANCELLING, CalibrationXAndYState.CANCELLED,
                      CalibrationXAndYState.FAILED);
    }

    public void setXOffset(String xOffset)
    {
        ((CalibrationXAndYActions) actions).setXOffset(xOffset);
    }

    public void setYOffset(int yOffset)
    {
        ((CalibrationXAndYActions) actions).setYOffset(yOffset);
    }
}
