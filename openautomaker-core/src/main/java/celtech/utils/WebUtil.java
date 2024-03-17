package celtech.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.environment.MachineType;
import org.openautomaker.environment.OpenAutomakerEnv;

/**
 *
 * @author George Salter
 */
public class WebUtil {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void launchURL(String url) {
		if (!Desktop.isDesktopSupported())
			return;

		MachineType machineType = OpenAutomakerEnv.get().getMachineType();

		if (machineType == MachineType.LINUX) {
			try {
				if (Runtime.getRuntime().exec(new String[] {
						"which", "xdg-open"
				}).getInputStream().read() != -1) {
					Runtime.getRuntime().exec(new String[] {
							"xdg-open", url
					});
				}
			}
			catch (IOException ex) {
				LOGGER.error("Failed to run linux-specific browser command");
			}
			return;
		}

		try {
			URI linkToVisit = new URI(url);
			Desktop.getDesktop().browse(linkToVisit);
		}
		catch (IOException | URISyntaxException ex) {
			LOGGER.error("Error when attempting to browse to " + url);
		}

	}
}
