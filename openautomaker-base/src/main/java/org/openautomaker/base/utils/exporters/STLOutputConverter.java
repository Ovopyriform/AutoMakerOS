package org.openautomaker.base.utils.exporters;

import static org.openautomaker.environment.OpenAutomakerEnv.PRINT_JOBS;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.utils.models.MeshForProcessing;
import org.openautomaker.base.utils.threed.CentreCalculations;
import org.openautomaker.base.utils.threed.MeshToWorldTransformer;
import org.openautomaker.environment.OpenAutomakerEnv;

import javafx.geometry.Point3D;
import javafx.scene.shape.ObservableFaceArray;
import javafx.scene.shape.TriangleMesh;

/**
 *
 * @author ianhudson
 */
public class STLOutputConverter implements MeshFileOutputConverter {

	private final Logger LOGGER = LogManager.getLogger();

	private int modelFileCount = 0;

	private static final String DOT_STL = ".stl";

	public STLOutputConverter() {
	}

	@Override
	public MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, boolean outputAsSingleFile) {
		return outputFile(meshesForProcessing, printJobUUID, OpenAutomakerEnv.get().getUserPath(PRINT_JOBS).resolve(printJobUUID), outputAsSingleFile);
	}

	@Override
	public MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing,
			String printJobUUID, Path printJobDirectory,
			boolean outputAsSingleFile) {
		List<String> createdFiles = new ArrayList<>();
		List<Vector3D> centroids = new ArrayList<>();

		modelFileCount = 0;

		if (outputAsSingleFile) {
			String tempModelFilename = printJobUUID
					+ "-" + modelFileCount + DOT_STL;
			String tempModelFilenameWithPath = printJobDirectory.resolve(tempModelFilename).toString();

			createdFiles.add(tempModelFilenameWithPath);

			centroids.add(outputMeshViewsInSingleFile(tempModelFilenameWithPath, meshesForProcessing));
		}
		else {
			for (MeshForProcessing meshForProcessing : meshesForProcessing) {
				String tempModelFilename = printJobUUID
						+ "-" + modelFileCount + DOT_STL;
				String tempModelFilenameWithPath = printJobDirectory.resolve(tempModelFilename).toString();

				List<MeshForProcessing> miniMap = new ArrayList<>();
				miniMap.add(new MeshForProcessing(meshForProcessing.getMeshView(), meshForProcessing.getMeshToWorldTransformer()));

				centroids.add(outputMeshViewsInSingleFile(tempModelFilenameWithPath, miniMap));
				createdFiles.add(tempModelFilename);
				modelFileCount++;
			}
		}

		CentreCalculations centreCalc = new CentreCalculations();
		centroids.forEach(centroid -> {
			centreCalc.processPoint(centroid);
		});

		return new MeshExportResult(createdFiles, centreCalc.getResult());
	}

	/**
	 * Returns the centroid of the meshes in world co-ordinates
	 *
	 * @param tempModelFilenameWithPath
	 * @param meshViews
	 * @param meshToWorldTransformer
	 * @return
	 */
	private Vector3D outputMeshViewsInSingleFile(final String tempModelFilenameWithPath,
			List<MeshForProcessing> meshesForProcessing) {
		CentreCalculations centreCalc = new CentreCalculations();

		File fFile = new File(tempModelFilenameWithPath);

		final short blankSpace = (short) 0;

		try {
			final DataOutputStream dataOutput = new DataOutputStream(new FileOutputStream(fFile));

			try {
				int totalNumberOfFacets = 0;
				//ByteBuffer headerByteBuffer = null;

				for (MeshForProcessing meshForProcessing : meshesForProcessing) {
					TriangleMesh triangles = (TriangleMesh) meshForProcessing.getMeshView().getMesh();
					ObservableFaceArray faceArray = triangles.getFaces();
					int numberOfFacets = faceArray.size() / 6;
					totalNumberOfFacets += numberOfFacets;
				}

				//File consists of:
				// 80 byte ascii header
				// Int containing number of facets
				ByteBuffer headerBuffer = ByteBuffer.allocate(80);
				headerBuffer.put(getSignature().getBytes("UTF-8"));

				dataOutput.write(headerBuffer.array());

				byte outputByte = (byte) (totalNumberOfFacets & 0xff);
				dataOutput.write(outputByte);

				outputByte = (byte) ((totalNumberOfFacets >>> 8) & 0xff);
				dataOutput.write(outputByte);

				outputByte = (byte) ((totalNumberOfFacets >>> 16) & 0xff);
				dataOutput.write(outputByte);

				outputByte = (byte) ((totalNumberOfFacets >>> 24) & 0xff);
				dataOutput.write(outputByte);

				ByteBuffer dataBuffer = ByteBuffer.allocate(50);
				//Binary STL files are always assumed to be little endian
				dataBuffer.order(ByteOrder.LITTLE_ENDIAN);

				// Then for each facet:
				//  3 floats for facet normals
				//  3 x 3 floats for vertices (x,y,z * 3)
				//  2 byte spacer
				for (MeshForProcessing meshForProcessing : meshesForProcessing) {
					MeshToWorldTransformer meshToWorldTransformer = meshForProcessing.getMeshToWorldTransformer();

					TriangleMesh triangles = (TriangleMesh) meshForProcessing.getMeshView().getMesh();
					int[] faceArray = triangles.getFaces().toArray(null);
					float[] pointArray = triangles.getPoints().toArray(null);
					int numberOfFacets = faceArray.length / 6;

					for (int facetNumber = 0; facetNumber < numberOfFacets; facetNumber++) {
						dataBuffer.rewind();
						// Output zero normals
						dataBuffer.putFloat(0);
						dataBuffer.putFloat(0);
						dataBuffer.putFloat(0);

						for (int vertexNumber = 0; vertexNumber < 3; vertexNumber++) {
							int vertexIndex = faceArray[(facetNumber * 6) + (vertexNumber * 2)];

							Point3D vertex = meshToWorldTransformer.transformMeshToRealWorldCoordinates(
									pointArray[vertexIndex * 3],
									pointArray[(vertexIndex * 3) + 1],
									pointArray[(vertexIndex * 3) + 2]);

							centreCalc.processPoint(vertex.getX(), vertex.getY(), vertex.getZ());

							dataBuffer.putFloat((float) vertex.getX());
							dataBuffer.putFloat((float) vertex.getZ());
							dataBuffer.putFloat(-(float) vertex.getY());
						}
						dataBuffer.putShort(blankSpace);

						dataOutput.write(dataBuffer.array());
					}
				}
			}
			catch (IOException ex) {
				LOGGER.error("Error writing to file " + fFile + " :" + ex.toString());

			}
			finally {
				try {
					if (dataOutput != null) {
						dataOutput.flush();
						dataOutput.close();
					}
				}
				catch (IOException ex) {
					LOGGER.error("Error closing file " + fFile + " :" + ex.toString());
				}
			}
		}
		catch (FileNotFoundException ex) {
			LOGGER.error("Error opening STL output file " + fFile + " :" + ex.toString());
		}

		return centreCalc.getResult();
	}

	private String getSignature() {
		return "Generated by " + OpenAutomakerEnv.getI18N().t("application.title") + " - " + OpenAutomakerEnv.get().getVersion();
	}
}
