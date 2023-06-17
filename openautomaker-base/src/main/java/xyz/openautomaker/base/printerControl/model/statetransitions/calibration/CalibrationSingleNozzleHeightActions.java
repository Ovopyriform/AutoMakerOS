/*
 * Copyright 2014 CEL UK
 */
package xyz.openautomaker.base.printerControl.model.statetransitions.calibration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.roboxbase.comms.exceptions.RoboxCommsException;
import celtech.roboxbase.comms.rx.HeadEEPROMDataResponse;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.configuration.datafileaccessors.HeadContainer;
import xyz.openautomaker.base.configuration.fileRepresentation.HeadFile;
import xyz.openautomaker.base.configuration.fileRepresentation.NozzleData;
import xyz.openautomaker.base.printerControl.PrinterStatus;
import xyz.openautomaker.base.printerControl.model.Head;
import xyz.openautomaker.base.printerControl.model.NozzleHeater;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.printerControl.model.PrinterException;
import xyz.openautomaker.base.printerControl.model.statetransitions.StateTransitionActions;
import xyz.openautomaker.base.utils.PrinterUtils;
import xyz.openautomaker.base.utils.tasks.Cancellable;

/**
 *
 * @author tony
 */
public class CalibrationSingleNozzleHeightActions extends StateTransitionActions {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Printer printer;
	private HeadEEPROMDataResponse savedHeadData;
	private final DoubleProperty zco = new SimpleDoubleProperty();
	private final DoubleProperty zcoGUIT = new SimpleDoubleProperty();
	private final CalibrationPrinterErrorHandler printerErrorHandler;

	private boolean failedActionPerformed = false;
	private final boolean safetyFeaturesRequired;

