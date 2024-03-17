package celtech.coreUI.controllers.panels;

import org.openautomaker.base.configuration.RoboxProfile;
import org.openautomaker.base.configuration.fileRepresentation.CameraProfile;
import org.openautomaker.environment.OpenAutomakerEnv;

import celtech.configuration.ApplicationConfiguration;

/**
 *
 * @author Ian
 */
public class LibraryMenuPanelController extends MenuPanelController {

	private InnerPanelDetails cameraProfileDetails = null;
	private CameraProfilesPanelController cameraProfileDetailsController = null;

	public LibraryMenuPanelController() {
		paneli18Name = "libraryMenu.title";
	}

	@Override
	protected void setupInnerPanels() {
		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "filamentLibraryPanel.fxml",
				new FilamentLibraryPanelController());

		profileDetailsController = new ProfileLibraryPanelController();
		profileDetails = loadInnerPanel(
				ApplicationConfiguration.fxmlUtilityPanelResourcePath + "profileDetails.fxml",
				profileDetailsController);

		cameraProfileDetailsController = new CameraProfilesPanelController();
		cameraProfileDetails = loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "cameraProfilesPanel.fxml",
				cameraProfileDetailsController);
	}

	public void showAndSelectPrintProfile(RoboxProfile roboxProfile) {
		String profileMenuItemName = OpenAutomakerEnv.getI18N().t(profileDetails.innerPanel.getMenuTitle());
		panelMenu.selectItemOfName(profileMenuItemName);
		profileDetailsController.setAndSelectPrintProfile(roboxProfile);
	}

	public void showAndSelectCameraProfile(CameraProfile profile) {
		String cameraProfileMenuItemName = OpenAutomakerEnv.getI18N().t(cameraProfileDetails.innerPanel.getMenuTitle());
		panelMenu.selectItemOfName(cameraProfileMenuItemName);
		cameraProfileDetailsController.setAndSelectCameraProfile(profile);
	}
}
