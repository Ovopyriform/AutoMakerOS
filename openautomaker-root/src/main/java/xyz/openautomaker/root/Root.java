package xyz.openautomaker.root;

import static xyz.openautomaker.environment.OpenAutoMakerEnv.ROOT_ACCESS_PIN;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;

import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.forms.MultiPartBundle;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.utils.ApplicationUtils;
import xyz.openautomaker.environment.OpenAutoMakerEnv;
import xyz.openautomaker.root.comms.CameraCommsManager;
import xyz.openautomaker.root.custom_dropwizard.AuthenticatedAssetsBundle;
import xyz.openautomaker.root.custom_dropwizard.ExternalAuthenticatedAssetsBundle;
import xyz.openautomaker.root.security.RootAPIAuthFilter;
import xyz.openautomaker.root.security.RootAPIAuthenticator;
import xyz.openautomaker.root.security.User;

/**
 *
 * @author Ian
 */
public class Root extends Application<RoboxRemoteConfiguration>
{

	private static final Logger LOGGER = LogManager.getLogger();

	private static Root instance = null;
	private CoreManager coreManager = null;
	private CameraCommsManager cameraCommsManager;
	private boolean isStopping = false;
	private boolean isUpgrading = false;

	public static void main(String[] args) throws Exception
	{
		instance = new Root();
		instance.run(args);
	}

	public static Root getInstance()
	{
		return instance;
	}

	@Override
	public String getName()
	{
		return "printerControl";
	}

	@Override
	public void initialize(Bootstrap<RoboxRemoteConfiguration> bootstrap)
	{
		BaseConfiguration.initialise(Root.class);
		coreManager = new CoreManager();

		cameraCommsManager = new CameraCommsManager();

		bootstrap.addBundle(new MultiPartBundle());

		String externalStaticDir = BaseConfiguration.getExternalStaticDirectory();
		AuthenticatedAssetsBundle webControlAssetsBundle = null;

		if (externalStaticDir != null && !externalStaticDir.isEmpty())
		{
			Path externalStaticDirPath = Paths.get(externalStaticDir);
			if (Files.isDirectory(externalStaticDirPath) &&
					Files.isReadable(externalStaticDirPath))
			{
				webControlAssetsBundle = new ExternalAuthenticatedAssetsBundle(externalStaticDirPath,
						"/assets", "/", new RootAPIAuthenticator());
			}
		}

		if (webControlAssetsBundle == null)
		{
			webControlAssetsBundle = new AuthenticatedAssetsBundle(
					"/assets", "/", new RootAPIAuthenticator());
		}
		bootstrap.addBundle(webControlAssetsBundle);
	}

	@Override
	public void run(RoboxRemoteConfiguration configuration,
			Environment environment)
	{
		String defaultApplicationPIN = configuration.getDefaultPIN();

		environment.lifecycle().manage(coreManager);

		environment.lifecycle().addServerLifecycleListener((Server server) ->
		{
			server.setStopAtShutdown(true);
			server.setStopTimeout(500);
		});

		environment.jersey().setUrlPattern("/api/*");

		final AdminAPI adminAPI = new AdminAPI();
		final LowLevelAPI lowLevelAPI = new LowLevelAPI();
		final PublicPrinterControlAPI highLevelAPI = new PublicPrinterControlAPI();
		final DiscoveryAPI discoveryAPI = new DiscoveryAPI(cameraCommsManager);
		final CameraAPI cameraAPI = new CameraAPI(cameraCommsManager);

		final AppSetupHealthCheck healthCheck
		= new AppSetupHealthCheck(configuration.getDefaultPIN());
		environment.healthChecks().register("template", healthCheck);
		environment.jersey().register(adminAPI);
		environment.jersey().register(lowLevelAPI);
		environment.jersey().register(highLevelAPI);
		environment.jersey().register(discoveryAPI);
		environment.jersey().register(cameraAPI);
		environment.jersey().register(CORSFilter.class);

		environment.jersey().register(new AuthDynamicFeature(new RootAPIAuthFilter.Builder<User>()
				.setAuthenticator(new RootAPIAuthenticator())
				.setRealm("Robox Root API")
				.buildAuthFilter()));
		environment.admin().addTask(new AdminUpdateTask());

		String hostAddress = "";
		try
		{
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements())
			{
				NetworkInterface ni = networkInterfaces
						.nextElement();
				Enumeration<InetAddress> nias = ni.getInetAddresses();
				while (nias.hasMoreElements())
				{
					InetAddress ia = nias.nextElement();
					if (!ia.isLinkLocalAddress()
							&& !ia.isLoopbackAddress()
							&& ia instanceof Inet4Address)
					{
						hostAddress = ia.getHostAddress();
						break;
					}
				}
			}
		} catch (SocketException e)
		{
			LOGGER.error("unable to get current IP " + e.getMessage());
		}

		cameraCommsManager.start();

		ApplicationUtils.outputApplicationStartupBanner();
		LOGGER.info("Root started up with IP " + hostAddress);
	}

	public void stop()
	{
		LOGGER.info("Stopping ...");
		isStopping = true;
		try {
			coreManager.stop();
		}
		catch (Exception ex) {
			LOGGER.error("Caught exception during stop.");
		}

		cameraCommsManager.shutdown();

		BaseConfiguration.shutdown();
		System.exit(0);
	}

	public void restart()
	{
		//Rely on the system process manager to restart us...
		// Kill the browser to make sure that the cache is zapped
		//if (BaseConfiguration.getMachineType() == MachineType.LINUX_X64
		//        || BaseConfiguration.getMachineType() == MachineType.LINUX_X86)
		//{
		//    try
		//    {
		//        Runtime.getRuntime().exec("killall chromium-browser");
		//    } catch (IOException ex)
		//    {
		//        LOGGER.exception("Failed to shut down browser", ex);
		//    }
		//}
		stop();
	}

	public void setApplicationPIN(String applicationPIN) {
		OpenAutoMakerEnv.get().setProperty(ROOT_ACCESS_PIN, applicationPIN);
	}

	public String getApplicationPIN() {
		return OpenAutoMakerEnv.get().getProperty(ROOT_ACCESS_PIN);
	}

	public void resetApplicationPINToDefault() {
		OpenAutoMakerEnv.get().removeProperty(ROOT_ACCESS_PIN);
	}

	public boolean getIsStopping() {
		return isStopping;
	}

	public void setIsStopping(boolean isStopping) {
		this.isStopping = isStopping;
	}

	public boolean getIsUpgrading() {
		return isUpgrading;
	}

	public void setIsUpgrading(boolean isUpgrading) {
		this.isUpgrading = isUpgrading;
	}

	public static boolean isResponding() {
		return instance != null && !(instance.isStopping || instance.isUpgrading);
	}
}
