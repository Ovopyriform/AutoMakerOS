package celtech.roboxbase.comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
public class SerialDeviceDetector extends DeviceDetector
{

	private static final Logger LOGGER = LogManager.getLogger();
    private final String deviceDetectorStringMac;
    private final String deviceDetectorStringWindows;
    private final String deviceDetectorStringLinux;
    private final String notConnectedString = "NOT_CONNECTED";
    private List<String> command = new ArrayList<>();

	public SerialDeviceDetector(Path pathToBinaries,
            String vendorID,
            String productID,
            String deviceNameToSearchFor)
    {
        super();

		deviceDetectorStringMac = pathToBinaries.resolve("RoboxDetector.mac.sh").toString();
		deviceDetectorStringLinux = pathToBinaries.resolve("RoboxDetector.linux.sh").toString();
		deviceDetectorStringWindows = pathToBinaries.resolve("RoboxDetector.exe").toString();

		switch (OpenAutoMakerEnv.get().getMachineType())
        {
            case WINDOWS:
                command.add(deviceDetectorStringWindows);
                command.add(vendorID);
                command.add(productID);
                break;
            case MAC:
                command.add(deviceDetectorStringMac);
                command.add(deviceNameToSearchFor);
                break;
			case LINUX:
                command.add(deviceDetectorStringLinux);
                command.add(deviceNameToSearchFor);
                command.add(vendorID);
                break;
            default:
				LOGGER.error("Unsupported OS - cannot establish comms.");
                break;
        }

        StringBuilder completeCommand = new StringBuilder();
        command.forEach((subcommand) ->
        {
            completeCommand.append(subcommand);
            completeCommand.append(" ");
        });

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Device detector command: " + completeCommand.toString());
    }

    @Override
    public List<DetectedDevice> searchForDevices()
    {
        StringBuilder outputBuffer = new StringBuilder();

        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;

        try
        {
            process = builder.start();
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.equalsIgnoreCase(notConnectedString) == false)
                {
                    outputBuffer.append(line);
                }
            }
        } catch (IOException ex)
        {
			LOGGER.error("Error " + ex);
        }

        List<DetectedDevice> detectedPrinters = new ArrayList<>();

        if (outputBuffer.length() > 0)
        {
            for (String handle : outputBuffer.toString().split(" "))
            {
                detectedPrinters.add(new DetectedDevice(DeviceConnectionType.SERIAL, handle));
            }
        }

        return detectedPrinters;
    }
}
