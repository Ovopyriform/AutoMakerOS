
package org.openautomaker.base.utils.tasks;

/**
 *
 * @author tony
 * @param <T>
 */
public interface TaskResponder<T>
{
    public void taskEnded(TaskResponse<T> taskResponse);
}
