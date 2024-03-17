package org.openautomaker.base.printerControl.model.statetransitions.calibration;

import org.openautomaker.base.BaseLookup;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.printerControl.model.PrinterListChangesAdapter;
import org.openautomaker.base.utils.tasks.Cancellable;

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
