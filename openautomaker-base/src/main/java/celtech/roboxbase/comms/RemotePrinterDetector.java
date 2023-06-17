package celtech.roboxbase.comms;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.configuration.CoreMemory;

/**
 *
 * @author Ian
 */
public class RemotePrinterDetector extends DeviceDetector
{

	private static final Logger LOGGER = LogManager.getLogger();

    public RemotePrinterDetector()
    {
        super();
    }

    @Override
    public List<DetectedDevice> searchForDevices()
    {
        List<DetectedDevice> newlyDetectedPrinters = new ArrayList();

        //Take a copy of the list in case it gets changed under our feet
        List<DetectedServer> activeRoboxRoots = new ArrayList<>(CoreMemory.getInstance().getActiveRoboxRoots());

        // Search the roots that have been registered in core memory
        for (DetectedServer server : activeRoboxRoots)
        {
            if (server.getServerStatus() == DetectedServer.ServerStatus.CONNECTED)
            {
                List<DetectedDevice> attachedPrinters = server.listAttachedPrinters();
                newlyDetectedPrinters.addAll(attachedPrinters);
            }
        }
        
        return newlyDetectedPrinters;
    }
}
