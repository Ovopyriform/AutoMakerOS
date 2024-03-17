package org.openautomaker.base.configuration.datafileaccessors;

import static org.openautomaker.environment.OpenAutomakerEnv.CAMERA_PROFILES;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.BaseConfiguration;
import org.openautomaker.base.configuration.fileRepresentation.CameraProfile;
import org.openautomaker.environment.OpenAutomakerEnv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

/**
 *
 * @author George Salter
 */
public class CameraProfileContainer 
{
	private static final Logger LOGGER = LogManager.getLogger();
    
    private static CameraProfileContainer instance;
    
    private final Map<String, CameraProfile> cameraProfilesMap;

    private final CameraProfile defaultCameraProfile;
 
    private CameraProfileContainer()
    {
        cameraProfilesMap = new HashMap<>();
        defaultCameraProfile = new CameraProfile();
        defaultCameraProfile.setSystemProfile(true);
        cameraProfilesMap.put(defaultCameraProfile.getProfileName().toLowerCase(), defaultCameraProfile);
        loadCameraProfiles();
    }
    
    public static CameraProfileContainer getInstance() 
    {
        if(instance == null)
        {
            instance = new CameraProfileContainer();
        }
        return instance;
    }
    
    private void loadCameraProfiles() 
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module());
        
		Path appCameraProfilesPath = OpenAutomakerEnv.get().getApplicationPath(CAMERA_PROFILES);
		Path userCameraProfilePath = OpenAutomakerEnv.get().getUserPath(CAMERA_PROFILES);
        
        String cameraProfileSearchString = "*" + BaseConfiguration.cameraProfileFileExtention;
        
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(appCameraProfilesPath, cameraProfileSearchString))
        {
            for(Path path : stream)
            {
                CameraProfile cameraProfile = objectMapper.readValue(path.toFile(), CameraProfile.class);
                cameraProfile.setSystemProfile(true);
                cameraProfilesMap.put(cameraProfile.getProfileName().toLowerCase(), cameraProfile);
            }
        } catch (IOException ex) 
        {
			LOGGER.error("Error when loading Camera profiles from " + appCameraProfilesPath.toString(), ex);
        }

        try(DirectoryStream<Path> stream = Files.newDirectoryStream(userCameraProfilePath, cameraProfileSearchString))
        {
            for(Path path : stream)
            {
                CameraProfile cameraProfile = objectMapper.readValue(path.toFile(), CameraProfile.class);
                cameraProfilesMap.put(cameraProfile.getProfileName().toLowerCase(), cameraProfile);
            }
        } catch (IOException ex) 
        {
			LOGGER.error("Error when loading Camera profiles from " + appCameraProfilesPath.toString(), ex);
        }
    }
    
    public void saveCameraProfile(CameraProfile cameraProfile)
    {
        if (cameraProfile.isSystemProfile())
        {
			LOGGER.warn("Can't save system camera profile.");
        }
        else
        {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new Jdk8Module());

            String cameraProfileFileName = cameraProfile.getProfileName() + BaseConfiguration.cameraProfileFileExtention;
			Path cameraProfilePath = OpenAutomakerEnv.get().getUserPath(CAMERA_PROFILES).resolve(cameraProfileFileName);

            try 
            {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(cameraProfilePath.toFile(), cameraProfile);
                cameraProfilesMap.put(cameraProfile.getProfileName().toLowerCase(), cameraProfile);
            } catch (IOException ex) 
            {
				LOGGER.error("Error when trying to save profile of " + cameraProfilePath.toString(), ex);
            }
        }
    }
    
    public void deleteCameraProfile(CameraProfile cameraProfile)
    {
        if (cameraProfile.isSystemProfile())
        {
			LOGGER.warn("Can't delete system camera profile.");
        }
        else
        {
            String profileName = cameraProfile.getProfileName();
            String profileNameLC = profileName.toLowerCase();
            
            if (cameraProfilesMap.containsKey(profileNameLC))
            {
                cameraProfilesMap.remove(profileNameLC);
            } else
            {
				LOGGER.error("File " + profileName + ", doesn't exist");
            }

			Path fileToDelete = OpenAutomakerEnv.get().getUserPath(CAMERA_PROFILES).resolve(profileName + BaseConfiguration.cameraProfileFileExtention);
            try 
            {
                Files.deleteIfExists(fileToDelete);
            } catch (IOException ex) 
            {
				LOGGER.error("Error when trying to delete profile of " + fileToDelete.toString(), ex);
            }
        }
    }
    
    public Map<String, CameraProfile> getCameraProfilesMap()
    {
        return cameraProfilesMap;
    }

    public CameraProfile getProfileByName(String profileName)
    {
        return cameraProfilesMap.get(profileName.toLowerCase());
    }

    public CameraProfile getDefaultProfile()
    {
        return cameraProfilesMap.get(BaseConfiguration.defaultCameraProfileName.toLowerCase());
    }
}
