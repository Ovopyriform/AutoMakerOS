
package celtech.roboxbase.comms.rx;

import java.util.ArrayList;

/**
 *
 * @author tony
 */
public abstract class ListFilesResponse extends RoboxRxPacket
{

    /**
     *
     */
    public ListFilesResponse()
    {
        super(RxPacketTypeEnum.LIST_FILES_RESPONSE, false, false);
    }

    /**
     *
     * @param byteData
     * @return
     */
    @Override
    public abstract boolean populatePacket(byte[] byteData, float requiredFirmwareVersion);

    /**
     *
     * @return
     */
    public abstract ArrayList<String> getPrintJobIDs();

}
