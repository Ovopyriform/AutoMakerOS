package xyz.openautomaker.base.configuration;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.OPENAUTOMAKER_LAST_PRINTER_FIRMWARE;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.OPENAUTOMAKER_LAST_PRINTER_SERIAL;
import static xyz.openautomaker.environment.OpenAutoMakerEnv.OPENAUTOMAKER_ROOT_CONNECTED;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import celtech.roboxbase.comms.DetectedServer;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
public class CoreMemory {
	private static final Logger LOGGER = LogManager.getLogger();

	private List<DetectedServer> cachedActiveRoboxRoots = null;

	private static CoreMemory instance = null;

	private final ObjectMapper mapper = new ObjectMapper();

	private CoreMemory() {
		SimpleModule module = new SimpleModule("DetectedServerDeserializer", new Version(1, 0, 0, null, null, null));
		module.addDeserializer(DetectedServer.class, new DetectedServer.DetectedServerDeserializer());
		module.addSerializer(DetectedServer.class, new DetectedServer.DetectedServerSerializer());
		mapper.registerModule(module);
	}

	public static CoreMemory getInstance() {
		if (instance == null) {
			instance = new CoreMemory();
		}
		return instance;
	}

	public List<DetectedServer> getActiveRoboxRoots() {
		if (cachedActiveRoboxRoots == null) {
			String activeRootsJSON = OpenAutoMakerEnv.get().getProperty(OPENAUTOMAKER_ROOT_CONNECTED);
			if (activeRootsJSON != null) {
				try {
					cachedActiveRoboxRoots = mapper.readValue(activeRootsJSON,
							new TypeReference<List<DetectedServer>>() {
							});
				}
				catch (IOException ex) {
					LOGGER.warn("Unable to map data for active robox roots");
				}
			}

			if (cachedActiveRoboxRoots == null) {
				cachedActiveRoboxRoots = new ArrayList<>();
			}
		}
		return cachedActiveRoboxRoots;
	}

	public void clearActiveRoboxRoots() {
		cachedActiveRoboxRoots.clear();
		OpenAutoMakerEnv.get().setProperty(OPENAUTOMAKER_ROOT_CONNECTED, "");

		// BaseConfiguration.setApplicationMemory(ACTIVE_ROBOX_ROOT_KEY, "");
	}

	private void writeRoboxRootData() {
		try {
			OpenAutoMakerEnv.get().setProperty(OPENAUTOMAKER_ROOT_CONNECTED, mapper.writeValueAsString(cachedActiveRoboxRoots));
		}
		catch (JsonProcessingException ex) {
			LOGGER.warn("Unable to write connected root data:", ex);
		}
	}

	public void activateRoboxRoot(DetectedServer server) {
		if (!cachedActiveRoboxRoots.contains(server)) {
			cachedActiveRoboxRoots.add(server);
			writeRoboxRootData();
		}
		else {
			// LOGGER.warning("Root " + server.getName() + " is already active");
		}
	}

	public void deactivateRoboxRoot(DetectedServer server) {
		if (cachedActiveRoboxRoots.contains(server)) {
			cachedActiveRoboxRoots.remove(server);
			writeRoboxRootData();
		}
	}

	public void updateRoboxRoot(DetectedServer server) {
		if (cachedActiveRoboxRoots.contains(server))
			writeRoboxRootData();
	}

	public float getLastPrinterFirmwareVersion() {
		String lastFirmwareVersion = OpenAutoMakerEnv.get().getProperty(OPENAUTOMAKER_LAST_PRINTER_FIRMWARE);

		if (lastFirmwareVersion == null || lastFirmwareVersion.isBlank())
			return 0;

		float lastFirmwareVersionNum = 0;
		try {
			lastFirmwareVersionNum = Float.valueOf(lastFirmwareVersion);
		}
		catch (NumberFormatException ex) {
			LOGGER.warn("Unable to read firmware version from application memory");
		}
		return lastFirmwareVersionNum;
	}

	public void setLastPrinterFirmwareVersion(float firmwareVersionInUse) {
		OpenAutoMakerEnv.get().setProperty(OPENAUTOMAKER_LAST_PRINTER_FIRMWARE, String.format(Locale.UK, "%f", firmwareVersionInUse));
	}

	public String getLastPrinterSerial() {
		return OpenAutoMakerEnv.get().getProperty(OPENAUTOMAKER_LAST_PRINTER_SERIAL);
	}

	public void setLastPrinterSerial(String printerIDToUse) {
		OpenAutoMakerEnv.get().setProperty(OPENAUTOMAKER_LAST_PRINTER_SERIAL, printerIDToUse);
	}
}
