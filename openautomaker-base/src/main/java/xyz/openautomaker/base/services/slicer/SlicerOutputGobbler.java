package xyz.openautomaker.base.services.slicer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author ianhudson
 */
class SlicerOutputGobbler extends Thread {

	private final InputStream is;
	private final String type;

	private static final Logger LOGGER = LogManager.getLogger();

	private final ProgressReceiver progressReceiver;
	private final SlicerType slicerType;

	SlicerOutputGobbler(ProgressReceiver progressReceiver, InputStream is, String type,
			SlicerType slicerType) {
		this.progressReceiver = progressReceiver;
		this.is = is;
		this.type = type;
		this.slicerType = slicerType;
		this.setName("SlicerOutputGobbler");
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			int printCounter = 0;
			while ((line = br.readLine()) != null) {
				printCounter++;

				if (line.startsWith("Progress")) {
					String[] lineParts = line.split(":");
					if (lineParts.length == 4) {
						String task = lineParts[1];
						int progressInt = 0;

						float workDone = Float.valueOf(lineParts[2]);
						float totalWork = slicerType != SlicerType.Cura ? parseTotalWork(lineParts[3]) : Float.valueOf(lineParts[3]);

						if (workDone == 1f || workDone == totalWork || printCounter >= 40) {
							printCounter = 0;
							LOGGER.debug(">" + line);
						}

						if (task.equalsIgnoreCase("inset")) {
							progressInt = (int) ((workDone / totalWork) * 25);
						}
						else if (task.contains("skin")) {
							progressInt = (int) ((workDone / totalWork) * 25) + 25;
						}
						else if (task.equalsIgnoreCase("export")) {
							progressInt = (int) ((workDone / totalWork) * 49) + 50;
						}
						else if (task.equalsIgnoreCase("process")) {
							progressInt = (int) ((workDone / totalWork) * 1) + 99;
						}
						setLoadProgress(task, progressInt);
					}
				}
			}
		}
		catch (IOException ioe) {
			LOGGER.error(ioe.getMessage());
		}
	}

	private void setLoadProgress(final String loadMessage, final int percentProgress) {
		if (progressReceiver != null) {
			progressReceiver.progressUpdateFromSlicer(loadMessage, percentProgress);
		}
	}

	private float parseTotalWork(String totalWork) {
		String[] lineParts = totalWork.split(" ");
		return Float.valueOf(lineParts[0]);
	}

	private float parsePercentagePart(String percentagePart) {
		String[] lineParts = percentagePart.split(" ");
		String value = lineParts[1].replace("%", "");
		return Float.valueOf(value);
	}
}
