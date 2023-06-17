package xyz.openautomaker.base.configuration.datafileaccessors;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.fileRepresentation.SlicerMappings;

/**
 *
 * @author ianhudson
 */
public final class SlicerMappingsContainer
{

	private static final Logger LOGGER = LogManager.getLogger();

    private static SlicerMappingsContainer instance = null;
    private static SlicerMappings slicerMappingsFile = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String defaultSlicerMappingsFileName = "slicermapping.dat";

    private SlicerMappingsContainer()
    {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        loadSlicerMappingsFile();
    }
    
    public void loadSlicerMappingsFile() 
    {        
		File slicerMappingsInputFile = BaseConfiguration.getApplicationPrintProfileDirectory().resolve(defaultSlicerMappingsFileName).toFile();
        if (!slicerMappingsInputFile.exists())
        {
            slicerMappingsFile = new SlicerMappings();
            try
            {
                mapper.writeValue(slicerMappingsInputFile, slicerMappingsFile);
            } catch (IOException ex)
            {
				LOGGER.error("Error trying to load slicer mapping file");
            }
        } else
        {
            try
            {
                slicerMappingsFile = mapper.readValue(slicerMappingsInputFile, SlicerMappings.class);

            } catch (IOException ex)
            {
				LOGGER.error("Error loading slicer mapping file " + slicerMappingsInputFile.getAbsolutePath(), ex);
            }
        }
    }

    /**
     *
     * @return
     */
    public static SlicerMappingsContainer getInstance()
    {
        if (instance == null)
        {
            instance = new SlicerMappingsContainer();
        }

        return instance;
    }

    /**
     *
     * @return
     */
    public static SlicerMappings getSlicerMappings()
    {
        if (instance == null)
        {
            instance = new SlicerMappingsContainer();
        }

        return slicerMappingsFile;
    }
}
