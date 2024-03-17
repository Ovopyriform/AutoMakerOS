package org.openautomaker;

import static org.openautomaker.environment.OpenAutomakerEnv.SCRIPT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.BaseLookup;
import org.openautomaker.base.configuration.BaseConfiguration;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.printerControl.model.PrinterException;
import org.openautomaker.base.utils.ApplicationUtils;
import org.openautomaker.base.utils.tasks.TaskResponse;
import org.openautomaker.environment.I18N;
import org.openautomaker.environment.OpenAutomakerEnv;
import org.openautomaker.environment.preference.DetectLoadedFilamentPreference;
import org.openautomaker.environment.preference.SafetyFeaturesPreference;
import org.openautomaker.environment.preference.SearchForRemoteCamerasPreference;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterEnabledPreference;
import org.openautomaker.ui.utils.FXProperty;

import celtech.Lookup;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.ChoiceLinkDialogBox;
import celtech.coreUI.components.ChoiceLinkDialogBox.PrinterDisconnectedException;
import celtech.roboxbase.comms.RoboxCommsManager;
import celtech.roboxbase.comms.interapp.InterAppCommsConsumer;
import celtech.roboxbase.comms.interapp.InterAppCommsThread;
import celtech.roboxbase.comms.interapp.InterAppRequest;
import celtech.roboxbase.comms.interapp.InterAppStartupStatus;
import celtech.webserver.LocalWebInterface;
import de.jangassen.MenuToolkit;
import de.jangassen.dialogs.about.AboutStageBuilder;
import de.jangassen.icns.IcnsParser;
import de.jangassen.icns.IcnsType;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class OpenAutomaker extends Application implements /* AutoUpdateCompletionListener, */ InterAppCommsConsumer {

	public static final String AUTOMAKER_ICON_256 = "/org/openautomaker/images/AutoMakerIcon_256x256.png";
	public static final String AUTOMAKER_ICON_64 = "/org/openautomaker/images/AutoMakerIcon_64x64.png";
	public static final String AUTOMAKER_ICON_32 = "/org/openautomaker/images/AutoMakerIcon_32x32.png";

	private static final Logger LOGGER = LogManager.getLogger();

	private final OpenAutomakerEnv fOpenAutomakerEnv;
	private final SafetyFeaturesPreference fSafetyFeaturesPreference;
	private final DetectLoadedFilamentPreference fDetectLoadedFilamentPreference;
	private final SearchForRemoteCamerasPreference fSearchForRemoteCamerasPreference;
	private final VirtualPrinterEnabledPreference fUseVirtualPrinterPreference;

	private static DisplayManager displayManager = null;

	private RoboxCommsManager commsManager = null;
	//private AutoUpdate autoUpdater = null;
	private List<Printer> waitingForCancelFrom = new ArrayList<>();
	private Stage mainStage;
	private LocalWebInterface localWebInterface = null;
	private final InterAppCommsThread interAppCommsListener = new InterAppCommsThread();
	private final List<String> modelsToLoadAtStartup = new ArrayList<>();
	private String modelsToLoadAtStartup_projectName = "Import";
	private boolean modelsToLoadAtStartup_dontgroup = false;

	private final String uriScheme = "automaker:";
	private final String paramDivider = "\\?";

	public OpenAutomaker() {
		fOpenAutomakerEnv = OpenAutomakerEnv.get();
		fSafetyFeaturesPreference = new SafetyFeaturesPreference();
		fDetectLoadedFilamentPreference = new DetectLoadedFilamentPreference();
		fSearchForRemoteCamerasPreference = new SearchForRemoteCamerasPreference();
		fUseVirtualPrinterPreference = new VirtualPrinterEnabledPreference();
	}

	@Override
	public void init() throws Exception {
		AutoMakerInterAppRequestCommands interAppCommand = AutoMakerInterAppRequestCommands.NONE;
		List<InterAppParameter> interAppParameters = new ArrayList<>();

		if (getParameters().getUnnamed().size() == 1) {
			String potentialParam = getParameters().getUnnamed().get(0);
			if (potentialParam.startsWith(uriScheme)) {
				//We've been started through a URI scheme
				potentialParam = potentialParam.replaceAll(uriScheme, "");

				String[] paramParts = potentialParam.split(paramDivider);
				if (paramParts.length == 2) {
					//                    LOGGER.info("Viable param:" + potentialParam + "->" + paramParts[0] + " -------- " + paramParts[1]);
					// Got a viable param
					switch (paramParts[0]) {
						case "loadModel":
							String[] subParams = paramParts[1].split("&");

							for (String subParam : subParams) {
								InterAppParameter parameter = InterAppParameter.fromParts(subParam);
								if (parameter != null) {
									interAppParameters.add(parameter);
								}
							}
							if (interAppParameters.size() > 0) {
								interAppCommand = AutoMakerInterAppRequestCommands.LOAD_MESH_INTO_LAYOUT_VIEW;
							}
							break;
						default:
							break;
					}
				}
			}
		}

		AutoMakerInterAppRequest interAppCommsRequest = new AutoMakerInterAppRequest();
		interAppCommsRequest.setCommand(interAppCommand);
		interAppCommsRequest.setUrlEncodedParameters(interAppParameters);

		InterAppStartupStatus startupStatus = interAppCommsListener.letUsBegin(interAppCommsRequest, this);

		if (startupStatus == InterAppStartupStatus.STARTED_OK) {
			BaseConfiguration.initialise(OpenAutomaker.class);
			Lookup.setupDefaultValuesFX();

			ApplicationUtils.outputApplicationStartupBanner();

			commsManager = RoboxCommsManager.getInstance(fOpenAutomakerEnv.getApplicationPath(SCRIPT), false, FXProperty.bind(fDetectLoadedFilamentPreference),
					FXProperty.bind(fSearchForRemoteCamerasPreference));

			switch (interAppCommand) {
				case LOAD_MESH_INTO_LAYOUT_VIEW:

					interAppCommsRequest.getUnencodedParameters().forEach(
							param -> {
								if (param.getType() == InterAppParameterType.MODEL_NAME) {
									modelsToLoadAtStartup.add(param.getUnencodedParameter());
								}
								else if (param.getType() == InterAppParameterType.PROJECT_NAME) {
									modelsToLoadAtStartup_projectName = param.getUnencodedParameter();
								}
								else if (param.getType() == InterAppParameterType.DONT_GROUP_MODELS) {
									switch (param.getUnencodedParameter()) {
										case "true":
											modelsToLoadAtStartup_dontgroup = true;
											break;
										default:
											break;
									}
								}
							});
					break;
				default:
					break;
			}
		}

		LOGGER.debug("Startup status was: " + startupStatus.name());
	}

	//TODO: Finalise this for the mac, windows and linux.
	private void attachMenus(Stage stage) {
		MenuToolkit tk = MenuToolkit.toolkit(fOpenAutomakerEnv.getLocale());

		AboutStageBuilder aboutStageBuilder = AboutStageBuilder.start("About OpenAutomaker")
				.withAppName("OpenAutomaker")
				.withCloseOnFocusLoss().withText("Line 1\nLine2")
				.withVersionString("Version " + fOpenAutomakerEnv.getVersion())
				.withCopyright("Copyright \u00A9 " + Calendar
						.getInstance().get(Calendar.YEAR));

		try {
			//AutoMakerEnvironment.get().getApplicationPath().resolve("AutoMaker.icns").toFile();
			IcnsParser parser = IcnsParser.forFile(fOpenAutomakerEnv.getApplicationPath().resolve("..").resolve("Resources").resolve("AutoMaker.icns").toFile());
			aboutStageBuilder = aboutStageBuilder.withImage(new Image(parser.getIconStream(IcnsType.ICON)));
		}
		catch (IOException e) {
			LOGGER.error("Couold not load ICNS");
		}

		Menu applicationMenu = tk.createDefaultApplicationMenu("OpenAutomaker", aboutStageBuilder.build());

		//TODO: Create the full menu bar.  Only really needed once I have the jar based project structure
		MenuBar bar = new MenuBar();
		bar.useSystemMenuBarProperty().set(true);
		bar.getMenus().add(applicationMenu);
		tk.setMenuBar(bar);

		//		Menu menu = new Menu("test");
		//		MenuItem myItem = new MenuItem("Hallo welt");
		//		menu.getItems().add(myItem);
		//		tk.setDockIconMenu(menu);
	}

	@Override
	public void start(Stage stage) throws Exception {
		mainStage = new Stage();

		I18N i18n = new I18N();

		try {
			displayManager = DisplayManager.getInstance();
			//i18nBundle = BaseLookup.getLanguageBundle();

			String applicationName = i18n.t("application.title");

			displayManager.configureDisplayManager(mainStage, applicationName, modelsToLoadAtStartup_projectName, modelsToLoadAtStartup, modelsToLoadAtStartup_dontgroup);

			attachIcons(mainStage);

			//if (OpenAutomakerEnv.get().getMachineType() == MachineType.MAC)
			attachMenus(mainStage);

			mainStage.setOnCloseRequest((WindowEvent event) -> {
				boolean transferringDataToPrinter = false;
				boolean willShutDown = true;

				for (Printer printer : BaseLookup.getConnectedPrinters()) {
					transferringDataToPrinter = transferringDataToPrinter | printer.getPrintEngine().transferGCodeToPrinterService.isRunning();
				}

				if (transferringDataToPrinter) {
					boolean shutDownAnyway = BaseLookup.getSystemNotificationHandler().showJobsTransferringShutdownDialog();

					if (shutDownAnyway) {
						for (Printer printer : BaseLookup.getConnectedPrinters()) {
							waitingForCancelFrom.add(printer);

							try {
								printer.cancel((TaskResponse<Object> taskResponse) -> {
									waitingForCancelFrom.remove(printer);
								}, fSafetyFeaturesPreference.get());
							}
							catch (PrinterException ex) {
								LOGGER.error("Error cancelling print on printer " + printer.getPrinterIdentity().printerFriendlyNameProperty().get() + " - " + ex.getMessage());
							}
						}
					}
					else {
						event.consume();
						willShutDown = false;
					}
				}

				if (willShutDown) {
					ApplicationUtils.outputApplicationShutdownBanner();
					Platform.exit();
				}
				else {
					LOGGER.info("Shutdown aborted - transfers to printer were in progress");
				}
			});
		}
		catch (Throwable ex) {
			ex.printStackTrace();
			Platform.exit();
		}

		showMainStage();
	}

	private void attachIcons(Stage stage) {
		stage.getIcons().addAll(
				new Image(getClass().getResourceAsStream(AUTOMAKER_ICON_256)),
				new Image(getClass().getResourceAsStream(AUTOMAKER_ICON_64)),
				new Image(getClass().getResourceAsStream(AUTOMAKER_ICON_32)));
	}

	//	@Override
	//	public void autoUpdateComplete(boolean requiresShutdown) {
	//		if (requiresShutdown) {
	//			Platform.exit();
	//		}
	//	}

	public static void main(String[] args) {
		System.setProperty("javafx.preloader", AutoMakerPreloader.class.getName());
		launch(args);
		// Sometimes a thread stops the application from terminating. The
		// problem is difficult to reproduce, and so far it has not been
		// possible to identify which thread is causing the problem. Calling
		// System.exit(0) should not be necessary and is not good practice, but
		// is a feeble attempt to force all threads to terminate.
		System.exit(0);
	}

	@Override
	public void stop() throws Exception {
		interAppCommsListener.shutdown();

		if (localWebInterface != null) {
			localWebInterface.stop();
		}

		int timeoutStrikes = 3;
		while (waitingForCancelFrom.size() > 0 && timeoutStrikes > 0) {
			Thread.sleep(1000);
			timeoutStrikes--;
		}

		if (commsManager != null) {
			commsManager.shutdown();
		}
		//		if (autoUpdater != null) {
		//			autoUpdater.shutdown();
		//		}
		if (displayManager != null) {
			displayManager.shutdown();
		}
		BaseConfiguration.shutdown();

		if (LOGGER.isDebugEnabled()) {
			outputRunningThreads();
		}

		Thread.sleep(5000);
		BaseLookup.setShuttingDown(true);
	}

	//    private void setAppUserIDForWindows()
	//    {
	//        if (getMachineType() == MachineType.WINDOWS)
	//        {
	//            setCurrentProcessExplicitAppUserModelID("CelTech.AutoMaker");
	//        }
	//    }
	//
	//    public static void setCurrentProcessExplicitAppUserModelID(final String appID)
	//    {
	//        if (SetCurrentProcessExplicitAppUserModelID(new WString(appID)).longValue() != 0)
	//        {
	//            throw new RuntimeException(
	//                "unable to set current process explicit AppUserModelID to: " + appID);
	//        }
	//    }
	//
	//    private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);
	//
	//    static
	//    {
	//        if (getMachineType() == MachineType.WINDOWS)
	//        {
	//            Native.register("shell32");
	//        }
	//    }
	/**
	 * Indicates whether any threads are believed to be running
	 *
	 * @return
	 */
	//	private boolean areThreadsStillRunning() {
	//		ThreadGroup rootThreadGroup = getRootThreadGroup();
	//		int numberOfThreads = rootThreadGroup.activeCount();
	//		return numberOfThreads > 0;
	//	}

	/**
	 * Outputs running thread names if there are any Returns true if running threads were found
	 *
	 * @return
	 */
	private boolean outputRunningThreads() {
		ThreadGroup rootThreadGroup = getRootThreadGroup();
		int numberOfThreads = rootThreadGroup.activeCount();
		Thread[] threadList = new Thread[numberOfThreads];
		rootThreadGroup.enumerate(threadList, true);

		if (numberOfThreads > 0) {
			LOGGER.info("There are " + numberOfThreads + " threads running:");
			for (Thread th : threadList) {
				LOGGER.info("---------------------------------------------------");
				LOGGER.info("THREAD DUMP:" + th.getName() + " isDaemon=" + th.isDaemon() + " isAlive=" + th.isAlive());
				for (StackTraceElement element : th.getStackTrace()) {
					LOGGER.info(">>>" + element.toString());
				}
				LOGGER.info("---------------------------------------------------");
			}
		}

		return numberOfThreads > 0;
	}

	private void showMainStage() {
		//final AutoUpdateCompletionListener completeListener = this;

		mainStage.setOnShown((WindowEvent event) -> {
			//autoUpdater = new AutoUpdate(BaseConfiguration.getApplicationShortName(), ApplicationConfiguration.getDownloadModifier(BaseConfiguration.getApplicationName()), completeListener);
			//autoUpdater.start();

			I18N i18n = new I18N();

			if (fOpenAutomakerEnv.has3DSupport()) {
				WelcomeToApplicationManager.displayWelcomeIfRequired();
				commsManager.start();
			}
			else {
				BaseLookup.getTaskExecutor().runOnGUIThread(() -> {
					ChoiceLinkDialogBox threeDProblemBox = new ChoiceLinkDialogBox(false);
					threeDProblemBox.setTitle(i18n.t("dialogs.fatalErrorNo3DSupport"));
					threeDProblemBox.setMessage(i18n.t("dialogs.automakerErrorNo3DSupport"));
					threeDProblemBox.addChoiceLink(i18n.t("dialogs.error.okAbortJob"));
					try {
						threeDProblemBox.getUserInput();
					}
					catch (PrinterDisconnectedException ex) {
					}
					LOGGER.error("Closing down due to lack of required 3D support.");
					Platform.exit();
				});
			}

			// Virtual printer check
			//if (fUseVirtualPrinterPreference.get())
			//	RoboxCommsManager.getInstance().addVirtualPrinter(true);

		});
		mainStage.setAlwaysOnTop(false);

		//set Stage boundaries to visible bounds of the main screen
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		mainStage.setX(primaryScreenBounds.getMinX());
		mainStage.setY(primaryScreenBounds.getMinY());
		mainStage.setWidth(primaryScreenBounds.getWidth());
		mainStage.setHeight(primaryScreenBounds.getHeight());

		mainStage.initModality(Modality.WINDOW_MODAL);

		mainStage.show();
	}

	@Override
	public void incomingComms(InterAppRequest interAppRequest) {
		LOGGER.info("Received an InterApp comms request: " + interAppRequest.toString());

		if (interAppRequest instanceof AutoMakerInterAppRequest) {
			AutoMakerInterAppRequest amRequest = (AutoMakerInterAppRequest) interAppRequest;
			switch (amRequest.getCommand()) {
				case LOAD_MESH_INTO_LAYOUT_VIEW:
					String projectName = "Import";
					List<String> modelsToLoad = new ArrayList<>();
					boolean dontGroupModels = false;

					for (InterAppParameter interAppParam : amRequest.getUnencodedParameters()) {
						if (interAppParam.getType() == InterAppParameterType.MODEL_NAME) {
							modelsToLoad.add(interAppParam.getUnencodedParameter());
						}
						else if (interAppParam.getType() == InterAppParameterType.PROJECT_NAME) {
							projectName = interAppParam.getUnencodedParameter();
						}
						else if (interAppParam.getType() == InterAppParameterType.DONT_GROUP_MODELS) {
							switch (interAppParam.getUnencodedParameter()) {
								case "true":
									dontGroupModels = true;
									break;
								default:
									break;
							}
						}
					}
					displayManager.loadModelsIntoNewProject(projectName, modelsToLoad, dontGroupModels);
					break;
				default:
					break;
			}

		}
	}

	private static ThreadGroup getRootThreadGroup() {
		ThreadGroup root = Thread.currentThread().getThreadGroup();
		ThreadGroup parent = root.getParent();
		while (parent != null) {
			root = parent;
			parent = parent.getParent();
		}
		return root;
	}
}
