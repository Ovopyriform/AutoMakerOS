/*
 * Copyright 2015 CEL UK
 */
package org.openautomaker.base.utils.tasks;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author tony
 */
public class SimpleCancellable implements Cancellable
{
    private BooleanProperty cancelled = new SimpleBooleanProperty(false);
    
    public BooleanProperty cancelled() {
        return cancelled;
    }
}
