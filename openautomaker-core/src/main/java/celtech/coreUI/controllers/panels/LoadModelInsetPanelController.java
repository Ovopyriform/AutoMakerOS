package celtech.coreUI.controllers.panels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.Lookup;
import celtech.appManager.ApplicationMode;
import celtech.appManager.ApplicationStatus;
import celtech.appManager.ProjectMode;
import celtech.configuration.ApplicationConfiguration;
import celtech.configuration.DirectoryMemoryProperty;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.components.InsetPanelMenu;
import celtech.coreUI.components.InsetPanelMenuItem;
import celtech.coreUI.visualisation.ModelLoader;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import netscape.javascript.JSObject;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.utils.SystemUtils;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
public class LoadModelInsetPanelController implements Initializable {

	private final FileChooser modelFileChooser = new FileChooser();
	private DisplayManager displayManager = null;
	private static final Logger LOGGER = LogManager.getLogger(
			LoadModelInsetPanelController.class.getName());
	private ModelLoader modelLoader = new ModelLoader();

	@FXML
	private VBox container;

	@FXML
	private VBox webContentContainer;

	@FXML
	private InsetPanelMenu menu;

	@FXML
	void cancelPressed(ActionEvent event) {
		ApplicationStatus.getInstance().returnToLastMode();
	}

	@FXML
	void addToProjectPressed(ActionEvent event) {
		Platform.runLater(() -> {
			ListIterator iterator = modelFileChooser.getExtensionFilters().listIterator();

			while (iterator.hasNext()) {
				iterator.next();
				iterator.remove();
			}

			String descriptionOfFile = OpenAutoMakerEnv.getI18N().t("dialogs.meshFileChooserDescription");

			modelFileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter(descriptionOfFile,
							ApplicationConfiguration.getSupportedFileExtensionWildcards(
									ProjectMode.MESH)));

			modelFileChooser.setInitialDirectory(ApplicationConfiguration.getLastDirectoryFile(DirectoryMemoryProperty.LAST_MODEL_DIRECTORY));

			List<File> files;

			files = modelFileChooser.showOpenMultipleDialog(DisplayManager.getMainStage());

			if (files != null && !files.isEmpty()) {
				ApplicationConfiguration.setLastDirectory(
						DirectoryMemoryProperty.LAST_MODEL_DIRECTORY,
						files.get(0).getParentFile().getAbsolutePath());
				modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(), files,
						true, null, false);
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		displayManager = DisplayManager.getInstance();

		menu.setTitle(OpenAutoMakerEnv.getI18N().t("loadModel.menuTitle"));

		InsetPanelMenuItem myComputerItem = new InsetPanelMenuItem();
		myComputerItem.setTitle(OpenAutoMakerEnv.getI18N().t("loadModel.myComputer"));

		InsetPanelMenuItem myMiniFactoryItem = new InsetPanelMenuItem();
		myMiniFactoryItem.setTitle(OpenAutoMakerEnv.getI18N().t("loadModel.myMiniFactory"));

		menu.addMenuItem(myComputerItem);
		menu.addMenuItem(myMiniFactoryItem);

		modelFileChooser.setTitle(OpenAutoMakerEnv.getI18N().t("dialogs.modelFileChooser"));
		modelFileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(OpenAutoMakerEnv.getI18N().t("dialogs.modelFileChooserDescription"),
						ApplicationConfiguration.getSupportedFileExtensionWildcards(
								ProjectMode.NONE)));

		ApplicationStatus.getInstance().modeProperty().addListener(
				(ObservableValue<? extends ApplicationMode> observable, ApplicationMode oldValue, ApplicationMode newValue) -> {
					if (newValue == ApplicationMode.ADD_MODEL && oldValue != newValue) {
						webContentContainer.getChildren().clear();

						WebView webView = new WebView();
						VBox.setVgrow(webView, Priority.ALWAYS);

						final WebEngine webEngine = webView.getEngine();

						webEngine.getLoadWorker().stateProperty().addListener(
								new ChangeListener<State>() {
									@Override
									public void changed(ObservableValue<? extends State> ov,
											State oldState, State newState) {
										switch (newState) {
											case RUNNING:
												break;
											case SUCCEEDED:
												JSObject win = (JSObject) webEngine.executeScript("window");
												win.setMember("automaker", new WebCallback());
												break;
										}
									}
								});
						webContentContainer.getChildren().addAll(webView);
						webEngine.load("http://cel-robox.myminifactory.com");
					}
				});
	}

	public class WebCallback {

		public void downloadFile(String fileURL) {
			LOGGER.debug("Got download URL of " + fileURL);

			String tempID = SystemUtils.generate16DigitID();
			try {
				URL downloadURL = new URL(fileURL);

				String extension = FilenameUtils.getExtension(fileURL);
				final String tempFilename = BaseConfiguration.getApplicationStorageDirectory()
						+ File.separator + tempID + "." + extension;

				URLConnection urlConn = downloadURL.openConnection();

				InputStream webInputStream = urlConn.getInputStream();

				if (extension.equalsIgnoreCase("stl")) {
					LOGGER.debug("Got stl file from My Mini Factory");
					final String targetname = BaseConfiguration.getUserStorageDirectory()
							+ File.separator + FilenameUtils.getBaseName(fileURL);
					writeStreamToFile(webInputStream, targetname);
				}
				else if (extension.equalsIgnoreCase("zip")) {
					LOGGER.debug("Got zip file from My Mini Factory");
					writeStreamToFile(webInputStream, tempFilename);
					ZipFile zipFile = new ZipFile(tempFilename);
					try {
						final Enumeration<? extends ZipEntry> entries = zipFile.entries();
						final List<File> filesToLoad = new ArrayList<>();
						while (entries.hasMoreElements()) {
							final ZipEntry entry = entries.nextElement();
							final String tempTargetname = BaseConfiguration.getUserStorageDirectory()
									+ File.separator + entry.getName();
							writeStreamToFile(zipFile.getInputStream(entry), tempTargetname);
							filesToLoad.add(new File(tempTargetname));
						}
						modelLoader.loadExternalModels(Lookup.getSelectedProjectProperty().get(),
								filesToLoad, null);
					}
					finally {
						zipFile.close();
					}
				}
				else if (extension.equalsIgnoreCase("rar")) {
					LOGGER.debug("Got rar file from My Mini Factory");
				}

				webInputStream.close();

			}
			catch (IOException ex) {
				LOGGER.error("Failed to download My Mini Factory file :" + fileURL);
			}
		}
	}

	private void writeStreamToFile(InputStream is, String localFilename) throws IOException {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(localFilename); //open outputstream to local file

			byte[] buffer = new byte[4096]; //declare 4KB buffer
			int len;

			//while we have availble data, continue downloading and storing to local file
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		}
		finally {
			try {
				if (is != null) {
					is.close();
				}
			}
			finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

}
