package org.openautomaker.base.configuration.datafileaccessors;

import static org.openautomaker.environment.OpenAutomakerEnv.HEADS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.HeadFileFilter;
import org.openautomaker.base.configuration.fileRepresentation.HeadFile;
import org.openautomaker.base.printerControl.model.Head.HeadType;
import org.openautomaker.environment.OpenAutomakerEnv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author ianhudson
 */
//TODO: Seems like an odd way to sort out the heads.  Fix.
public class HeadContainer
{

	private static final Logger LOGGER = LogManager.getLogger();

    private static HeadContainer instance = null;
    private static final ObservableList<HeadFile> completeHeadList = FXCollections.observableArrayList();
    private static final ObservableMap<String, HeadFile> completeHeadMap = FXCollections.observableHashMap();
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String defaultHeadID = "RBX01-SM";
    public static final HeadType defaultHeadType = HeadType.SINGLE_MATERIAL_HEAD;

    private HeadContainer()
    {
		File applicationHeadDirHandle = OpenAutomakerEnv.get().getApplicationPath(HEADS).toFile();
        File[] applicationheads = applicationHeadDirHandle.listFiles(new HeadFileFilter());
        ArrayList<HeadFile> heads = ingestHeads(applicationheads);
        completeHeadList.addAll(heads);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private ArrayList<HeadFile> ingestHeads(File[] headFiles)
    {
        ArrayList<HeadFile> headList = new ArrayList<>();

        for (File headFile : headFiles)
        {
            try
            {
                HeadFile headFileData = mapper.readValue(headFile, HeadFile.class);

                headList.add(headFileData);
                completeHeadMap.put(headFileData.getTypeCode(), headFileData);

            } catch (IOException ex)
            {
				LOGGER.error("Error loading head " + headFile.getAbsolutePath());
            }
        }

        return headList;
    }

    public static HeadContainer getInstance()
    {
        if (instance == null)
        {
            instance = new HeadContainer();
        }

        return instance;
    }

    public static HeadFile getHeadByID(String headID)
    {
        if (instance == null)
        {
            HeadContainer.getInstance();
        }

        HeadFile returnedHead = completeHeadMap.get(headID);
        return returnedHead;
    }

    public static ObservableList<HeadFile> getCompleteHeadList()
    {
        if (instance == null)
        {
            instance = new HeadContainer();
        }

        return completeHeadList;
    }
}
