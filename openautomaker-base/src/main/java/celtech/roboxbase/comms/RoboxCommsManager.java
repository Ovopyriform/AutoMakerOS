package celtech.roboxbase.comms;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.BaseLookup;
import org.openautomaker.base.camera.CameraInfo;
import org.openautomaker.base.printerControl.model.HardwarePrinter;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.printerControl.model.PrinterConnection;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.MachineType;
import org.openautomaker.environment.OpenAutomakerEnv;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterEnabledPreference;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterHeadPreference;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterTypePreference;

import celtech.roboxbase.comms.remote.RoboxRemoteCommandInterface;
import celtech.roboxbase.comms.rx.StatusResponse;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
//TODO: revisit how this manages printers.  Seems like encapsulation is out of whack
public class RoboxCommsManager extends Thread implements PrinterStatusConsumer {

	private static final Logger LOGGER = LogManager.getLogger();

	// Used to manage the virtual printer rather than relying on UI listeners.
	private VirtualPrinterEnabledPreference fVirtualPrinterEnabledPreference;
	private VirtualPrinterHeadPreference fVirtualPrinterHeadPreference;
	private VirtualPrinterTypePreference fVirtualPrinterTypePreference;

	private UUID fVirtualPrinterHandle = null;

	//public static final String CUSTOM_CONNECTION_HANDLE = "OfflinePrinterConnection";

	public static final String MOUNTED_MEDIA_FILE_PATH = "/media";

	public static final int MAX_ACTIVE_PRINTERS = 9;

	private static RoboxCommsManager instance = null;

	private boolean keepRunning = true;

	private final String printerToSearchFor = "Robox";
	private final String roboxVendorID = "16D0";
	private final String roboxProductID = "081B";

	private final ObservableMap<DetectedDevice, Printer> activePrinters = FXCollections.observableHashMap();
	private final ObservableList<CameraInfo> activeCameras = FXCollections.observableArrayList();
	private boolean suppressPrinterIDChecks = false;
	private int sleepBetweenStatusChecksMS = 1000;

	private final SerialDeviceDetector usbSerialDeviceDetector;
	private final RemotePrinterDetector remotePrinterDetector;

	// perhaps we want to move this (and asociated code) into CameraCommsManager
	// This also means we need to move the CameraCommsManager out of root
	// and into RoboxBase...
	private final RemoteCameraDetector remoteCameraDetector;

	private boolean doNotCheckForPresenceOfHead = false;
	private BooleanProperty detectLoadedFilamentOverride = new SimpleBooleanProperty(true);
	private BooleanProperty searchForRemoteCamerasProperty = new SimpleBooleanProperty(false);

	// Set to true when an attempt is made to connect another printer when the maximum number
	// of printers have already been connected. It will be set to false again when a printer is
	// disconnected. This means listeners will be notifed when the first attempt to connect
	// too any printers is made, but will not be notified again until the number of connected
	// printers decreases.
	private BooleanProperty tooManyRoboxAttachedProperty = new SimpleBooleanProperty(true);

	private RoboxCommsManager(Path pathToBinaries,
			boolean suppressPrinterIDChecks,
			boolean doNotCheckForPresenceOfHead,
			BooleanProperty detectLoadedFilamentProperty,
			BooleanProperty searchForRemoteCamerasProperty) {

		configureVirtualPrinterPreferenceListeners();

		this.suppressPrinterIDChecks = suppressPrinterIDChecks;
		this.doNotCheckForPresenceOfHead = doNotCheckForPresenceOfHead;
		this.detectLoadedFilamentOverride = detectLoadedFilamentProperty;
		this.searchForRemoteCamerasProperty = searchForRemoteCamerasProperty;

		this.setDaemon(true);
		this.setName("Robox Comms Manager");
		this.setPriority(6);

		usbSerialDeviceDetector = new SerialDeviceDetector(pathToBinaries, roboxVendorID, roboxProductID, printerToSearchFor);
		remotePrinterDetector = new RemotePrinterDetector();

		remoteCameraDetector = new RemoteCameraDetector();

		tooManyRoboxAttachedProperty.set(false);
	}