	public CalibrationSingleNozzleHeightActions(Printer printer, Cancellable userCancellable, Cancellable errorCancellable, boolean safetyFeaturesRequired) {
		super(userCancellable, errorCancellable);

		this.safetyFeaturesRequired = safetyFeaturesRequired;

		this.printer = printer;
		zco.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
			BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
				// zcoGUIT mirrors zco but is only changed on the GUI Thread
				LOGGER.debug("set zcoGUIT to " + zco.get());
				zcoGUIT.set(zco.get());
			});
		});
		printerErrorHandler = new CalibrationPrinterErrorHandler(printer, errorCancellable);
		printerErrorHandler.registerForPrinterErrors();
		CalibrationUtils.setCancelledIfPrinterDisconnected(printer, errorCancellable);
	}

	@Override
	public void initialise() {
		savedHeadData = null;
		zco.set(0);
	}

	public void doInitialiseAndHeatNozzleAction() throws InterruptedException, PrinterException, RoboxCommsException, CalibrationException {
		printerErrorHandler.registerForPrinterErrors();

		zco.set(0);

		printer.setPrinterStatus(PrinterStatus.CALIBRATING_NOZZLE_HEIGHT);
		savedHeadData = printer.readHeadEEPROM(true);
		clearZOffsetsOnHead();
		heatNozzle();

	}

    private void clearZOffsetsOnHead() throws RoboxCommsException
    {
        HeadFile headDataFile = HeadContainer.getHeadByID(savedHeadData.getHeadTypeCode());
        NozzleData nozzle1Data = headDataFile.getNozzles().get(0);
        NozzleData nozzle2Data = null;
        if (headDataFile.getNozzles().size() == 1)
            nozzle2Data= nozzle1Data;
        else
            nozzle2Data = headDataFile.getNozzles().get(1);

        printer.transmitWriteHeadEEPROM(savedHeadData.getHeadTypeCode(),
                savedHeadData.getUniqueID(),
                savedHeadData.getMaximumTemperature(),
                savedHeadData.getThermistorBeta(),
                savedHeadData.getThermistorTCal(),
                nozzle1Data.getDefaultXOffset(),
                nozzle1Data.getDefaultYOffset(),
                0,
                nozzle1Data.getMinBOffset(),
                savedHeadData.getFilamentID(0),
                savedHeadData.getFilamentID(1),
                nozzle2Data.getDefaultXOffset(),
                nozzle2Data.getDefaultYOffset(),
                0,
                nozzle2Data.getMaxBOffset(),
                savedHeadData.getLastFilamentTemperature(0),
                savedHeadData.getLastFilamentTemperature(1),
                savedHeadData.getHeadHours());
        printer.readHeadEEPROM(false);
    }

	private void heatNozzle() throws InterruptedException, PrinterException {
		printer.homeAllAxes(true, userOrErrorCancellable);

		printer.goToTargetNozzleHeaterTemperature(0);
		if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD) {
			printer.goToTargetNozzleHeaterTemperature(1);
		}

		waitOnNozzleTemperature(0);
		if (PrinterUtils.waitOnMacroFinished(printer, userOrErrorCancellable)) {
			return;
		}

		if (printer.headProperty().get().headTypeProperty().get() == Head.HeadType.DUAL_MATERIAL_HEAD) {
			waitOnNozzleTemperature(1);
			if (PrinterUtils.waitOnMacroFinished(printer, userOrErrorCancellable)) {
				return;
			}
		}

		printer.goToZPosition(50);
		printer.goToXYPosition(printer.getPrintVolumeCentre().getX(), printer.getPrintVolumeCentre().getY());
		if (PrinterUtils.waitOnBusy(printer, userOrErrorCancellable)) {
			return;
		}
		printer.switchOnHeadLEDs();
	}

	private void waitOnNozzleTemperature(int nozzleNumber) throws InterruptedException {
		NozzleHeater nozzleHeater = printer.headProperty().get().getNozzleHeaters().get(nozzleNumber);
		PrinterUtils.waitUntilTemperatureIsReached(nozzleHeater.nozzleTemperatureProperty(), null, nozzleHeater.nozzleTargetTemperatureProperty().get(), 5, 300, userOrErrorCancellable);
	}

	public void doHomeZAction() throws CalibrationException {
		printer.homeZ();
	}

	public void doLiftHeadAction() throws PrinterException, CalibrationException {
		printer.switchToAbsoluteMoveMode();
		printer.goToZPosition(30);
	}

	public void doIncrementZAction() throws CalibrationException {
		zco.set(zco.get() + 0.05);
		printer.goToZPosition(zco.get());
	}

	public void doDecrementZAction() throws CalibrationException {
		zco.set(zco.get() - 0.05);
		if (zco.get() < 0) {
			zco.set(0);
		}
		printer.goToZPosition(zco.get());
	}

	public void doFinishedAction() {
		try {
			printerErrorHandler.deregisterForPrinterErrors();
			saveSettings();
			switchHeatersAndHeadLightOff();
		} catch (RoboxCommsException | PrinterException ex) {
			LOGGER.error("Error in finished action: " + ex);
		}
		printer.setPrinterStatus(PrinterStatus.IDLE);
	}

	public void doFailedAction() {

		// this can be called twice if an error occurs
		if (failedActionPerformed) {
			return;
		}

		failedActionPerformed = true;

		try {
			restoreHeadData();
			abortAnyOngoingPrint();
			resetPrinter();
		} catch (CalibrationException | PrinterException ex) {
			LOGGER.error("Error in failed action: " + ex);
		}
		printer.setPrinterStatus(PrinterStatus.IDLE);
	}

	public void doBringBedToFrontAndRaiseHead() throws PrinterException, CalibrationException {
		printer.switchToAbsoluteMoveMode();
		printer.goToXYZPosition(105, 150, 25);
		PrinterUtils.waitOnBusy(printer, userOrErrorCancellable);
	}

	private void switchHeatersAndHeadLightOff() throws PrinterException {
		printer.switchAllNozzleHeatersOff();
		printer.switchOffHeadLEDs();
	}

    private void restoreHeadData()
    {
        if (savedHeadData != null)
        {
            try
            {
				LOGGER.debug("Restore head data");
                printer.transmitWriteHeadEEPROM(savedHeadData.getHeadTypeCode(),
                        savedHeadData.getUniqueID(),
                        savedHeadData.getMaximumTemperature(),
                        savedHeadData.getThermistorBeta(),
                        savedHeadData.getThermistorTCal(),
                        savedHeadData.getNozzle1XOffset(),
                        savedHeadData.getNozzle1YOffset(),
                        savedHeadData.getNozzle1ZOffset(),
                        savedHeadData.getNozzle1BOffset(),
                        savedHeadData.getFilamentID(0),
                        savedHeadData.getFilamentID(1),
                        savedHeadData.getNozzle2XOffset(),
                        savedHeadData.getNozzle2YOffset(),
                        savedHeadData.getNozzle2ZOffset(),
                        savedHeadData.getNozzle2BOffset(),
                        savedHeadData.getLastFilamentTemperature(0),
                        savedHeadData.getLastFilamentTemperature(1),
                        savedHeadData.getHeadHours());
            } catch (RoboxCommsException ex)
            {
				LOGGER.error("Unable to restore head! " + ex);
            }
        }
    }

    public void saveSettings() throws RoboxCommsException
    {
		LOGGER.debug("zco is " + zco);
        printer.transmitWriteHeadEEPROM(savedHeadData.getHeadTypeCode(),
                savedHeadData.getUniqueID(),
                savedHeadData.getMaximumTemperature(),
                savedHeadData.getThermistorBeta(),
                savedHeadData.getThermistorTCal(),
                savedHeadData.getNozzle1XOffset(),
                savedHeadData.getNozzle1YOffset(),
                (float) (-zco.get()),
                savedHeadData.getNozzle1BOffset(),
                savedHeadData.getFilamentID(0),
                savedHeadData.getFilamentID(1),
                savedHeadData.getNozzle2XOffset(),
                savedHeadData.getNozzle2YOffset(),
                (float) (-zco.get()),
                savedHeadData.getNozzle2BOffset(),
                savedHeadData.getLastFilamentTemperature(0),
                savedHeadData.getLastFilamentTemperature(1),
                savedHeadData.getHeadHours());
    }

	public ReadOnlyDoubleProperty getZcoGUITProperty() {
		return zcoGUIT;
	}

	@Override
	public void whenUserCancelDetected() {
		restoreHeadData();
		abortAnyOngoingPrint();
	}

	@Override
	public void whenErrorDetected() {
		printerErrorHandler.deregisterForPrinterErrors();
		restoreHeadData();
		abortAnyOngoingPrint();
	}

	@Override
	public void resetAfterCancelOrError() {
		try {
			doFailedAction();
		} catch (Exception ex) {
			ex.printStackTrace();
			LOGGER.error("error resetting printer " + ex);
		}
	}

	private void resetPrinter() throws PrinterException, CalibrationException {
		printerErrorHandler.deregisterForPrinterErrors();
		switchHeatersAndHeadLightOff();
		doBringBedToFrontAndRaiseHead();
		PrinterUtils.waitOnBusy(printer, (Cancellable) null);
	}

	private void abortAnyOngoingPrint() {
		try {
			if (printer.canCancelProperty().get()) {
				printer.cancel(null, safetyFeaturesRequired);
			}
		} catch (PrinterException ex) {
			LOGGER.error("Failed to abort print - " + ex.getMessage());
		}
	}

}
