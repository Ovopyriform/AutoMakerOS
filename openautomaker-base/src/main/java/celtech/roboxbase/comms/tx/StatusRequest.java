/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class StatusRequest extends RoboxTxPacket
{

    /**
     *
     */
    public StatusRequest()
    {
        super(TxPacketTypeEnum.STATUS_REQUEST, false, false);
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
