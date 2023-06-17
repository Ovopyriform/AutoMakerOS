package celtech.coreUI.controllers.panels.userpreferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import celtech.configuration.UserPreferences;
import celtech.coreUI.controllers.panels.PreferencesInnerPanelController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import xyz.openautomaker.base.ApplicationFeature;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
public class SlicerTypePreference implements PreferencesInnerPanelController.Preference
{
	private static final Logger LOGGER = LogManager.getLogger(SlicerTypePreference.class.getName());

	private final ComboBox<SlicerType> control;
	private final UserPreferences userPreferences;
	private final ObservableList<SlicerType> slicerTypes = FXCollections.observableArrayList();

	private boolean updating = false;
	private final ChangeListener<SlicerType> slicerTypeCtrlChangeListener = (observable, oldValue, newValue) -> {
		if (!updating) {
			updateValueFromControl();
		}
	};
	private final ChangeListener<SlicerType> slicerTypeUPChangeListener = (observable, oldValue, newValue) -> {
		if (!updating) {
			populateControlWithCurrentValue();
		}
	};

	public SlicerTypePreference(UserPreferences userPreferences)
	{
		this.userPreferences = userPreferences;

		control = new ComboBox<>();
		control.getStyleClass().add("cmbCleanCombo");

		for (SlicerType slicerType : SlicerType.values()) {
			slicerTypes.add(slicerType);
		}

		//slicerTypes.add(SlicerType.Cura);
		//slicerTypes.add(SlicerType.Cura4);

		control.setItems(slicerTypes);
		control.setPrefWidth(150);
		control.setMinWidth(control.getPrefWidth());
		control.setCellFactory((ListView<SlicerType> param) -> new SlicerTypeCell());
		control.valueProperty().addListener(slicerTypeCtrlChangeListener);
		userPreferences.getSlicerTypeProperty().addListener(slicerTypeUPChangeListener);
	}

	@Override
	public void updateValueFromControl()
	{
		if (!updating) {
			updating = true; // Prevent recursive calls.
			SlicerType slicerType = control.getValue();
			if(slicerType == null)
			{
				LOGGER.warn("SlicerType from Slicer setting is null. Setting to default Cura");
				slicerType = SlicerType.Cura;
			}
			else if (slicerType == SlicerType.Cura4)
			{
				if(!BaseConfiguration.isApplicationFeatureEnabled(ApplicationFeature.LATEST_CURA_VERSION))
				{
					slicerType = SlicerType.Cura;
				}
			}

			control.setValue(slicerType);
			userPreferences.setSlicerType(slicerType);

			updating = false;
		}
	}

	@Override
	public void populateControlWithCurrentValue()
	{
		SlicerType chosenType = userPreferences.getSlicerType();
		control.setValue(chosenType);
	}

	@Override
	public Control getControl()
	{
		return control;
	}

	@Override
	public String getDescription()
	{
		return OpenAutoMakerEnv.getI18N().t("preferences.slicerType");
	}

	@Override
	public void disableProperty(ObservableValue<Boolean> disableProperty)
	{
		control.disableProperty().unbind();
		control.disableProperty().bind(disableProperty);
	}
}
