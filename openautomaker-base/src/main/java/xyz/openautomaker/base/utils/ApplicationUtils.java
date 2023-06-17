package xyz.openautomaker.base.utils;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
//TODO: Why is this even here?
public class ApplicationUtils {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final String STARTUP_MESSAGE = """
			_
			**********************************************************************
			Starting %s
			Date: %tc
			Version: %s
			Installation directory: %s
			Machine type: %s
			**********************************************************************
			""";

	private static final String SHUTDOWN_MESSAGE = """
			_
			**********************************************************************
			Shutting down %s
			Date: %tc
			**********************************************************************
			""";

	public static void outputApplicationStartupBanner() {

		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		LOGGER.info(String.format(STARTUP_MESSAGE, env.getName(), new Date(), env.getVersion(), env.getApplicationPath().toString(), env.getMachineType().name()));
    }

    public static void outputApplicationShutdownBanner()
    {
		OpenAutoMakerEnv env = OpenAutoMakerEnv.get();
		LOGGER.info(SHUTDOWN_MESSAGE, env.getName(), new Date());
    }
}
