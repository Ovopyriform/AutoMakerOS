
package org.openautomaker.base.printerControl.model.statetransitions.calibration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.BaseLookup;
import org.openautomaker.base.appManager.SystemNotificationManager.PrinterErrorChoice;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.utils.tasks.Cancellable;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.roboxbase.comms.events.ErrorConsumer;
import celtech.roboxbase.comms.rx.FirmwareError;

/**
 * The PurgePrinterErrorHandlerOrig listens for printer errors and if they occur
 * then cause the user to get a Continue/Abort dialog.
 *
 * @author tony
 */
public class CalibrationPrinterErrorHandler
{

	private static final Logger LOGGER = LogManager.getLogger();

    private final Printer printer;
    private final Cancellable errorCancellable;

    public CalibrationPrinterErrorHandler(Printer printer, Cancellable errorCancellable)
    {
        this.printer = printer;
        this.errorCancellable = errorCancellable;
    }

    ErrorConsumer errorConsumer = (FirmwareError error) ->
    {
		LOGGER.debug("ERROR consumed in purge");
        notifyUserErrorHasOccurredAndAbortIfNotSlip(error);
    };

    public void registerForPrinterErrors()
    {
        List<FirmwareError> errors = new ArrayList<>();
        errors.add(FirmwareError.ALL_ERRORS);
        printer.registerErrorConsumer(errorConsumer, errors);
    }

    /**
     * Check if a printer error has occurred and if so notify the user via a
     * dialog box (only giving the Abort option). Return a boolean indicating if
     * the process should abort.
     */
    private boolean dialogOnDisplay = false;

    private void notifyUserErrorHasOccurredAndAbortIfNotSlip(FirmwareError error)
    {
        if (!errorCancellable.cancelled().get())
        {
            if (error == FirmwareError.B_POSITION_LOST
                    || error == FirmwareError.B_POSITION_WARNING
                    || error == FirmwareError.ERROR_BED_TEMPERATURE_DROOP)
            {
                // Do nothing for the moment...
                printer.clearError(error);
            } else if ((error == FirmwareError.D_FILAMENT_SLIP
                    || error == FirmwareError.E_FILAMENT_SLIP)
                    && !dialogOnDisplay)
            {
                dialogOnDisplay = true;
                Optional<PrinterErrorChoice> response = BaseLookup.getSystemNotificationHandler().
                        showPrinterErrorDialog(
                                OpenAutomakerEnv.getI18N().t(error.getErrorTitleKey()),
                                OpenAutomakerEnv.getI18N().t(error.getErrorMessageKey()),
                                true,
                                true,
                                false,
                                false);

                boolean abort = false;

                if (response.isPresent())
                {
                    switch (response.get())
                    {
                        case ABORT:
                            abort = true;
                            break;
                    }
                } else
                {
                    abort = true;
                }

                if (abort)
                {
                    cancelCalibration();
                }
                dialogOnDisplay = false;
            } else if (!dialogOnDisplay)
            {
                dialogOnDisplay = true;
                // Must be something else
                // if not filament slip or B POSITION then cancel / abort printer activity immediately
                cancelCalibration();
                BaseLookup.getSystemNotificationHandler().
                        showPrinterErrorDialog(
                                OpenAutomakerEnv.getI18N().t(error.getErrorTitleKey()),
                                OpenAutomakerEnv.getI18N().t("calibrationPanel.errorInPrinter"),
                                false,
                                false,
                                false,
                                true);
                dialogOnDisplay = false;
            }
        }
    }

    private void cancelCalibration()
    {
        errorCancellable.cancelled().set(true);
    }

    public void deregisterForPrinterErrors()
    {
        printer.deregisterErrorConsumer(errorConsumer);
    }
}
