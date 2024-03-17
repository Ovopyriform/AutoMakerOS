package org.openautomaker.base.configuration.slicer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.environment.Slicer;

/**
 *
 * @author Ian
 */
public class SlicerConfigWriterFactory {

	private static Logger LOGGER = LogManager.getLogger();

	public static SlicerConfigWriter getConfigWriter(Slicer slicerType) {
		SlicerConfigWriter writer = null;

		switch (slicerType) {
			case CURA:
				writer = new CuraConfigWriter(slicerType);
				break;
			default:
				writer = new Cura4ConfigWriter(slicerType);
				break;
		}
		return writer;
	}
}
