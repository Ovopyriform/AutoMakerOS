package celtech.coreUI.controllers.panels;

import celtech.configuration.ApplicationConfiguration;
import celtech.coreUI.controllers.panels.userpreferences.Preferences;
import celtech.coreUI.controllers.utilityPanels.HeadEEPROMController;

//TODO: Look at this binding of FXML to classes
public class ExtrasMenuPanelController extends MenuPanelController {

	public ExtrasMenuPanelController() {
		paneli18Name = "extrasMenu.title";
	}

	/**
	 * Define the inner panels to be offered in the main menu. For the future this is configuration information that could be e.g. stored in XML or in a plugin.
	 */
	@Override
	protected void setupInnerPanels() {
		loadInnerPanel(
				ApplicationConfiguration.fxmlUtilityPanelResourcePath + "headEEPROM.fxml",
				new HeadEEPROMController());

		//UserPreferences userPreferences = Lookup.getUserPreferences();
		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "preferencesPanel.fxml",
				new PreferencesInnerPanelController("preferences.environment",
						Preferences.createEnvironmentPreferences()));
		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "preferencesPanel.fxml",
				new PreferencesInnerPanelController("preferences.printing",
						Preferences.createPrintingPreferences()));
		//        loadInnerPanel(
		//                ApplicationConfiguration.fxmlPanelResourcePath + "preferencesPanel.fxml",
		//                new PreferencesInnerPanelController("preferences.timelapse",
		//                        Preferences.createTimelapsePreferences(userPreferences)));

		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "rootScanner.fxml",
				new RootScannerPanelController());

		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "MaintenanceInsetPanel.fxml",
				new MaintenanceInsetPanelController());

		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "preferencesPanel.fxml",
				new PreferencesInnerPanelController("preferences.advanced",
						Preferences.createAdvancedPreferences()));
		loadInnerPanel(
				ApplicationConfiguration.fxmlPanelResourcePath + "preferencesPanel.fxml",
				new PreferencesInnerPanelController("preferences.customPrinter",
						Preferences.createCustomPrinterPreferences()));
	}
}
