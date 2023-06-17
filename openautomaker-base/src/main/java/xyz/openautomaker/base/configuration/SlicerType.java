package xyz.openautomaker.base.configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

import xyz.openautomaker.base.configuration.slicer.Cura4ConfigWriter;
import xyz.openautomaker.base.configuration.slicer.CuraConfigWriter;
import xyz.openautomaker.base.configuration.slicer.SlicerConfigWriter;
import xyz.openautomaker.base.postprocessor.nouveau.Cura4PlusGCodeParser;
import xyz.openautomaker.base.postprocessor.nouveau.CuraGCodeParser;
import xyz.openautomaker.base.postprocessor.nouveau.GCodeParser;

/**
 *
 * @author Ian
 */
public enum SlicerType {

	Cura(1, "Cura", Paths.get("Cura"), CuraConfigWriter.class, CuraGCodeParser.class),
	@JsonEnumDefaultValue
	Cura4(2, "Cura 4", Paths.get("Cura4"), Cura4ConfigWriter.class, Cura4PlusGCodeParser.class),
	Cura5(3, "Cura 5 (Experimental)", Paths.get("Cura5"), Cura4ConfigWriter.class, Cura4PlusGCodeParser.class);

	private final int enumPosition;
	private final String friendlyName;
	private final Path pathModifier;
	private final Class<? extends SlicerConfigWriter> configWriterClass;
	private final Class<? extends GCodeParser> parserClass;

	private SlicerType(int enumPosition, String friendlyName, Path pathModifier, Class<? extends SlicerConfigWriter> configWriterClass, Class<? extends GCodeParser> parserClass) {
		this.enumPosition = enumPosition;
		this.friendlyName = friendlyName;
		this.pathModifier = pathModifier;
		this.configWriterClass = configWriterClass;
		this.parserClass = parserClass;
	}

	/**
	 *
	 * @return
	 */
	public int getEnumPosition() {
		return enumPosition;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public Path getPathModifier() {
		return pathModifier;
	}

	public Class<? extends SlicerConfigWriter> getConfigWriterClass() {
		return configWriterClass;
	}

	public Class<? extends GCodeParser> getParserClass() {
		return parserClass;
	}
	/**
	 *
	 * @param enumPosition
	 * @return
	 */
	public static SlicerType fromEnumPosition(int enumPosition) {
		SlicerType returnVal = null;

		for (SlicerType value : values()) {
			if (value.getEnumPosition() == enumPosition) {
				returnVal = value;
				break;
			}
		}

		return returnVal;
	}

	@Override
	public String toString() {
		return name();
	}
}
