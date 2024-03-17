/*
 * Copyright 2015 CEL UK
 */
package celtech.coreUI.controllers.panels.userpreferences;

import java.util.ArrayList;
import java.util.List;

import org.openautomaker.environment.preference.DetectLoadedFilamentPreference;
import org.openautomaker.environment.preference.FirstUsePreference;
import org.openautomaker.environment.preference.GBPToLocalMultiplierPreference;
import org.openautomaker.environment.preference.SafetyFeaturesPreference;
import org.openautomaker.environment.preference.SearchForRemoteCamerasPreference;
import org.openautomaker.environment.preference.ShowGCodePreviewPreference;
import org.openautomaker.environment.preference.ShowTooltipsPreference;
import org.openautomaker.environment.preference.SplitLoosePartsOnLoadPreference;
import org.openautomaker.environment.preference.advanced.ShowAdjustmentsPreference;
import org.openautomaker.environment.preference.advanced.ShowDiagnosticsPreference;
import org.openautomaker.environment.preference.advanced.ShowGCodeConsolePreference;
import org.openautomaker.environment.preference.advanced.ShowSnapshotPreference;
import org.openautomaker.environment.preference.virtual_printer.VirtualPrinterEnabledPreference;
import org.openautomaker.ui.utils.FXProperty;

import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController.Preference;
import javafx.beans.property.BooleanProperty;

/**
 * Preferences creates collections of the Preference class.
 *
 * @author tony
 */
public class Preferences {

	public static List<PreferencesInnerPanelController.Preference> createPrintingPreferences() {
		List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

		Preference slicerTypePref = new SlicerTypePreferenceController();

		Preference safetyFeaturesOnPref = new TickBoxPreference(FXProperty.bind(new SafetyFeaturesPreference()), "preferences.safetyFeaturesOn");

		Preference detectFilamentLoadedPref = new TickBoxPreference(FXProperty.bind(new DetectLoadedFilamentPreference()), "preferences.detectLoadedFilament");

		preferences.add(slicerTypePref);
		preferences.add(safetyFeaturesOnPref);
		preferences.add(detectFilamentLoadedPref);

		return preferences;
	}

	public static List<PreferencesInnerPanelController.Preference> createEnvironmentPreferences() {
		List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

		Preference languagePref = new LanguagePreferenceController();
		Preference showTooltipsPref = new TickBoxPreference(FXProperty.bind(new ShowTooltipsPreference()), "preferences.showTooltips");
		Preference logLevelPref = new LogLevelPreferenceController();
		Preference firstUsePref = new TickBoxPreference(FXProperty.bind(new FirstUsePreference()), "preferences.firstUse");

		Preference currencySymbolPref = new CurrencySymbolPreferenceController();
		Preference currencyGBPToLocalMultiplierPref = new FloatingPointPreference(FXProperty.bind(new GBPToLocalMultiplierPreference()),
				2, 7, false, "preferences.currencyGBPToLocalMultiplier");

		Preference loosePartSplitPref = new TickBoxPreference(FXProperty.bind(new SplitLoosePartsOnLoadPreference()), "preferences.loosePartSplit");

		Preference autoGCodePreviewPref = new TickBoxPreference(FXProperty.bind(new ShowGCodePreviewPreference()), "preferences.autoGCodePreview");

		Preference searchForRemoteCamerasPref = new TickBoxPreference(FXProperty.bind(new SearchForRemoteCamerasPreference()), "preferences.searchForRemoteCameras");

		preferences.add(firstUsePref);
		preferences.add(languagePref);
		preferences.add(logLevelPref);
		preferences.add(currencySymbolPref);
		preferences.add(currencyGBPToLocalMultiplierPref);
		preferences.add(loosePartSplitPref);
		preferences.add(autoGCodePreviewPref);
		preferences.add(searchForRemoteCamerasPref);

		return preferences;
	}

	public static List<PreferencesInnerPanelController.Preference> createAdvancedPreferences() {
		List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

		AdvancedModePreferenceController advancedModePref = new AdvancedModePreferenceController();

		TickBoxPreference showDiagnosticsPref = new TickBoxPreference(FXProperty.bind(new ShowDiagnosticsPreference()), "preferences.showDiagnostics");
		showDiagnosticsPref.disableProperty(advancedModePref.getSelectedProperty().not());

		TickBoxPreference showGCodePref = new TickBoxPreference(FXProperty.bind(new ShowGCodeConsolePreference()), "preferences.showGCode");
		showGCodePref.disableProperty(advancedModePref.getSelectedProperty().not());

		TickBoxPreference showAdjustmentsPref = new TickBoxPreference(FXProperty.bind(new ShowAdjustmentsPreference()), "preferences.showAdjustments");
		showAdjustmentsPref.disableProperty(advancedModePref.getSelectedProperty().not());

		TickBoxPreference showSnapshotPref = new TickBoxPreference(FXProperty.bind(new ShowSnapshotPreference()), "preferences.showSnapshot");
		showSnapshotPref.disableProperty(advancedModePref.getSelectedProperty().not());

		preferences.add(advancedModePref);
		preferences.add(showDiagnosticsPref);
		preferences.add(showGCodePref);
		preferences.add(showAdjustmentsPref);
		preferences.add(showSnapshotPref);

		return preferences;
	}

	public static List<PreferencesInnerPanelController.Preference> createCustomPrinterPreferences() {
		List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

		BooleanProperty customPrinterEnabled = FXProperty.bind(new VirtualPrinterEnabledPreference());
		Preference enableCustomPrinterPref = new TickBoxPreference(customPrinterEnabled, "preferences.customPrinterEnabled");
		Preference customPrinterTypePref = new CustomPrinterTypePreferenceController();
		Preference customPrinterHeadPref = new CustomPrinterHeadPreferenceController();

		//BooleanProperty windows32Bit = new SimpleBooleanProperty(BaseConfiguration.isWindows32Bit());
		//enableCustomPrinterPref.disableProperty(windows32Bit);

		preferences.add(enableCustomPrinterPref);
		preferences.add(customPrinterTypePref);
		preferences.add(customPrinterHeadPref);

		return preferences;
	}

	//TODO: Root Only
	public static List<PreferencesInnerPanelController.Preference> createRootPreferences() {
		List<PreferencesInnerPanelController.Preference> preferences = new ArrayList<>();

		//        Preference
		//        Preference slicerTypePref = new SlicerTypePreference(userPreferences);
		//
		//        Preference safetyFeaturesOnPref = new TickBoxPreference(userPreferences.
		//                safetyFeaturesOnProperty(), "preferences.safetyFeaturesOn");
		//
		//        Preference detectFilamentLoadedPref = new TickBoxPreference(userPreferences.
		//                detectLoadedFilamentProperty(), "preferences.detectLoadedFilament");
		//
		//        preferences.add(slicerTypePref);
		//        preferences.add(safetyFeaturesOnPref);
		//        preferences.add(detectFilamentLoadedPref);

		return preferences;
	}
}
