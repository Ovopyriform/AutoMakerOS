/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.coreUI.components;

import java.io.IOException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.services.ControllableService;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.DisplayManager;
import celtech.coreUI.controllers.ProgressDialogController;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author ianhudson
 */
public class ProgressDialog {

	private static final Logger LOGGER = LogManager.getLogger(ProgressDialog.class.getName());
	private Stage dialogStage = null;
	private ProgressDialogController dialogController = null;
	private StackPane dialogBoxContainer = null;

	/**
	 *
	 */
	public ProgressDialog() {
		setupDialog();
	}

	/**
	 *
	 * @param service
	 */
	public ProgressDialog(ControllableService service) {
		setupDialog();
		dialogController.configure(service, dialogStage);
	}

	/**
	 *
	 * @param service
	 */
	public void associateControllableService(ControllableService service) {
		dialogController.configure(service, dialogStage);
	}

	private void setupDialog() {
		dialogStage = new Stage(StageStyle.TRANSPARENT);
		URL dialogFXMLURL = ProgressDialog.class.getResource(ApplicationConfiguration.fxmlResourcePath + "ProgressDialog.fxml");
		FXMLLoader dialogLoader = new FXMLLoader(dialogFXMLURL);
		try {
			dialogBoxContainer = (StackPane) dialogLoader.load();
			dialogController = (ProgressDialogController) dialogLoader.getController();

			Scene dialogScene = new Scene(dialogBoxContainer, Color.TRANSPARENT);
			dialogScene.getStylesheets().add(ApplicationConfiguration.getMainCSSFile());
			dialogStage.setScene(dialogScene);
			dialogStage.initOwner(DisplayManager.getMainStage());
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.toFront();
		}
		catch (IOException ex) {
			LOGGER.error("Couldn't load dialog box FXML", ex);
		}
	}

	/**
	 *
	 * @param eventType
	 * @param eventHandler
	 */
	public void addKeyHandler(EventType<KeyEvent> eventType, EventHandler<KeyEvent> eventHandler) {
		dialogBoxContainer.addEventHandler(eventType, eventHandler);
	}

	/**
	 *
	 * @param eventType
	 * @param eventHandler
	 */
	public void removeKeyHandler(EventType<KeyEvent> eventType, EventHandler<KeyEvent> eventHandler) {
		dialogBoxContainer.removeEventHandler(eventType, eventHandler);
	}
}
