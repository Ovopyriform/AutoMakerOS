package celtech.services.gcodepreview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.IntegerProperty;

/**
 *
 * @author Tony Aldhous
 */
class GCodePreviewConsumer extends Thread {
	private static final int MAX_LAYER_COUNT = 10000;
	private final InputStream is;
	private static final Logger LOGGER = LogManager.getLogger();
	private IntegerProperty layerCountProperty = null;
	private int layerCount = 0;

	GCodePreviewConsumer(InputStream is) {
		this.is = is;
	}

	void setLayerCountProperty(IntegerProperty layerCountProperty) {
		this.layerCountProperty = layerCountProperty;
		this.layerCountProperty.set(layerCount);
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			boolean receivedGoodbye = false;
			while (!receivedGoodbye && (line = br.readLine()) != null) {
				LOGGER.debug("> " + line);
				Scanner lineScanner = new Scanner(line);
				if (lineScanner.hasNext()) {
					String commandWord = lineScanner.next().toLowerCase();
					switch (commandWord) {
						case "layercount":
							if (lineScanner.hasNextInt()) {
								int value = lineScanner.nextInt();
								if (value > 0 && value < MAX_LAYER_COUNT) {
									layerCount = value;
									if (layerCountProperty != null)
										layerCountProperty.set(layerCount);
								}
							}
							break;
						case "goodbye!":
							receivedGoodbye = true;
							break;

						default:
							break;
					}
				}
			}
		}
		catch (IOException ioe) {
			LOGGER.error(ioe.getMessage());
		}
	}
}
