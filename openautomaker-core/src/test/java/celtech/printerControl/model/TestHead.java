/*
 * Copyright 2015 CEL UK
 */
package celtech.printerControl.model;

import javafx.beans.property.FloatProperty;
import xyz.openautomaker.base.configuration.fileRepresentation.HeadFile;
import xyz.openautomaker.base.configuration.fileRepresentation.NozzleHeaterData;
import xyz.openautomaker.base.printerControl.model.Head;
import xyz.openautomaker.base.printerControl.model.NozzleHeater;

/**
 *
 * @author tony
 */
public class TestHead extends Head {

	public TestHead(HeadFile headFile) {
		super(headFile);
	}

	@Override
	protected NozzleHeater makeNozzleHeater(NozzleHeaterData nozzleHeaterData) {
		return new TestNozzleHeater(nozzleHeaterData.getMaximum_temperature_C(),
				nozzleHeaterData.getBeta(),
				nozzleHeaterData.getTcal(),
				0, 0, 0, 0, "");
	}

	public class TestNozzleHeater extends NozzleHeater {

		public TestNozzleHeater(float maximumTemperature,
				float beta,
				float tcal,
				float lastFilamentTemperature,
				int nozzleTemperature,
				int nozzleFirstLayerTargetTemperature,
				int nozzleTargetTemperature,
				String filamentID) {
			super(maximumTemperature, beta, tcal, lastFilamentTemperature, nozzleTemperature,
					nozzleFirstLayerTargetTemperature, nozzleTargetTemperature, filamentID);
		}

		@Override
		public FloatProperty lastFilamentTemperatureProperty() {
			return lastFilamentTemperature;
		}

	}

}
