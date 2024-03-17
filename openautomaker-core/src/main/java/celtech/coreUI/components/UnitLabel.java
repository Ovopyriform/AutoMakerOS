package celtech.coreUI.components;

import org.openautomaker.environment.preference.ShowMetricUnitsPreference;

import celtech.configuration.units.UnitType;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

/**
 *
 * @author Ian
 */
public class UnitLabel extends Label {

	private final StringProperty unitType = new SimpleStringProperty(UnitType.NONE.name());
	private UnitType units = UnitType.NONE;

	public UnitType getUnits() {
		return units;
	}

	public void setUnits(UnitType units) {
		this.units = units;
		unitType.set(units.name());
		updateDisplay();
	}

	public String getUnitType() {
		return units.name();
	}

	public void setUnitType(String value) {
		units = UnitType.valueOf(value);
		unitType.set(value);
		updateDisplay();
	}

	public ReadOnlyStringProperty unitTypeProperty() {
		return unitType;
	}

	private void updateDisplay() {
		if (!new ShowMetricUnitsPreference().get()) {
			this.setText(units.getImperialSymbol());
			return;
		}

		this.setText(units.getMetricSymbol());
	}
}
