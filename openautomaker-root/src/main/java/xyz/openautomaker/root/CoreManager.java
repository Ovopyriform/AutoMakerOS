package xyz.openautomaker.root;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.SCRIPT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.roboxbase.comms.DiscoveryAgentRemoteEnd;
import celtech.roboxbase.comms.RoboxCommsManager;
import io.dropwizard.lifecycle.Managed;
import xyz.openautomaker.base.ApplicationFeature;
import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.appManager.ConsoleSystemNotificationManager;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.utils.tasks.HeadlessTaskExecutor;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author ianhudson
 */
public class CoreManager implements Managed
{

	private static final Logger LOGGER = LogManager.getLogger();

	private RoboxCommsManager commsManager = null;
	private DiscoveryAgentRemoteEnd discoveryAgent = null;

	@Override
	public void start() throws Exception
	{
		//This horrible monstrosity is to get JavaFX to start.
		//It uses an internal function that is not part of the public API.
		//Solution - remove all references to JavaFX in RoboxBase
		com.sun.javafx.application.PlatformImpl.startup(()->{});

		BaseConfiguration.disableApplicationFeature(ApplicationFeature.AUTO_UPDATE_FIRMWARE);
		BaseLookup.setupDefaultValues();
		BaseLookup.setSystemNotificationHandler(new ConsoleSystemNotificationManager());
		BaseLookup.setTaskExecutor(new HeadlessTaskExecutor());

		discoveryAgent = new DiscoveryAgentRemoteEnd(BaseConfiguration.getApplicationVersion());
		Thread discoveryThread = new Thread(discoveryAgent);
		discoveryThread.setDaemon(true);
		discoveryThread.start();
		;
		commsManager = RoboxCommsManager.getInstance(OpenAutoMakerEnv.get().getApplicationPath(SCRIPT), false, true, false);
		PrinterRegistry.getInstance();
		MountableMediaRegistry.getInstance();
		commsManager.start();
	}

	@Override
	public void stop() throws Exception
	{
		LOGGER.info("Asked to shutdown Root");
		//com.sun.javafx.application.PlatformImpl.exit();
	}
}
