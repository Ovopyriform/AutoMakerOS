/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.tx;

/**
 *
 * @author ianhudson
 */
public class SendResetErrors extends RoboxTxPacket
{

    /**
     *
     */
    public SendResetErrors()
    {
        super(TxPacketTypeEnum.RESET_ERRORS, false, false);
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
