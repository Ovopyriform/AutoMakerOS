package celtech.configuration;

import xyz.openautomaker.base.configuration.SlicerType;

/**
 *
 * @author Ian
 */
public enum CustomSlicerType {
	Default(null),
	Cura(SlicerType.Cura),
	Cura4(SlicerType.Cura4),
	Cura5(SlicerType.Cura5);

	private final SlicerType slicerType;

	private CustomSlicerType(SlicerType slicerType) {
		this.slicerType = slicerType;
	}

	public static CustomSlicerType customTypefromSettings(SlicerType slicerType) {
		CustomSlicerType customSlicerType = CustomSlicerType.valueOf(slicerType.name());

		return customSlicerType;
	}

	public SlicerType getSlicerType() {
		return slicerType;
	}
}
