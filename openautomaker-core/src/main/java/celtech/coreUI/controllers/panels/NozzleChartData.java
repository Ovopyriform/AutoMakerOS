package celtech.coreUI.controllers.panels;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.printerControl.model.HeaterMode;
import xyz.openautomaker.environment.OpenAutoMakerEnv;

/**
 *
 * @author Ian
 */
public class NozzleChartData {

	private final XYChart.Series<Number, Number> nozzleTemperatureData;
	private final LineChart.Series<Number, Number> nozzleTargetTemperatureSeries = new LineChart.Series<>();

	private final LineChart.Data<Number, Number> nozzleTargetPoint = new LineChart.Data<>(
			BaseConfiguration.NUMBER_OF_TEMPERATURE_POINTS_TO_KEEP - 5, 0);
	private final ReadOnlyIntegerProperty nozzleTargetTemperatureProperty;
	private final ReadOnlyIntegerProperty nozzleFirstLayerTargetTemperatureProperty;
	private final ReadOnlyIntegerProperty nozzleTemperatureProperty;
	private final ReadOnlyObjectProperty<HeaterMode> nozzleHeaterModeProperty;
	private final String degreesC;

	ChangeListener<HeaterMode> nozzleHeaterModeListener = (ObservableValue<? extends HeaterMode> observable, HeaterMode oldValue, HeaterMode newValue) -> {
		updateNozzleTargetPoint();
	};

	ChangeListener<Number> nozzleTargetTemperatureListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
		updateNozzleTargetPoint();
	};

	ChangeListener<Number> nozzleFirstLayerTargetTemperatureListener = (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
		updateNozzleTargetPoint();
	};

	public NozzleChartData(int nozzleNumber, XYChart.Series<Number, Number> nozzleTemperatureData,
			ReadOnlyObjectProperty<HeaterMode> nozzleHeaterModeProperty,
			ReadOnlyIntegerProperty nozzleTargetTemperatureProperty,
			ReadOnlyIntegerProperty nozzleFirstLayerTargetTemperatureProperty,
			ReadOnlyIntegerProperty nozzleTemperatureProperty) {
		this.nozzleTemperatureData = nozzleTemperatureData;

		this.nozzleHeaterModeProperty = nozzleHeaterModeProperty;
		nozzleHeaterModeProperty.addListener(nozzleHeaterModeListener);

		this.nozzleTargetTemperatureProperty = nozzleTargetTemperatureProperty;
		nozzleTargetTemperatureProperty.addListener(nozzleTargetTemperatureListener);

		this.nozzleFirstLayerTargetTemperatureProperty = nozzleFirstLayerTargetTemperatureProperty;
		nozzleFirstLayerTargetTemperatureProperty.addListener(
				nozzleFirstLayerTargetTemperatureListener);

		this.nozzleTemperatureProperty = nozzleTemperatureProperty;

		nozzleTargetTemperatureSeries.getData().add(nozzleTargetPoint);

		degreesC = OpenAutoMakerEnv.getI18N().t("misc.degreesC");

		updateNozzleTargetPoint();
	}

	public void destroy() {
		if (this.nozzleHeaterModeProperty != null) {
			nozzleHeaterModeProperty.removeListener(nozzleHeaterModeListener);
		}

		if (this.nozzleTargetTemperatureProperty != null) {
			this.nozzleTargetTemperatureProperty.removeListener(nozzleTargetTemperatureListener);
		}

		if (this.nozzleFirstLayerTargetTemperatureProperty != null) {
			this.nozzleFirstLayerTargetTemperatureProperty.removeListener(
					nozzleFirstLayerTargetTemperatureListener);
		}
	}

	private void updateNozzleTargetPoint() {
		if (nozzleHeaterModeProperty == null || nozzleTargetTemperatureProperty == null
				|| nozzleFirstLayerTargetTemperatureProperty == null) {
			return;
		}
		if (nozzleHeaterModeProperty.get() == HeaterMode.OFF) {
			nozzleTargetPoint.setYValue(0);
		}
		else if (nozzleHeaterModeProperty.get() == HeaterMode.FIRST_LAYER) {
			nozzleTargetPoint.setYValue(nozzleFirstLayerTargetTemperatureProperty.get());
		}
		else if (nozzleHeaterModeProperty.get() == HeaterMode.FILAMENT_EJECT) {
			nozzleTargetPoint.setYValue(140.0);
		}
		else {
			nozzleTargetPoint.setYValue(nozzleTargetTemperatureProperty.get());
		}
	}

	public LineChart.Series<Number, Number> getTargetTemperatureSeries() {
		return nozzleTargetTemperatureSeries;
	}

	public LineChart.Series<Number, Number> getNozzleTemperatureSeries() {
		return nozzleTemperatureData;
	}
}
