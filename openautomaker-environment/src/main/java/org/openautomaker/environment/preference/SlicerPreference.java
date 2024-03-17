package org.openautomaker.environment.preference;

import java.util.List;

import org.openautomaker.environment.Slicer;

/**
 * Preference for the selected Slicer
 */
public class SlicerPreference extends AbsPreference<Slicer> {

	public SlicerPreference() {
		super();
	}

	@Override
	public List<Slicer> values() {
		return List.of(Slicer.values());
	}

	@Override
	public Slicer get() {
		return Slicer.valueOf(getUserNode().get(getKey(), Slicer.CURA_5.name()));
	}

	@Override
	public void set(Slicer slicer) {
		getUserNode().put(getKey(), slicer.name());
	}

}
