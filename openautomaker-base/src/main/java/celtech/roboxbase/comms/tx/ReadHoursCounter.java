package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class ReadHoursCounter extends RoboxTxPacket
{

    /**
     *
     */
    public ReadHoursCounter()
    {
        super(TxPacketTypeEnum.READ_HOURS_COUNTER, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public boolean populatePacket(byte[] byteData)
    {
        return false;
    }
}
