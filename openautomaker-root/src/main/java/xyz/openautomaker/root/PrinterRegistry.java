package xyz.openautomaker.root;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.ROOT_SERVER_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.printerControl.model.Head;
import xyz.openautomaker.base.printerControl.model.Printer;
import xyz.openautomaker.base.printerControl.model.PrinterListChangesListener;
import xyz.openautomaker.base.printerControl.model.PrinterListChangesNotifier;
import xyz.openautomaker.base.printerControl.model.Reel;
import xyz.openautomaker.base.utils.SystemUtils;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author ianhudson
 */
public class PrinterRegistry implements PrinterListChangesListener {

	private static final Logger LOGGER = LogManager.getLogger();

	private static PrinterRegistry instance = null;
	private final Map<String, Printer> remotePrinters = new ConcurrentHashMap<>();
	private final List<String> remotePrinterIDs = new ArrayList<>(); // Why is this here?

	private PrinterRegistry() {
		PrinterListChangesNotifier notifier = BaseLookup.getPrinterListChangesNotifier();
		if (notifier != null) {
			notifier.addListener(this);
		}
	}

	public static PrinterRegistry getInstance() {
		if (instance == null) {
			instance = new PrinterRegistry();
		}

		return instance;
	}

	public Map<String, Printer> getRemotePrinters() {
		return remotePrinters;
	}

	public List<String> getRemotePrinterIDs() {
		return remotePrinterIDs;
	}

	@Override
	public void whenPrinterAdded(Printer printer) {
		if (!remotePrinters.containsValue(printer)) {

			String printerID = null;

			do {
				printerID = SystemUtils.generate16DigitID();
			} while (remotePrinters.containsKey(printerID));

			LOGGER.info("New printer detected - id is " + printerID);
			synchronized (this) { //TODO: Change these to the concurrent versions instead of synchronising
				remotePrinters.put(printerID, printer);
				remotePrinterIDs.add(printerID);
			}
		}
	}

	@Override
	public void whenPrinterRemoved(Printer printer) {
		for (Entry<String, Printer> printerEntry : remotePrinters.entrySet()) {
			if (printerEntry.getValue() == printer) {
				remotePrinters.remove(printerEntry.getKey());
				remotePrinterIDs.remove(printerEntry.getKey());
				LOGGER.info("Printer with id " + printerEntry.getKey() + " removed");
				break;
			}
		}
	}

	@Override
	public void whenHeadAdded(Printer printer) {
	}

	@Override
	public void whenHeadRemoved(Printer printer, Head head) {
	}

	@Override
	public void whenReelAdded(Printer printer, int reelIndex) {
	}

	@Override
	public void whenReelRemoved(Printer printer, Reel reel, int reelIndex) {
	}

	@Override
	public void whenReelChanged(Printer printer, Reel reel) {
	}

	@Override
	public void whenExtruderAdded(Printer printer, int extruderIndex) {
	}

	@Override
	public void whenExtruderRemoved(Printer printer, int extruderIndex) {
	}

	public String getServerName() {
		return OpenAutoMakerEnv.get().getProperty(ROOT_SERVER_NAME);
	}

	public void setServerName(String serverName) {
		OpenAutoMakerEnv.get().setProperty(ROOT_SERVER_NAME, serverName);
	}
}
