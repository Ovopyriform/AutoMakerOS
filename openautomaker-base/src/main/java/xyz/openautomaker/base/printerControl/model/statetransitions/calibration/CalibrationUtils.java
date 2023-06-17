package xyz.openautomaker.base.printerControl.model.statetransitions.calibration;

import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.printerControl.model.PrinterListChangesAdapter;
import xyz.openautomaker.base.utils.tasks.Cancellable;

/**
 *
 * @author ianhudson
 */
public class CalibrationUtils
{
    public static void setCancelledIfPrinterDisconnected(Printer printerToMonitor,
            Cancellable cancellable)
    {
        BaseLookup.getPrinterListChangesNotifier().addListener(new PrinterListChangesAdapter()
        {
            @Override
            public void whenPrinterRemoved(Printer printer)
            {
                if (printerToMonitor == printer)
                {
                    cancellable.cancelled().set(true);
                }
            }
        });
    }
}
