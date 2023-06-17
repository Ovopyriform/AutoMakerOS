package celtech.configuration.datafileaccessors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import celtech.configuration.UserPreferences;
import celtech.configuration.fileRepresentation.UserPreferenceFile;
import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author ianhudson
 */
public class UserPreferenceContainer
{

	private static final Logger LOGGER = LogManager.getLogger(UserPreferenceContainer.class.getName());
	private static UserPreferenceContainer instance = null;
	private static UserPreferenceFile userPreferenceFile = null;
	private static final ObjectMapper mapper = new ObjectMapper();
	public static final String defaultUserPreferenceFilename = "roboxpreferences.pref";

	private UserPreferenceContainer()
	{
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		Path userPreferencesInputPath = OpenAutoMakerEnv.get().getUserPath().resolve(defaultUserPreferenceFilename);

		File userPreferenceInputFile = userPreferencesInputPath.toFile();
		if (!userPreferenceInputFile.exists())
		{
			userPreferenceFile = new UserPreferenceFile();
			try
			{
				mapper.writeValue(userPreferenceInputFile, userPreferenceFile);
			} catch (IOException ex)
			{
				LOGGER.error("Error trying to create user preferences file at " + userPreferenceInputFile.getAbsolutePath(), ex);
			}
		} else
		{
			try
			{
				mapper.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
				userPreferenceFile = mapper.readValue(userPreferenceInputFile, UserPreferenceFile.class);
			} catch (IOException ex)
			{
				LOGGER.error("Error loading user preferences " + userPreferenceInputFile.getAbsolutePath() + ": " + ex.getMessage());
			}
		}

		if (userPreferenceFile.getSlicerType() == null)
			userPreferenceFile.setSlicerType(SlicerType.Cura4);
	}

	/**
	 *
	 * @return
	 */
	public static UserPreferenceContainer getInstance()
	{
		if (instance == null)
		{
			instance = new UserPreferenceContainer();
		}

		return instance;
	}

	/**
	 *
	 * @return
	 */
	public static UserPreferenceFile getUserPreferenceFile()
	{
		if (instance == null)
		{
			instance = new UserPreferenceContainer();
		}

		return userPreferenceFile;
	}

	public static void savePreferences(UserPreferences userPreferences)
	{
		Path userPreferencesInputPath = OpenAutoMakerEnv.get().getUserPath().resolve(defaultUserPreferenceFilename);

		File userPreferenceInputFile = userPreferencesInputPath.toFile();

		userPreferenceFile.populateFromSettings(userPreferences);

		try
		{
			mapper.writeValue(userPreferenceInputFile, userPreferenceFile);
		} catch (IOException ex)
		{
			LOGGER.error("Error trying to write user preferences");
		}
	}
}
