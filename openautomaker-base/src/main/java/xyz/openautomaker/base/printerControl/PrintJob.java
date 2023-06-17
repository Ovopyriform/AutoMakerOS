package xyz.openautomaker.base.printerControl;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.PRINT_JOBS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.fileRepresentation.CameraSettings;
import xyz.openautomaker.base.postprocessor.PrintJobStatistics;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 * A PrintJob represents a print run of a Project, and is associated with a print job directory in
 * the print spool directory.
 *
 * @author Ian
 */
public class PrintJob
{
	private static final Logger LOGGER = LogManager.getLogger();
    
    
    private String jobUUID = null;
	private Path printJobPath = null;
    private PrintJobStatistics statistics = null;
    private CameraSettings cameraData = null;
    private boolean cameraDataLoadAttempted = false;
    
	public PrintJob(String jobUUID) {
		this(jobUUID, OpenAutoMakerEnv.get().getUserPath(PRINT_JOBS).resolve(jobUUID));
    }

	public PrintJob(String jobUUID, Path printJobDirectory) {
        this.jobUUID = jobUUID;
		this.printJobPath = printJobDirectory;
    }

    /**
     * Get the location of the gcode file as produced by the slicer
     *
     * @return
     */
	public Path getGCodeFileLocation() {
		return printJobPath.resolve(jobUUID + BaseConfiguration.gcodeTempFileExtension);
    }

    /**
     * Return if the roboxised file is found in the print spool directory
     *
     * @return
     */
    public boolean roboxisedFileExists()
    {
		File printJobFile = getRoboxisedFileLocation().toFile();
        return printJobFile.exists();
    }

    /**
     * @return the jobUUID
     */
    public String getJobUUID()
    {
        return jobUUID;
    }

    /**
     * @return the printJobDirectory
     */
	public Path getJobDirectory()
    {
		return printJobPath;
    }

    /**
     * Get the location of the roboxised file
     *
     * @return
     */
	public Path getRoboxisedFileLocation()
    {
		return printJobPath.resolve(jobUUID + BaseConfiguration.gcodePostProcessedFileHandle + BaseConfiguration.gcodeTempFileExtension);
    }

    /**
     * Get the location of the statistics file
     *
     * @return
     */
	public Path getStatisticsFileLocation()
    {
		return printJobPath.resolve(jobUUID + BaseConfiguration.statisticsFileExtension);
    }

    public PrintJobStatistics getStatistics() throws IOException
    {
        if (statistics == null)
        {
			LOGGER.info("Looking for statistics file in location - " + getStatisticsFileLocation());
            statistics = PrintJobStatistics.importStatisticsFromGCodeFile(getStatisticsFileLocation());
        }
        return statistics;
    }

    /**
     * Get the location of the statistics file
     *
     * @return
     */
	public Path getCameraDataFileLocation()
    {
		return printJobPath.resolve(jobUUID + BaseConfiguration.cameraDataFileExtension);
    }

    public CameraSettings getCameraData()
    {
        if (!cameraDataLoadAttempted && cameraData == null)
        {
            try {
				LOGGER.info("Looking for camera data file in location - " + getCameraDataFileLocation());
                cameraData = CameraSettings.readFromFile(getCameraDataFileLocation());
            }
            catch (IOException ex) {
                
            }
            finally {
                cameraDataLoadAttempted = true;
            }
        }
        return cameraData;
    }
}
