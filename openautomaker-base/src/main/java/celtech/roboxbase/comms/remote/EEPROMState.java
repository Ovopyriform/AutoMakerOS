

package celtech.roboxbase.comms.remote;

/**
 *
 * @author Ian
 */
public enum EEPROMState
{

    /**
     *
     */
    NOT_PRESENT(0),

    /**
     *
     */
    NOT_PROGRAMMED(1),

    /**
     *
     */
    PROGRAMMED(2);

    /**
     *
     * @param valueOf
     * @return
     */
    public static EEPROMState modeFromValue(Integer valueOf)
    {
        EEPROMState returnedMode = null;
        
        for (EEPROMState mode : EEPROMState.values())
        {
            if (mode.getValue() == valueOf)
            {
                returnedMode = mode;
                break;
            }
        }
        
        return returnedMode;
    }
    
    private int value;

    private EEPROMState(int value)
    {
        this.value = value;
    }
    
    /**
     *
     * @return
     */
    public int getValue()
    {
        return value;
    }
}
