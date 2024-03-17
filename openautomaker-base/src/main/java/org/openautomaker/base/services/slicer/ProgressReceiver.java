/*
 * Copyright 2015 CEL UK
 */
package org.openautomaker.base.services.slicer;

/**
 *
 * @author tony
 */
public interface ProgressReceiver
{

    void progressUpdateFromSlicer(String message, float workDone);
}
