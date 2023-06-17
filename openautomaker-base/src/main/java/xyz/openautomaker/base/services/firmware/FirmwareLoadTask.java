package xyz.openautomaker.base.services.firmware;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.exceptions.SDCardErrorException;
import javafx.concurrent.Task;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.utils.SystemUtils;
import xyz.openautomaker.environment.OpenAutoMakerEnv;
import xyz.openautomaker.i18n.OpenAutoMakerI18N;

/**
 *
 * @author ianhudson
 */
public class FirmwareLoadTask extends Task<FirmwareLoadResult> {

	private String firmwareFileToLoad = null;

	private static final Logger LOGGER = LogManager.getLogger();

	private Printer printerToUpdate = null;

	/**
	 * Modified so that this does not trigger the actual update - this should now be fired once this task reports success
	 *
	 * @param firmwareFileToLoad
	 * @param printerToUpdate
	 */
	public FirmwareLoadTask(String firmwareFileToLoad, Printer printerToUpdate) {
		this.firmwareFileToLoad = firmwareFileToLoad;
		this.printerToUpdate = printerToUpdate;
	}

	@Override
	protected FirmwareLoadResult call() throws Exception {
		OpenAutoMakerI18N i18n = OpenAutoMakerEnv.getI18N();

		FirmwareLoadResult returnValue = new FirmwareLoadResult();

		try {
			File file = new File(firmwareFileToLoad);
			byte[] fileData = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(fileData);
			dis.close();

			int remainingBytes = fileData.length;
			int bufferPosition = 0;
			String firmwareID = SystemUtils.generate16DigitID();
			boolean sendOK = printerToUpdate.initialiseDataFileSend(firmwareID, false);

			if (sendOK) {
				updateTitle(i18n.t("dialogs.firmwareUpdateProgressTitle"));
				updateMessage(i18n.t("dialogs.firmwareUpdateProgressLoading"));

				while (bufferPosition < fileData.length && !isCancelled()) {
					updateProgress(bufferPosition, fileData.length);
					byte byteToOutput = fileData[bufferPosition];
					String byteAsString = String.format("%02X", byteToOutput);

					printerToUpdate.sendDataFileChunk(byteAsString, remainingBytes == 1, false);

					remainingBytes--;
					bufferPosition++;
				}

				if (!isCancelled()) {
					printerToUpdate.transmitUpdateFirmware(firmwareID);
					returnValue.setStatus(FirmwareLoadResult.SUCCESS);
				}
			}
		} catch (SDCardErrorException ex) {
			LOGGER.error("SD card exception whilst updating firmware");
			returnValue.setStatus(FirmwareLoadResult.SDCARD_ERROR);
		} catch (RoboxCommsException ex) {
			LOGGER.error("Other comms exception whilst updating firmware ", ex);
			returnValue.setStatus(FirmwareLoadResult.OTHER_ERROR);
		} catch (IOException ex) {
			LOGGER.error("Couldn't load firmware file " + ex.toString());
			returnValue.setStatus(FirmwareLoadResult.FILE_ERROR);
		}

		return returnValue;
	}
}
