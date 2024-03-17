package org.openautomaker.base.appManager;

import java.util.Optional;
import java.util.Set;

import org.openautomaker.base.appManager.NotificationType;
import org.openautomaker.base.appManager.PurgeResponse;
import org.openautomaker.base.appManager.SystemNotificationManager;
import org.openautomaker.base.configuration.fileRepresentation.HeadFile;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.services.firmware.FirmwareLoadResult;
import org.openautomaker.base.services.firmware.FirmwareLoadService;
import org.openautomaker.base.utils.tasks.TaskResponder;

import celtech.roboxbase.comms.RoboxResetIDResult;
import celtech.roboxbase.comms.rx.FirmwareError;
import celtech.roboxbase.comms.rx.PrinterIDResponse;

/**
 *
 * @author Ian
 */
public class TestSystemNotificationManager implements SystemNotificationManager
{

    @Override
    public boolean askUserToUpdateFirmware(Printer printerToUpdate)
    {
        return false;
    }
    
    @Override
    public boolean showDowngradeFirmwareDialog(Printer printerToUpdate)
    {
        return false;
    }
    
    @Override
    public RoboxResetIDResult askUserToResetPrinterID(Printer printerToUse, PrinterIDResponse printerID)
    {
        return RoboxResetIDResult.RESET_NOT_DONE;
    }
    
    @Override
    public void processErrorPacketFromPrinter(FirmwareError response, Printer printer)
    {
    }

    @Override
    public void showCalibrationDialogue()
    {
    }

    @Override
    public void showFirmwareUpgradeStatusNotification(FirmwareLoadResult result)
    {
    }

    @Override
    public void showGCodePostProcessSuccessfulNotification()
    {
    }

    @Override
    public void showHeadUpdatedNotification()
    {
    }

    @Override
    public void showPrintJobCancelledNotification()
    {
    }

    @Override
    public void showPrintJobFailedNotification()
    {
    }

    @Override
    public void showPrintTransferInitiatedNotification()
    {
    }

    @Override
    public void showPrintTransferSuccessfulNotification(String printerName)
    {
    }

    @Override
    public void showReprintStartedNotification()
    {
    }

    @Override
    public void showSDCardNotification()
    {
    }

    @Override
    public void showSliceSuccessfulNotification()
    {
    }

    @Override
    public void configureFirmwareProgressDialog(FirmwareLoadService firmwareLoadService)
    {
    }

    @Override
    public void showNoSDCardDialog()
    {
    }

    @Override
    public void showNoPrinterIDDialog(Printer printer)
    {
    }

    @Override
    public void showInformationNotification(String title, String message)
    {
    }

    @Override
    public void showWarningNotification(String title, String message)
    {
    }

    @Override
    public void showErrorNotification(String title, String message)
    {
    }

    @Override
    public boolean showOpenDoorDialog()
    {
        return false;
    }

    @Override
    public boolean showModelTooBigDialog(String modelFilename)
    {
        return false;
    }

    @Override
    public boolean showApplicationUpgradeDialog(String applicationName)
    {
        return false;
    }
    
    @Override
    public boolean showAreYouSureYouWantToDowngradeDialog()
    {
        return false;
    }

    @Override
    public boolean showJobsTransferringShutdownDialog()
    {
        return false;
    }

    @Override
    public void showProgramInvalidHeadDialog(TaskResponder<HeadFile> taskResponse)
    {
    }

    @Override
    public void showHeadNotRecognisedDialog(String printerName)
    {
    }

    @Override
    public PurgeResponse showPurgeDialog()
    {
        return PurgeResponse.PRINT_WITH_PURGE;
    }

    @Override
    public void showReelNotRecognisedDialog(String printerName)
    {
    }

    @Override
    public void showReelUpdatedNotification()
    {
    }

    @Override
    public Optional<PrinterErrorChoice> showPrinterErrorDialog(String title, String message,
            boolean showContinueOption, boolean showAbortOption, boolean showRetryOption, boolean showOKOption)
    {
        return Optional.empty();
    }

    @Override
    public void askUserToClearBed()
    {
    }

    @Override
    public boolean confirmAdvancedMode()
    {
        return true;
    }

    @Override
    public void showPrintTransferFailedNotification(String printerName)
    {
    }

    @Override
    public void removePrintTransferFailedNotification()
    {
    }

    @Override
    public void showEjectFailedDialog(Printer printer, int nozzleNumber, FirmwareError error)
    {
    }

    @Override
    public void showKeepPushingFilamentNotification()
    {
    }

    @Override
    public void hideKeepPushingFilamentNotification()
    {
    }

    @Override
    public void showFilamentStuckMessage()
    {
    }

    @Override
    public void showLoadFilamentNowMessage()
    {
    }

    @Override
    public void showFilamentMotionCheckBanner()
    {
    }

    @Override
    public void hideFilamentMotionCheckBanner()
    {
    }

    @Override
    public boolean showModelIsInvalidDialog(Set<String> modelNames)
    {
        return false;
    }

    @Override
    public void clearAllDialogsOnDisconnect()
    {
    }

    @Override
    public PurgeResponse showPurgeDialog(boolean allowAutoPrint)
    {
        return PurgeResponse.PRINT_WITH_PURGE;
    }
    
    @Override
    public void hideProgramInvalidHeadDialog()
    {
    }

    @Override
    public void showDismissableNotification(String message, String buttonText, NotificationType notificationType)
    {
    }
}
