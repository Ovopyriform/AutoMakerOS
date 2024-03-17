/*
 * Copyright 2015 CEL UK
 */
package org.openautomaker.base.printerControl.model;

import org.openautomaker.base.configuration.fileRepresentation.HeadFile;
import org.openautomaker.base.configuration.fileRepresentation.NozzleHeaterData;
import org.openautomaker.base.printerControl.model.Head;
import org.openautomaker.base.printerControl.model.NozzleHeater;

import javafx.beans.property.FloatProperty;

/**
 *
 * @author tony
 */
public class TestHead extends Head
{

    public TestHead(HeadFile headFile)
    {
        super(headFile);
    }

    @Override
    protected NozzleHeater makeNozzleHeater(NozzleHeaterData nozzleHeaterData)
    {
        return new TestNozzleHeater(nozzleHeaterData.getMaximum_temperature_C(),
                                    nozzleHeaterData.getBeta(),
                                    nozzleHeaterData.getTcal(),
                                    0, 0, 0, 0, "");
    }

    public class TestNozzleHeater extends NozzleHeater
    {

        public TestNozzleHeater(float maximumTemperature,
            float beta,
            float tcal,
            float lastFilamentTemperature,
            int nozzleTemperature,
            int nozzleFirstLayerTargetTemperature,
            int nozzleTargetTemperature,
            String filamentID)
        {
            super(maximumTemperature, beta, tcal, lastFilamentTemperature, nozzleTemperature,
                  nozzleFirstLayerTargetTemperature, nozzleTargetTemperature, filamentID);
        }

        public FloatProperty lastFilamentTemperatureProperty()
        {
            return lastFilamentTemperature;
        }

    }

}
