
package xyz.openautomaker.base.postprocessor;

/**
 *
 * @author Ian
 */
class EventIndex
{
    private int index = 0;
    private EventType eventType;

    public EventIndex(EventType eventType, int index)
    {
        this.eventType = eventType;
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public EventType getEventType()
    {
        return eventType;
    }

    public void setEventType(EventType eventType)
    {
        this.eventType = eventType;
    }
}
