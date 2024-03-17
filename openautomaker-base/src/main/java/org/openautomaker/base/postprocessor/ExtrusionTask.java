package org.openautomaker.base.postprocessor;

import org.openautomaker.environment.Slicer;

/**
 *
 * @author Ian
 */
public enum ExtrusionTask {

	Skirt("skirt", "brim", "TYPE:SKIRT"),
	Perimeter("perimeter", "perimeter", "TYPE:WALL-INNER"),
	ExternalPerimeter("externalPerimeter", "N/A-not-included-in-slic3r", "TYPE:WALL-OUTER"),
	Skin("skin", "N/A-not-included-in-slic3r", "TYPE:SKIN"),
	Support("support", "support material", "TYPE:SUPPORT"),
	Support_Interface("supportInterface", "support material interface", "N/A-not-included-in-cura"),
	Fill("fill", "fill", "TYPE:FILL");

	private final String genericLabelInGCode;
	private final String curaLabelInGCode;

	private ExtrusionTask(String genericLabelInGCode, String slic3rLabelInGCode, String curaLabelInGCode) {
		this.genericLabelInGCode = genericLabelInGCode;
		this.curaLabelInGCode = curaLabelInGCode;
	}

	/**
	 *
	 * @return
	 */
	public String getGenericLabel() {
		return genericLabelInGCode;
	}

	/**
	 *
	 * @param slicerType
	 * @return
	 */
	public String getSlicerSpecificLabel(Slicer slicerType) {
		return curaLabelInGCode;
	}

	/**
	 *
	 * @param slicerType     - if null then the generic string is searched for
	 * @param stringToSearch
	 * @return
	 */
	public static ExtrusionTask lookupExtrusionTaskFromComment(Slicer slicerType, String stringToSearch) {
		ExtrusionTask foundExtrusionTask = null;

		if (stringToSearch != null) {
			for (ExtrusionTask task : ExtrusionTask.values()) {
				String labelToSearchFor = null;

				if (slicerType != null) {
					labelToSearchFor = task.getSlicerSpecificLabel(slicerType);
				}
				else {
					labelToSearchFor = task.getGenericLabel();
				}

				if (stringToSearch.contains(labelToSearchFor)) {
					foundExtrusionTask = task;
					break;
				}
			}
		}

		return foundExtrusionTask;
	}
}
