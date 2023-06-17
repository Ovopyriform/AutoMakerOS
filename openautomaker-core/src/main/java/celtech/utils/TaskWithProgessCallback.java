package celtech.utils;

import javafx.concurrent.Task;
import xyz.openautomaker.base.utils.PercentProgressReceiver;

/**
 *
 * @author ianhudson
 */
public abstract class TaskWithProgessCallback<V> extends Task<V> implements PercentProgressReceiver
{
}
