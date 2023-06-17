/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.utils.tasks;

/**
 *
 * @author tony
 * @param <T>
 */
public interface TaskResponder<T>
{
    public void taskEnded(TaskResponse<T> taskResponse);
}
