package xyz.openautomaker.base.services.slicer;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.SCRIPT;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.environment.OpenAutoMakerEnv;
import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.environment.MachineType;

/**
 *
 * @author George Salter
 */
public class SlicerUtils {
	private static final Logger LOGGER = LogManager.getLogger();

	//TODO: Not really needed.  Maven should build the dist with the appropriate files.
	public static void killSlicing(SlicerType slicerType) {
		String windowsKillCommand = "taskkill /IM \"CuraEngine.exe\" /F";
		String macKillCommand = "KillCuraEngine.mac.sh";
		String linuxKillCommand = "KillCuraEngine.linux.sh";

		List<String> commands = new ArrayList<>();

		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		Path scriptPath = env.getApplicationPath(SCRIPT);

		switch (env.getMachineType()) {
			//			case WINDOWS_95:
			//				commands.add("command.com");
			//				commands.add("/S");
			//				commands.add("/C");
			//				commands.add(windowsKillCommand);
			//				break;
			case WINDOWS:
				commands.add("cmd.exe");
				commands.add("/S");
				commands.add("/C");
				commands.add(windowsKillCommand);
				break;
			case MAC:
				commands.add(scriptPath.resolve(macKillCommand).toString());
				break;
			case LINUX:
				commands.add(scriptPath.resolve(linuxKillCommand).toString());
				break;
			default:
				break;
		}

		if (!commands.isEmpty()) {
			ProcessBuilder killSlicerProcessBuilder = new ProcessBuilder(commands);
			if (env.getMachineType() != MachineType.WINDOWS) {
				Path binDir = env.getApplicationPath(SCRIPT);
				LOGGER.debug("Set working directory (Non-Windows) to " + binDir);
				killSlicerProcessBuilder.directory(binDir.toFile());
			}
			try {
				Process slicerKillProcess = killSlicerProcessBuilder.start();
				slicerKillProcess.waitFor();
			}
			catch (IOException | InterruptedException ex) {
				LOGGER.error("Exception whilst killing slicer", ex);
			}
		}
	}
}
