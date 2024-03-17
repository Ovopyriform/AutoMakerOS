package org.openautomaker.base.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.BaseConfiguration;
import org.openautomaker.base.configuration.fileRepresentation.CameraSettings;
import org.openautomaker.base.postprocessor.PrintJobStatistics;

import com.google.common.io.Files;

/**
 *
 * @author George Salter
 */
public class PrintJobUtils {

	private static final Logger LOGGER = LogManager.getLogger();

	public static void assignPrintJobIdToProject(String jobUUID, Path printJobDirectoryPath, String printQuality, Optional<CameraSettings> cameraData) {
		try {
			renameFilesInPrintJob(jobUUID, printJobDirectoryPath, printQuality);
			Path statisticsFileLocation = printJobDirectoryPath.resolve(jobUUID + BaseConfiguration.statisticsFileExtension);

			PrintJobStatistics statistics = PrintJobStatistics.importStatisticsFromGCodeFile(statisticsFileLocation);
			statistics.setPrintJobID(jobUUID);
			statistics.writeStatisticsToFile(statisticsFileLocation);
		}
		catch (IOException ex) {
			LOGGER.error("Exception when reading or writing statistics file", ex);
		}
		cameraData.ifPresent((cd) -> {
			try {
				Path cameraFileLocation = printJobDirectoryPath.resolve(jobUUID + BaseConfiguration.cameraDataFileExtension);
				cd.writeToFile(cameraFileLocation);
			}
			catch (IOException ex) {
				LOGGER.error("Exception when writing camera data", ex);
			}
		});
	}

	private static void renameFilesInPrintJob(String jobUUID, Path printJobDirectoryPath, String printQuality) {
		File printJobDir = printJobDirectoryPath.toFile();
		Stream.of(printJobDir.listFiles()).forEach(file -> {
			try {
				String originalFile = file.getPath();
				if (originalFile.contains(printQuality)) {
					String newFile = originalFile.replace(printQuality, jobUUID);
					Files.move(new File(originalFile), new File(newFile));
				}
			}
			catch (IOException ex) {
				LOGGER.error("Error when renaiming files for print job: " + jobUUID, ex);
			}
		});
	}
}
