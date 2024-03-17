package org.openautomaker.base.utils.exporters;

import java.nio.file.Path;
import java.util.List;

import org.openautomaker.base.utils.models.MeshForProcessing;

/**
 *
 * @author Ian
 */
public interface MeshFileOutputConverter
{

    /**
     * Output the stl or amf file for the given project to the file indicated by the project job
     * UUID.
     * @param meshesForProcessing
     * @param printJobUUID
     * @param outputAsSingleFile
     * @return List of filenames that have been created
     */
    MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, boolean outputAsSingleFile);

    /**
     * Output the stl or amf file for the given project to the file indicated by the project job
     * UUID.
     * @param meshesForProcessing
     * @param printJobUUID
     * @param printJobDirectory
     * @param outputAsSingleFile
     * @return List of filenames that have been created
     */
	MeshExportResult outputFile(List<MeshForProcessing> meshesForProcessing, String printJobUUID, Path printJobDirectory, boolean outputAsSingleFile);
}
