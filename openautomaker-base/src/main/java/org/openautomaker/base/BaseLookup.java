package org.openautomaker.base;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.appManager.ConsoleSystemNotificationManager;
import org.openautomaker.base.appManager.SystemNotificationManager;
import org.openautomaker.base.camera.CameraInfo;
import org.openautomaker.base.configuration.datafileaccessors.SlicerMappingsContainer;
import org.openautomaker.base.configuration.fileRepresentation.SlicerMappings;
import org.openautomaker.base.postprocessor.GCodeOutputWriter;
import org.openautomaker.base.postprocessor.GCodeOutputWriterFactory;
import org.openautomaker.base.postprocessor.LiveGCodeOutputWriter;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.printerControl.model.PrinterListChangesNotifier;
import org.openautomaker.base.utils.tasks.LiveTaskExecutor;
import org.openautomaker.base.utils.tasks.TaskExecutor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author ianhudson
 */
public class BaseLookup
{
	private static final Logger LOGGER = LogManager.getLogger();

    private static TaskExecutor taskExecutor;
    private static SlicerMappings slicerMappings;
    private static GCodeOutputWriterFactory<GCodeOutputWriter> postProcessorGCodeOutputWriterFactory;
    private static SystemNotificationManager systemNotificationHandler;
    private static boolean shuttingDown = false;

    private static PrinterListChangesNotifier printerListChangesNotifier;
    private static final ObservableList<Printer> CONNECTED_PRINTERS = FXCollections.observableArrayList();
    private static final ObservableList<Printer> CONNECTED_PRINTERS_UNMODIFIABLE = FXCollections.unmodifiableObservableList(CONNECTED_PRINTERS);

    private static final ObservableList<CameraInfo> CONNECTED_CAMS = FXCollections.observableArrayList();
    private static final ObservableList<CameraInfo> CONNECTED_CAMS_UNMODIFIABLE = FXCollections.unmodifiableObservableList(CONNECTED_CAMS);
    
    public static final ObservableList<File> MOUNTED_USB_DIRECTORIES = FXCollections.observableArrayList();

    /**
     * The database of known filaments.
     */
	//private static FilamentContainer filamentContainer;

	//	public static ResourceBundle getLanguageBundle() {
	//		return OpenAutomakerEnv.getI18N().getCombinedResourceBundle();
	//	}

    /**
     * Strings containing templates (eg *T14) should be substituted with the
     * correct text.
     *
     * @param langString
     * @return
     */
	//    public static String substituteTemplates(String langString)
	//    {
	//		return OpenAutomakerEnv.getI18N().substituteTemplates(langString);
	//    }

    public static TaskExecutor getTaskExecutor()
    {
        return taskExecutor;
    }

    public static void setTaskExecutor(TaskExecutor taskExecutor)
    {
        BaseLookup.taskExecutor = taskExecutor;
    }

    public static void setSlicerMappings(SlicerMappings slicerMappings)
    {
        BaseLookup.slicerMappings = slicerMappings;
    }

    public static SlicerMappings getSlicerMappings()
    {
        return slicerMappings;
    }

	public static GCodeOutputWriterFactory<GCodeOutputWriter> getPostProcessorOutputWriterFactory()
    {
        return postProcessorGCodeOutputWriterFactory;
    }

    public static void setPostProcessorOutputWriterFactory(
            GCodeOutputWriterFactory<GCodeOutputWriter> factory)
    {
        postProcessorGCodeOutputWriterFactory = factory;
    }

    public static SystemNotificationManager getSystemNotificationHandler()
    {
        return systemNotificationHandler;
    }

    public static void setSystemNotificationHandler(
            SystemNotificationManager systemNotificationHandler)
    {
        BaseLookup.systemNotificationHandler = systemNotificationHandler;
    }

    public static boolean isShuttingDown()
    {
        return shuttingDown;
    }

    public static void setShuttingDown(boolean shuttingDown)
    {
        BaseLookup.shuttingDown = shuttingDown;
    }

    public static PrinterListChangesNotifier getPrinterListChangesNotifier()
    {
        return printerListChangesNotifier;
    }

    public static void printerConnected(Printer printer)
    {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(">>>Printer connection notification - " + printer);

            doPrinterConnect(printer);
        });
    }

    private static synchronized void doPrinterConnect(Printer printer)
    {
        CONNECTED_PRINTERS.add(printer);
    }

    public static void printerDisconnected(Printer printer)
    {
        BaseLookup.getTaskExecutor().runOnGUIThread(() ->
        {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("<<<Printer disconnection notification - " + printer);

            doPrinterDisconnect(printer);
        });
    }

    private static synchronized void doPrinterDisconnect(Printer printer)
    {
        CONNECTED_PRINTERS.remove(printer);
    }

    public static ObservableList<Printer> getConnectedPrinters()
    {
        return CONNECTED_PRINTERS_UNMODIFIABLE;
    }
    
    public static void cameraConnected(CameraInfo camera)
    {
        CONNECTED_CAMS.add(camera);
    }
    
    public static void cameraDisconnected(CameraInfo camera)
    {
        CONNECTED_CAMS.remove(camera);
    }
    
    public static ObservableList<CameraInfo> getConnectedCameras()
    {
        return CONNECTED_CAMS_UNMODIFIABLE;
    }
    
    public static synchronized void retainAndAddUSBDirectories(File[] usbDirs) {
        MOUNTED_USB_DIRECTORIES.retainAll(usbDirs);
        for(File usbDir : usbDirs) {
            if(!MOUNTED_USB_DIRECTORIES.contains(usbDir)) {
                MOUNTED_USB_DIRECTORIES.add(usbDir);
            }
        }
    }

    public static void setupDefaultValues()
    {
		setupDefaultValues(new ConsoleSystemNotificationManager());
    }


	// TODO: Move to AutoMaker environment
	public static void setupDefaultValues(SystemNotificationManager notificationManager) {

		BaseLookup.setTaskExecutor(new LiveTaskExecutor());

        printerListChangesNotifier = new PrinterListChangesNotifier(BaseLookup.getConnectedPrinters());

        setSystemNotificationHandler(notificationManager);

        setSlicerMappings(SlicerMappingsContainer.getSlicerMappings());

        setPostProcessorOutputWriterFactory(LiveGCodeOutputWriter::new);
    }
}
