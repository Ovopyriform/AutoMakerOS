package celtech.coreUI.visualisation.shapes;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ArrayChangeListener;
import javafx.collections.ObservableFloatArray;

/**
 * Catmull Clark subdivision surface polygon mesh
 */
public class SubdivisionMesh extends PolygonMesh {
	private final PolygonMesh originalMesh;
	private int subdivisionLevel;
	private BoundaryMode boundaryMode;
	private MapBorderMode mapBorderMode;
	private final List<SymbolicPolygonMesh> symbolicMeshes;

	private boolean pointValuesDirty;
	private boolean meshDirty;
	private boolean subdivisionLevelDirty;

	/**
	 * Describes whether the edges and points at the boundary are treated as creases
	 */
	public enum BoundaryMode {
		/**
		 * Only edges at the boundary are treated as creases
		 */
		CREASE_EDGES,
		/**
		 * Edges and points at the boundary are treated as creases
		 */
		CREASE_ALL
	}

	/**
	 * Describes how the new texture coordinate for the control point is defined
	 */
	public enum MapBorderMode {
		/**
		 * Jeeps the same uvs for all control points
		 */
		NOT_SMOOTH,
		/**
		 * Smooths uvs of points at corners
		 */
		SMOOTH_INTERNAL,
		/**
		 * Smooths uvs of points at boundaries and original control points (and creases [in the future when creases are defined])
		 */
		SMOOTH_ALL
	}

	/**
	 *
	 * @param originalMesh
	 * @param subdivisionLevel
	 * @param boundaryMode
	 * @param mapBorderMode
	 */
	public SubdivisionMesh(PolygonMesh originalMesh, int subdivisionLevel, BoundaryMode boundaryMode, MapBorderMode mapBorderMode) {
		this.originalMesh = originalMesh;
		setSubdivisionLevelForced(subdivisionLevel);
		setBoundaryModeForced(boundaryMode);
		setMapBorderModeForced(mapBorderMode);

		symbolicMeshes = new ArrayList<>(4); // the polymesh is usually subdivided up to 3 times

		originalMesh.getPoints().addListener(new ArrayChangeListener<ObservableFloatArray>() {
			@Override
			public void onChanged(ObservableFloatArray observableArray, boolean sizeChanged, int from, int to) {
				if (sizeChanged) {
					meshDirty = true;
				}
				else {
					pointValuesDirty = true;
				}
			}
		});
		originalMesh.getTexCoords().addListener(new ArrayChangeListener<ObservableFloatArray>() {
			@Override
			public void onChanged(ObservableFloatArray observableArray, boolean sizeChanged, int from, int to) {
				meshDirty = true;
			}
		});
	}

	/**
	 * Updates the variables of the underlying polygon mesh. It only updates the fields that need to be updated.
	 */
	public void update() {
		if (meshDirty) {
			symbolicMeshes.clear();
			symbolicMeshes.add(new SymbolicPolygonMesh(originalMesh));
			pointValuesDirty = true;
			subdivisionLevelDirty = true;
		}

		while (subdivisionLevel >= symbolicMeshes.size()) {
			symbolicMeshes.add(SymbolicSubdivisionBuilder.subdivide(symbolicMeshes.get(symbolicMeshes.size() - 1), boundaryMode, mapBorderMode));
			pointValuesDirty = true;
			subdivisionLevelDirty = true;
		}

		if (pointValuesDirty) {
			for (int i = 0; i <= subdivisionLevel; i++) {
				SymbolicPolygonMesh symbolicMesh = symbolicMeshes.get(i);
				symbolicMesh.points.update();
			}
		}

		if (pointValuesDirty || subdivisionLevelDirty) {
			getPoints().setAll(symbolicMeshes.get(subdivisionLevel).points.data);
		}

		if (subdivisionLevelDirty) {
			faces = symbolicMeshes.get(subdivisionLevel).faces;
			numEdgesInFaces = -1;
			getFaceSmoothingGroups().setAll(symbolicMeshes.get(subdivisionLevel).faceSmoothingGroups);
			getTexCoords().setAll(symbolicMeshes.get(subdivisionLevel).texCoords);
		}

		meshDirty = false;
		pointValuesDirty = false;
		subdivisionLevelDirty = false;
	}

	private void setSubdivisionLevelForced(int subdivisionLevel) {
		this.subdivisionLevel = subdivisionLevel;
		subdivisionLevelDirty = true;
	}

	private void setBoundaryModeForced(SubdivisionMesh.BoundaryMode boundaryMode) {
		this.boundaryMode = boundaryMode;
		meshDirty = true;
	}

	private void setMapBorderModeForced(SubdivisionMesh.MapBorderMode mapBorderMode) {
		this.mapBorderMode = mapBorderMode;
		meshDirty = true;
	}

	/**
	 *
	 * @return
	 */
	public PolygonMesh getOriginalMesh() {
		return originalMesh;
	}

	/**
	 *
	 * @return
	 */
	public int getSubdivisionLevel() {
		return subdivisionLevel;
	}

	/**
	 *
	 * @param subdivisionLevel
	 */
	public void setSubdivisionLevel(int subdivisionLevel) {
		if (subdivisionLevel != this.subdivisionLevel) {
			setSubdivisionLevelForced(subdivisionLevel);
		}
	}

	/**
	 *
	 * @return
	 */
	public SubdivisionMesh.BoundaryMode getBoundaryMode() {
		return boundaryMode;
	}

	/**
	 *
	 * @param boundaryMode
	 */
	public void setBoundaryMode(SubdivisionMesh.BoundaryMode boundaryMode) {
		if (boundaryMode != this.boundaryMode) {
			setBoundaryModeForced(boundaryMode);
		}
	}

	/**
	 *
	 * @return
	 */
	public SubdivisionMesh.MapBorderMode getMapBorderMode() {
		return mapBorderMode;
	}

	/**
	 *
	 * @param mapBorderMode
	 */
	public void setMapBorderMode(SubdivisionMesh.MapBorderMode mapBorderMode) {
		if (mapBorderMode != this.mapBorderMode) {
			setMapBorderModeForced(mapBorderMode);
		}
	}
}
