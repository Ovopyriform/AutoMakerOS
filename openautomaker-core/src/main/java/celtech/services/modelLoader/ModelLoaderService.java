package celtech.services.modelLoader;

import java.io.File;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import xyz.openautomaker.base.services.ControllableService;

/**
 *
 * @author ianhudson
 */
public class ModelLoaderService extends Service<ModelLoadResults> implements
ControllableService
{

	private List<File> modelFilesToLoad;

	public final void setModelFilesToLoad(List<File> modelFiles)
	{
		modelFilesToLoad = modelFiles;
	}

	@Override
	protected Task<ModelLoadResults> createTask()
	{
		return new ModelLoaderTask(modelFilesToLoad);
	}

	@Override
	public boolean cancelRun()
	{
		return cancel();
	}

}
