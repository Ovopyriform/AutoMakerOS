/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.printerControl.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ObservableValue;

/**
 *
 * @author tony
 */
class ReelChangesNotifier
{

    List<ReelChangesListener> listeners = new ArrayList<>();
    
    ReelChangesNotifier(Reel reel)
    {
        reel.dataChangedToggleProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) ->
        {
            fireReelChanged();
        });
    }
    
    private void fireReelChanged() {
        for (ReelChangesListener listener : listeners)
        {
            listener.whenReelChanged();
        }
    }
    
    public void addListener(ReelChangesListener listener)
    {
        this.listeners.add(listener);
    }

    void removeListener(ReelChangesListener listener)
    {
        this.listeners.remove(listener);
    }    
    
}
