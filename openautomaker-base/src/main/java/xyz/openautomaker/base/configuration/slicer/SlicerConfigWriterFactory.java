package xyz.openautomaker.base.configuration.slicer;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author Ian
 */
public class SlicerConfigWriterFactory {

	private static Logger LOGGER = LogManager.getLogger();

	public static SlicerConfigWriter getConfigWriter(SlicerType slicerType) {

		SlicerConfigWriter slicerConfigWriter = null;

		try {
			slicerType.getConfigWriterClass().getConstructor(SlicerType.class).newInstance(slicerType);
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SlicerConfigWriter writer = null;

		switch (slicerType) {
			case Cura:
				writer = new CuraConfigWriter(slicerType);
				break;
			default:
				writer = new Cura4ConfigWriter(slicerType);
				break;
		}
		return writer;
	}
}
