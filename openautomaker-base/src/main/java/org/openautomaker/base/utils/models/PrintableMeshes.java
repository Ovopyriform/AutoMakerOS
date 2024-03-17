package org.openautomaker.base.utils.models;

import java.util.List;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.openautomaker.base.configuration.RoboxProfile;
import org.openautomaker.base.configuration.datafileaccessors.HeadContainer;
import org.openautomaker.base.configuration.fileRepresentation.HeadFile;
import org.openautomaker.base.configuration.fileRepresentation.PrinterSettingsOverrides;
import org.openautomaker.base.services.camera.CameraTriggerData;
import org.openautomaker.base.services.slicer.PrintQualityEnumeration;
import org.openautomaker.environment.Slicer;

/**
 *
 * @author ianhudson
 */
public class PrintableMeshes
{

    private final List<MeshForProcessing> meshesForProcessing;
    private final List<Boolean> usedExtruders;
    private final List<Integer> extruderForModel;
    private final String projectName;
    private final String requiredPrintJobID;
    private final RoboxProfile settings;
    private final PrinterSettingsOverrides printOverrides;
    private final PrintQualityEnumeration printQuality;
    private final Slicer defaultSlicerType;
    private final Vector3D centreOfPrintedObject;
    private final boolean safetyFeaturesRequired;
    private final boolean cameraEnabled;
    private final CameraTriggerData cameraTriggerData;

    public PrintableMeshes(List<MeshForProcessing> meshesForProcessing,
            List<Boolean> usedExtruders,
            List<Integer> extruderForModel,
            String projectName,
            String requiredPrintJobID,
            RoboxProfile settings,
            PrinterSettingsOverrides printOverrides,
            PrintQualityEnumeration printQuality,
            Slicer defaultSlicerType,
            Vector3D centreOfPrintedObject,
            boolean safetyFeaturesRequired,
            boolean cameraEnabled,
            CameraTriggerData cameraTriggerData)
    {
        this.meshesForProcessing = meshesForProcessing;
        this.usedExtruders = usedExtruders;
        this.extruderForModel = extruderForModel;
        this.projectName = projectName;
        this.requiredPrintJobID = requiredPrintJobID;
        this.settings = settings;
        this.printOverrides = printOverrides;
        this.printQuality = printQuality;
        this.defaultSlicerType = defaultSlicerType;
        this.centreOfPrintedObject = centreOfPrintedObject;
        this.safetyFeaturesRequired = safetyFeaturesRequired;
        this.cameraEnabled = cameraEnabled;
        this.cameraTriggerData = cameraTriggerData;
    }

    public List<MeshForProcessing> getMeshesForProcessing()
    {
        return meshesForProcessing;
    }

    public List<Boolean> getUsedExtruders()
    {
        return usedExtruders;
    }

    public List<Integer> getExtruderForModel()
    {
        return extruderForModel;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public String getRequiredPrintJobID()
    {
        return requiredPrintJobID;
    }

    public RoboxProfile getSettings()
    {
        return settings;
    }

    public PrinterSettingsOverrides getPrintOverrides()
    {
        return printOverrides;
    }

    public PrintQualityEnumeration getPrintQuality()
    {
        return printQuality;
    }

    public Slicer getDefaultSlicerType()
    {
        return defaultSlicerType;
    }

    public Vector3D getCentreOfPrintedObject()
    {
        return centreOfPrintedObject;
    }

    public boolean isSafetyFeaturesRequired()
    {
        return safetyFeaturesRequired;
    }

    public boolean isCameraEnabled()
    {
        return cameraEnabled;
    }

    public CameraTriggerData getCameraTriggerData()
    {
        return cameraTriggerData;
    }
    
    public int getNumberOfNozzles()
    {
        int nNozzles = 0;
        HeadFile printerHead = HeadContainer.getHeadByID(settings.getHeadType());
        if (printerHead != null)
            nNozzles = printerHead.getNozzles().size();
        return nNozzles;
    }
}
