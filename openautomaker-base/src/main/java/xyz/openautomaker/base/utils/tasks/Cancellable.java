
package xyz.openautomaker.base.utils.tasks;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author tony
 */
public interface Cancellable
{
    public BooleanProperty cancelled();
}
