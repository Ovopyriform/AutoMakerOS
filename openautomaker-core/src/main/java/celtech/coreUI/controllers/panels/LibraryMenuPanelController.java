package celtech.coreUI.controllers.panels;

import celtech.configuration.ApplicationConfiguration;
import xyz.openautomaker.base.configuration.RoboxProfile;
import xyz.openautomaker.base.configuration.fileRepresentation.CameraProfile;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

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
		String profileMenuItemName = OpenAutoMakerEnv.getI18N().t(profileDetails.innerPanel.getMenuTitle());
		panelMenu.selectItemOfName(profileMenuItemName);
		profileDetailsController.setAndSelectPrintProfile(roboxProfile);
	}

	public void showAndSelectCameraProfile(CameraProfile profile) {
		String cameraProfileMenuItemName = OpenAutoMakerEnv.getI18N().t(cameraProfileDetails.innerPanel.getMenuTitle());
		panelMenu.selectItemOfName(cameraProfileMenuItemName);
		cameraProfileDetailsController.setAndSelectCameraProfile(profile);
	}
}
