package celtech.services.modelLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.coreUI.visualisation.metaparts.ModelLoadResult;
import celtech.coreUI.visualisation.metaparts.ModelLoadResultType;
import celtech.utils.threed.importers.obj.ObjImporter;
import celtech.utils.threed.importers.stl.STLImporter;
import celtech.utils.threed.importers.svg.SVGImporter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.utils.FileUtilities;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author ianhudson
 */
public class ModelLoaderTask extends Task<ModelLoadResults>
{

	private static final Logger LOGGER = LogManager.getLogger();

	private final List<File> modelFilesToLoad;
	private final DoubleProperty percentProgress = new SimpleDoubleProperty();

	public ModelLoaderTask(List<File> modelFilesToLoad)
	{
		this.modelFilesToLoad = modelFilesToLoad;

		percentProgress.addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t, Number t1)
			{
				updateProgress(t1.doubleValue(), 100.0);
			}
		});
	}

	@Override
	protected ModelLoadResults call() throws Exception
	{
		List<ModelLoadResult> modelLoadResultList = new ArrayList<>();

		updateTitle(OpenAutoMakerEnv.getI18N().t("dialogs.loadModelTitle"));

		for (File modelFileToLoad : modelFilesToLoad)
		{
			LOGGER.info("Model file load started:" + modelFileToLoad.getName());

			String modelFilePath = modelFileToLoad.getAbsolutePath();
			updateMessage(OpenAutoMakerEnv.getI18N().t("dialogs.gcodeLoadMessagePrefix")
					+ modelFileToLoad.getName());
			updateProgress(0, 100);

			final List<String> fileNamesToLoad = new ArrayList<>();

			if (modelFilePath.toUpperCase().endsWith("ZIP"))
			{
				//                modelLoadResults.setShouldCentre(false);
				ZipFile zipFile = new ZipFile(modelFilePath);

				try
				{
					final Enumeration<? extends ZipEntry> entries = zipFile.entries();
					while (entries.hasMoreElements())
					{
						final ZipEntry entry = entries.nextElement();
						final String tempTargetname = BaseConfiguration.getUserTempDirectory() + entry.getName();
						FileUtilities.writeStreamToFile(zipFile.getInputStream(entry), tempTargetname);
						fileNamesToLoad.add(tempTargetname);
					}
				} catch (IOException ex)
				{
					LOGGER.error("Error unwrapping zip - " + ex.getMessage());
				} finally
				{
					zipFile.close();
				}
			} else
			{
				fileNamesToLoad.add(modelFilePath);
			}

			for (String filenameToLoad : fileNamesToLoad)
			{
				ModelLoadResult loadResult = loadTheFile(filenameToLoad);
				if (loadResult != null)
				{
					modelLoadResultList.add(loadResult);
				} else
				{
					LOGGER.warn("Failed to load model: " + filenameToLoad);
				}
			}
		}

		ModelLoadResultType type = null;
		if (!modelLoadResultList.isEmpty())
		{
			type = modelLoadResultList.get(0).getType();
		}
		return new ModelLoadResults(type, modelLoadResultList);
	}

	private ModelLoadResult loadTheFile(String modelFileToLoad)
	{
		ModelLoadResult modelLoadResult = null;

		if (modelFileToLoad.toUpperCase().endsWith("OBJ"))
		{
			ObjImporter reader = new ObjImporter();
			modelLoadResult = reader.loadFile(this, modelFileToLoad, percentProgress, false);
		} else if (modelFileToLoad.toUpperCase().endsWith("STL"))
		{
			STLImporter reader = new STLImporter();
			modelLoadResult = reader.loadFile(this, new File(modelFileToLoad),
					percentProgress);
		} else if (modelFileToLoad.toUpperCase().endsWith("SVG"))
		{
			SVGImporter reader = new SVGImporter();
			modelLoadResult = reader.loadFile(this, new File(modelFileToLoad),
					percentProgress);
		}

		return modelLoadResult;
	}

	/**
	 *
	 * @param message
	 */
	public void updateMessageText(String message)
	{
		updateMessage(message);
	}
}