	/**
	 * Configures the preferences and listeners for the virtual printer
	 */
	private void configureVirtualPrinterPreferenceListeners() {
		// Configure the virtual printer type from the preference
		fVirtualPrinterTypePreference = new VirtualPrinterTypePreference();
		fVirtualPrinterTypePreference.addChangeListener(new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				// Refresh the preference
				fVirtualPrinterTypePreference = new VirtualPrinterTypePreference();
				refreshVirtualPrinter();
			}
		});

		// Configure the virtual printer head from the preference
		fVirtualPrinterHeadPreference = new VirtualPrinterHeadPreference();
		fVirtualPrinterHeadPreference.addChangeListener(new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				fVirtualPrinterHeadPreference = new VirtualPrinterHeadPreference();
				refreshVirtualPrinter();
			}

		});

		// Configure the virtual printer from the preferences
		fVirtualPrinterEnabledPreference = new VirtualPrinterEnabledPreference();
		if (fVirtualPrinterEnabledPreference.get())
			addVirtualPrinter(true);

		fVirtualPrinterEnabledPreference.addChangeListener(new PreferenceChangeListener() {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				fVirtualPrinterEnabledPreference = new VirtualPrinterEnabledPreference();
				if (fVirtualPrinterEnabledPreference.get()) {
					addVirtualPrinter(true);
					return;
				}

				removeAllDummyPrinters();
			}
		});
	}

	/**
	 * Refresh the virtual printer. Used if the printer type or head changes.
	 */
	private void refreshVirtualPrinter() {
		Optional<DetectedDevice> virtualPrinterHandle = getVirtualPrinter();
		if (virtualPrinterHandle.isPresent())
			removeDummyPrinter(virtualPrinterHandle.get());

		addVirtualPrinter(true);
	}

	/**
	 *
	 * @return
	 */
	public static RoboxCommsManager getInstance() {
		return instance;
	}

	/**
	 *
	 * @param pathToBinaries
	 * @return
	 */
	public static RoboxCommsManager getInstance(Path pathToBinaries) {
		if (instance == null) {
			instance = new RoboxCommsManager(pathToBinaries, false, false, new SimpleBooleanProperty(true), new SimpleBooleanProperty(false));
		}

		return instance;
	}

	/**
	 *
	 * @param pathToBinaries
	 * @param doNotCheckForHeadPresence
	 * @param detectLoadedFilament
	 * @param searchForRemoteCameras
	 * @return
	 */
	public static RoboxCommsManager getInstance(Path pathToBinaries,
			boolean doNotCheckForHeadPresence,
			boolean detectLoadedFilament,
			boolean searchForRemoteCameras) {
		if (instance == null) {
			instance = new RoboxCommsManager(pathToBinaries,
					false,
					doNotCheckForHeadPresence,
					new SimpleBooleanProperty(detectLoadedFilament),
					new SimpleBooleanProperty(searchForRemoteCameras));
		}

		return instance;
	}

	/**
	 *
	 * @param pathToBinaries
	 * @param doNotCheckForHeadPresence
	 * @param detectLoadedFilamentProperty
	 * @param searchForRemoteCameras
	 * @return
	 */
	public static RoboxCommsManager getInstance(Path pathToBinaries,
			boolean doNotCheckForHeadPresence,
			BooleanProperty detectLoadedFilamentProperty,
			BooleanProperty searchForRemoteCamerasProperty) {
		if (instance == null) {
			instance = new RoboxCommsManager(pathToBinaries,
					false,
					doNotCheckForHeadPresence,
					detectLoadedFilamentProperty,
					searchForRemoteCamerasProperty);
		}

		return instance;
	}

	// This needs to be synchronized because it is called from both the RoboxCommsManager thread and
	// the main JavaFX thread (when a dummy printer is added). Without the synchronization, the activePrinters
	// list can be updated simultaneously by the two threads, occasionally causing corruption.
	private synchronized void assessCandidatePrinter(DetectedDevice detectedPrinter) {
		if (detectedPrinter != null
				&& !activePrinters.keySet().contains(detectedPrinter)) {
			if (activePrinters.size() >= MAX_ACTIVE_PRINTERS) {
				LOGGER.info("Max number of printers already connected - not connecteding to new printer on " + detectedPrinter.getConnectionHandle());
				tooManyRoboxAttachedProperty.set(true);
			}
			else {
				// We need to connect!
				LOGGER.info("Adding new printer on " + detectedPrinter.getConnectionHandle());
				Printer newPrinter = makePrinter(detectedPrinter);
				activePrinters.put(detectedPrinter, newPrinter);
				newPrinter.startComms();
			}
		}
	}

	private Printer makePrinter(DetectedDevice detectedPrinter) {
		HardwarePrinter.FilamentLoadedGetter filamentLoadedGetter = (StatusResponse statusResponse, int extruderNumber) -> {
			if (!detectLoadedFilamentOverride.get()) {
				// if this preference has been deselected then always say that the filament
				// has been detected as loaded.
				return true;
			}
			else {
				if (extruderNumber == 1) {
					return statusResponse.isFilament1SwitchStatus();
				}
				else {
					return statusResponse.isFilament2SwitchStatus();
				}
			}
		};
		Printer newPrinter = null;

		switch (detectedPrinter.getConnectionType()) {
			case SERIAL:
				newPrinter = new HardwarePrinter(this, new HardwareCommandInterface(
						this, detectedPrinter, suppressPrinterIDChecks,
						sleepBetweenStatusChecksMS), filamentLoadedGetter,
						doNotCheckForPresenceOfHead);
				newPrinter.setPrinterConnection(PrinterConnection.LOCAL);
				break;
			case ROBOX_REMOTE:
				RoboxRemoteCommandInterface commandInterface = new RoboxRemoteCommandInterface(
						this, (RemoteDetectedPrinter) detectedPrinter, suppressPrinterIDChecks,
						sleepBetweenStatusChecksMS);

				newPrinter = new HardwarePrinter(this, commandInterface, filamentLoadedGetter,
						doNotCheckForPresenceOfHead);
				newPrinter.setPrinterConnection(PrinterConnection.REMOTE);
				break;
			case DUMMY:
				//TODO: If we keep a reference to the dummy printer interface surely we can change things on the fly?
				DummyPrinterCommandInterface dummyCommandInterface = new DummyPrinterCommandInterface(this,
						detectedPrinter,
						suppressPrinterIDChecks,
						sleepBetweenStatusChecksMS,
						new I18N().t("preferences.customPrinter"),
						fVirtualPrinterTypePreference.get().getTypeCode());

				dummyCommandInterface.setupHead(fVirtualPrinterHeadPreference.get());
				newPrinter = new HardwarePrinter(this, dummyCommandInterface, filamentLoadedGetter,
						doNotCheckForPresenceOfHead);
				//                
				//                if(detectedPrinter.getConnectionHandle().equals(CUSTOM_CONNECTION_HANDLE)) 
				//                {
				newPrinter.setPrinterConnection(PrinterConnection.OFFLINE);
				//                } else 
				//                {
				//                    newPrinter.setPrinterConnection(PrinterConnection.DUMMY);
				//                }
				break;
			default:
				LOGGER.error("Don't know how to handle connected printer: " + detectedPrinter);
				break;
		}
		return newPrinter;
	}

	private void removeMissingCameras(List<CameraInfo> remotelyAttachedCameras) {
		List<CameraInfo> missingCameras = new ArrayList<>();

		// Remove cameras that are no longer detected.
		activeCameras.forEach((c) -> {
			if (!remotelyAttachedCameras.contains(c))
				missingCameras.add(c);
		});
		missingCameras.forEach((c) -> {
			BaseLookup.cameraDisconnected(c);
			activeCameras.remove(c);
		});
	}

	private void assessCandidateCamera(CameraInfo candidateCamera) {
		if (!activeCameras.contains(candidateCamera)) {
			activeCameras.add(candidateCamera);
			BaseLookup.cameraConnected(candidateCamera);
		}
	}

	@Override
	public void start() {
		LOGGER.debug("Starting comms manager");
		super.start();
	}

	@Override
	public void run() {
		while (keepRunning) {
			long startOfRunTime = System.currentTimeMillis();

			// Search
			List<DetectedDevice> directlyAttachedDevices = usbSerialDeviceDetector.searchForDevices();
			List<DetectedDevice> remotelyAttachedDevices = remotePrinterDetector.searchForDevices();

			if (searchForRemoteCamerasProperty.get()) {
				// Cache camera info
				List<CameraInfo> remotelyAttachedCameras = remoteCameraDetector.searchForDevices();
				removeMissingCameras(remotelyAttachedCameras);
				remotelyAttachedCameras.forEach(this::assessCandidateCamera);
			}
			else if (!activeCameras.isEmpty()) {
				// searchForRemoteCamerasProperty has been set to false, so clear camera cache.
				activeCameras.forEach((c) -> {
					// The camera detected flag on the server is reset for each camera
					// on the server. Although slightly inefficent, this doesn't matter
					// as all cameras will be removed.
					c.getServer().setCameraDetected(false);
					BaseLookup.cameraDisconnected(c);
				});
				activeCameras.clear();
			}

			// Now new connections
			List<DetectedDevice> printersToConnect = new ArrayList<>();
			directlyAttachedDevices.forEach(newPrinter -> {
				if (!activePrinters.keySet().contains(newPrinter)) {
					printersToConnect.add(newPrinter);
				}
			});
			remotelyAttachedDevices.forEach(newPrinter -> {
				if (!activePrinters.keySet().contains(newPrinter)) {
					printersToConnect.add(newPrinter);
				}
			});

			for (DetectedDevice printerToConnect : printersToConnect) {
				LOGGER.debug("We have found a new printer " + printerToConnect);
				assessCandidatePrinter(printerToConnect);
			}

			if (OpenAutomakerEnv.get().getMachineType() == MachineType.LINUX) {
				// check if there are any usb drives mounted
				File mediaDir = new File(MOUNTED_MEDIA_FILE_PATH);
				BaseLookup.retainAndAddUSBDirectories(mediaDir.listFiles());
			}

			long endOfRunTime = System.currentTimeMillis();
			long runTime = endOfRunTime - startOfRunTime;
			long sleepTime = 500 - runTime;

			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				}
				catch (InterruptedException ex) {
					LOGGER.info("Comms manager was interrupted during sleep");
				}
			}
		}
	}

	/**
	 *
	 */
	public void shutdown() {
		keepRunning = false;

		List<Printer> printersToShutdown = new ArrayList<>();
		BaseLookup.getConnectedPrinters().forEach(printer -> {
			printersToShutdown.add(printer);
		});

		for (Printer printer : printersToShutdown) {
			LOGGER.debug("Shutdown printer " + printer);
			try {
				printer.getCommandInterface().shutdown();
				printer.shutdown();
			}
			catch (Exception ex) {
				LOGGER.error("Error shutting down printer");
			}
		}
	}

	/**
	 *
	 * @param detectedPrinter
	 */
	@Override
	public void printerConnected(DetectedDevice detectedPrinter) {
		Printer printerHusk = activePrinters.get(detectedPrinter);
		printerHusk.connectionEstablished();

		BaseLookup.printerConnected(printerHusk);
	}

	/**
	 *
	 */
	// This needs to be synchronized because it is called from both the RoboxCommsManager thread and
	// the main JavaFX thread (when a dummy printer is removed). Without the synchronization, the activePrinters
	// list can be updated simultaneously by the two threads, occasionally causing corruption.
	private synchronized void disconnectedSync(DetectedDevice printerHandle) {
		final Printer printerToRemove = activePrinters.get(printerHandle);
		if (printerToRemove != null) {
			printerToRemove.stopComms();
			printerToRemove.shutdown();
		}

		BaseLookup.printerDisconnected(printerToRemove);

		if (activePrinters.containsKey(printerHandle)) {
			if (activePrinters.get(printerHandle) != null
					&& activePrinters.get(printerHandle).getPrinterIdentity() != null
					&& activePrinters.get(printerHandle).getPrinterIdentity().printerFriendlyNameProperty().get() != null
					&& !activePrinters.get(printerHandle).getPrinterIdentity().printerFriendlyNameProperty().get().equals("")) {
				LOGGER.info("Disconnected from " + activePrinters.get(printerHandle).getPrinterIdentity().printerFriendlyNameProperty().get());
			}
			else {
				LOGGER.info("Disconnected");
			}
			activePrinters.remove(printerHandle);
			tooManyRoboxAttachedProperty.set(false);
		}
	}

	/**
	 *
	 */
	@Override
	public void disconnected(DetectedDevice printerHandle) {
		disconnectedSync(printerHandle);
	}

	public void addVirtualPrinter(boolean isCustomPrinter) {
		//Generate a UUID for the printer handle
		fVirtualPrinterHandle = UUID.randomUUID();
		DetectedDevice virtualPrinter = new DetectedDevice(DeviceDetector.DeviceConnectionType.DUMMY, fVirtualPrinterHandle.toString());
		assessCandidatePrinter(virtualPrinter);

		//        dummyPrinterCounter++;
		//        String actualPrinterPort = isCustomPrinter ? CUSTOM_CONNECTION_HANDLE : dummyPrinterPort + " " + dummyPrinterCounter;
		//        dummyPrinterName = isCustomPrinter ? OpenAutomakerEnv.getI18N().t("preferences.customPrinter") : "DP " + dummyPrinterCounter;
		//        DetectedDevice printerHandle = new DetectedDevice(DeviceDetector.DeviceConnectionType.DUMMY,
		//                actualPrinterPort);
		//        assessCandidatePrinter(printerHandle);
	}

	public void removeDummyPrinter(DetectedDevice printerHandle) {
		disconnectedSync(printerHandle);
	}

	public void removeAllDummyPrinters() {
		getDummyPrinterHandles().forEach(this::removeDummyPrinter);
	}

	private Optional<DetectedDevice> getVirtualPrinter() {
		return getDummyPrinterHandles().stream()
				.filter(printerHandle -> printerHandle.getConnectionHandle().equals(fVirtualPrinterHandle.toString()))
				.findFirst();
	}

	public List<Printer> getDummyPrinters() {
		return activePrinters.entrySet()
				.stream()
				.filter(p -> p.getKey().getConnectionType() == DeviceDetector.DeviceConnectionType.DUMMY)
				.map(e -> e.getValue())
				.collect(Collectors.toList());
	}

	private List<DetectedDevice> getDummyPrinterHandles() {
		return activePrinters.keySet()
				.stream()
				.filter(printerHandle -> printerHandle.getConnectionType() == DeviceDetector.DeviceConnectionType.DUMMY)
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @param milliseconds
	 */
	public void setSleepBetweenStatusChecks(int milliseconds) {
		sleepBetweenStatusChecksMS = milliseconds;
	}

	private void deviceNoLongerPresent(DetectedDevice detectedDevice) {
		Printer printerToDisconnect = activePrinters.get(detectedDevice);
		if (printerToDisconnect != null) {
			CommandInterface commandInterface = printerToDisconnect.getCommandInterface();
			if (commandInterface != null) {
				commandInterface.shutdown();
			}
			else {
				LOGGER.info("CI was null");
			}
		}
		else {
			LOGGER.info("not in active list");
		}
	}

	public BooleanProperty tooManyRoboxAttachedProperty() {
		return tooManyRoboxAttachedProperty;
	}

	//    public void setDummyPrinterHeadType(String dummyPrinterHeadType) {
	//        this.dummyPrinterHeadType = dummyPrinterHeadType;
	//    }
	//    
	//    public void setDummyPrinterType(PrinterType dummyPrinterType) {
	//        this.dummyPrinterType = dummyPrinterType;
	//    }
}
