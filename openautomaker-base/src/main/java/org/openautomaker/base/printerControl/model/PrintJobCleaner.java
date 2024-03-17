/*
 * Cleaner to delete old print jobs and timelapse collections on Root.
 */
package org.openautomaker.base.printerControl.model;

import static org.openautomaker.environment.OpenAutomakerEnv.PRINT_JOBS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.BaseLookup;
import org.openautomaker.base.configuration.BaseConfiguration;
import org.openautomaker.base.postprocessor.PrintJobStatistics;
import org.openautomaker.base.printerControl.PrintJob;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.roboxbase.comms.remote.clear.SuitablePrintJob;

/**
 *
 * @author Tony Aldhous
 */
public class PrintJobCleaner {

	private static final int MAX_RETAINED_PRINT_JOBS = 32;
	private static final long MAX_TIMELAPSE_DIRS_SIZE = 1000000000L;

	private static final Logger LOGGER = LogManager.getLogger();

	private Map<File, Long> fileToSizeMap = new HashMap<>();

	public PrintJobCleaner() {
	}

	public void tidyDirectories() {
		BaseLookup.getTaskExecutor().runOnBackgroundThread(() -> {
			tidyPrintJobDirectories();
			tidyTimelapseDirectories();
		});
	}

	public void tidyPrintJobDirectories() {
		List<PrintJobStatistics> orderedStats = new ArrayList<>();
		List<SuitablePrintJob> suitablePrintJobs = new ArrayList<>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

		File printSpoolDir = OpenAutomakerEnv.get().getUserPath(PRINT_JOBS).toFile();
		for (File printJobDir : printSpoolDir.listFiles()) {
			if (printJobDir.isDirectory()) {
				PrintJob pj = new PrintJob(printJobDir.getName());
				File roboxisedGCode = pj.getRoboxisedFileLocation().toFile();
				File statistics = pj.getStatisticsFileLocation().toFile();

				boolean directoryValid = false;
				if (roboxisedGCode.exists() && statistics.exists()) {
					// Valid files - does it work for us?
					try {
						PrintJobStatistics stats = pj.getStatistics();
						orderedStats.add(stats);
						directoryValid = true;
					} catch (IOException ex) {
						LOGGER.error("Failed to load stats from " + printJobDir.getName(), ex);
					}
				}
				if (!directoryValid) {
					try {
						// Delete the invalid directory.
						FileUtils.deleteDirectory(printJobDir);
					} catch (IOException ex) {
						LOGGER.error("Failed to delete invalid project directory \"" + printJobDir.getName() + "\"", ex);
					}
				}
			}
		}

		orderedStats.sort((PrintJobStatistics o1, PrintJobStatistics o2) -> o1.getCreationDate().compareTo(o2.getCreationDate()));
		// Make sure the newest are at the top
		Collections.reverse(orderedStats);

		if (orderedStats.size() > MAX_RETAINED_PRINT_JOBS) {
			// Delete the older projects as there are more than the max number to retain.
			for (int index = MAX_RETAINED_PRINT_JOBS; index < orderedStats.size(); ++index) {
				File printJobDir = OpenAutomakerEnv.get().getUserPath(orderedStats.get(index).getPrintJobID()).toFile();
				try {
					FileUtils.deleteDirectory(printJobDir);
				} catch (IOException ex) {
					LOGGER.error("Failed to delete project directory " + printJobDir, ex);
				}
			}
		}
	}

	public long calculateDirectorySize(File d) {
		try {
			return Files.walk(d.toPath()).mapToLong((f) -> f.toFile().length()).sum();
		} catch (IOException ex) {
			LOGGER.error("Failed to find length of " + d, ex);
			return 0L;
		}
	}

	public void tidyTimelapseDirectories() {
		File timelapseDir = BaseConfiguration.getTimelapseDirectory().toFile();
		List<File> fileList = new ArrayList<>();
		long totalSize = 0;
		for (File timelapsePrinterDir : timelapseDir.listFiles()) {
			for (File f : timelapsePrinterDir.listFiles()) {
				if (f.canWrite() && f.isDirectory()) {
					long s = calculateDirectorySize(f);
					if (s > 0) {
						fileToSizeMap.put(f, s);
						fileList.add(f);
					}
				}
			}
		}

		if (totalSize > MAX_TIMELAPSE_DIRS_SIZE) {
			fileList.sort((File f1, File f2) -> {
				long d = f1.lastModified() - f2.lastModified();
				if (d == 0)
					d = fileToSizeMap.get(f1) - fileToSizeMap.get(f2);
				return (d > 0 ? 1 : (d < 0 ? -1 : 0));
			});

			for (File deletableDir : fileList) {
				try {
					FileUtils.deleteDirectory(deletableDir);
				} catch (IOException ex) {
					LOGGER.error("Failed to delete timelapse directory " + deletableDir, ex);
				}
				totalSize -= fileToSizeMap.get(deletableDir);
				if (totalSize < MAX_TIMELAPSE_DIRS_SIZE)
					break;
			}
		}
	}
}
