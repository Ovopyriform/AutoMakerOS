/*
 * Copyright 2015 CEL UK
 */
package xyz.openautomaker.base.configuration.slicer;

import static org.apache.commons.io.FileUtils.readLines;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import xyz.openautomaker.base.configuration.SlicerType;
import xyz.openautomaker.base.configuration.fileRepresentation.PrinterSettingsOverrides;
import xyz.openautomaker.base.configuration.fileRepresentation.SlicerMappingData;
import xyz.openautomaker.base.configuration.fileRepresentation.SupportType;
import xyz.openautomaker.base.configuration.slicer.SlicerConfigWriter;
import xyz.openautomaker.base.configuration.slicer.SlicerConfigWriterFactory;
import xyz.openautomaker.base.services.slicer.PrintQualityEnumeration;
import xyz.openautomaker.base.utils.BaseEnvironmentConfiguredTest;

/**
 *
 * @author tony
 */
public class SlicerConfigWriterTest extends BaseEnvironmentConfiguredTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void testOptionalOperatorConditionSucceeds() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		SlicerMappingData mappingData = new SlicerMappingData();
		mappingData.setDefaults(new ArrayList<>());
		mappingData.setMappingData(new HashMap<>());
		mappingData.getMappingData().put("supportAngle", "supportOverhangThreshold_degrees:?generateSupportMaterial=false->-1");
		configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("supportAngle=-1"));
	}

	@Test
	public void testOptionalOperatorConditionFails() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
		printerSettings.setPrintSupportTypeOverride(SupportType.MATERIAL_1);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		SlicerMappingData mappingData = new SlicerMappingData();
		mappingData.setDefaults(new ArrayList<>());
		mappingData.setMappingData(new HashMap<>());
		mappingData.getMappingData().put("supportAngle", "supportOverhangThreshold_degrees:?generateSupportMaterial=false->-1");
		configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
		List<String> outputData = readLines(destinationFile.toFile());
		assertFalse(outputData.contains("supportAngle=40"));
	}

	@Test
	public void testNoOutputOperatorConditionFails() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
		printerSettings.setRaftOverride(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		SlicerMappingData mappingData = new SlicerMappingData();
		mappingData.setDefaults(new ArrayList<>());
		mappingData.setMappingData(new HashMap<>());
		mappingData.getMappingData().put("raftInterfaceLinewidth", "400:?printRaft=false->|");
		configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("raftInterfaceLinewidth=400"));
	}

	@Test
	public void testNoOutputOperatorConditionSucceeds() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
		printerSettings.setRaftOverride(false);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		SlicerMappingData mappingData = new SlicerMappingData();
		mappingData.setDefaults(new ArrayList<>());
		mappingData.setMappingData(new HashMap<>());
		mappingData.getMappingData().put("raftInterfaceLinewidth", "400:?printRaft=false->|");
		configWriter.generateConfigForSlicerWithMappings(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile, mappingData);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(!outputData.contains("raftInterfaceLinewidth"));
	}

	@Test
	public void testGenerateConfigForRaftOnCuraDraft() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
		printerSettings.setRaftOverride(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("raftBaseThickness=300"));
		assertTrue(outputData.contains("raftInterfaceThickness=280"));
	}

	@Test
	public void testGenerateConfigForRaftOnCuraNormal() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
		printerSettings.setRaftOverride(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("raftBaseThickness=300"));
		assertTrue(outputData.contains("raftInterfaceThickness=280"));
	}

	@Test
	public void testGenerateConfigForRaftOnCuraFine() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
		printerSettings.setRaftOverride(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("raftBaseThickness=300"));
		assertTrue(outputData.contains("raftInterfaceThickness=280"));
	}

	@Test
	public void testGenerateConfigForNoRaftOnCuraDraft() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
		printerSettings.setRaftOverride(false);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(!outputData.contains("raftBaseThickness"));
		assertTrue(!outputData.contains("raftInterfaceThickness"));
	}

	@Test
	public void testGenerateConfigForNoRaftOnCuraNormal() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
		printerSettings.setRaftOverride(false);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(!outputData.contains("raftBaseThickness"));
		assertTrue(!outputData.contains("raftInterfaceThickness"));
	}

	@Test
	public void testGenerateConfigForNoRaftOnCuraFine() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
		printerSettings.setRaftOverride(false);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(!outputData.contains("raftBaseThickness"));
		assertTrue(!outputData.contains("raftInterfaceThickness"));
	}

	//    @Test
	//    public void testGenerateConfigForRaftOnSlic3rDraft() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
	//        printerSettings.setRaftOverride(true);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        for (String outputData1 : outputData)
	//        {
	//            System.out.println(outputData1);
	//        }
	//        assertTrue(outputData.contains("raft_layers = 2"));
	//        assertTrue(outputData.contains("support_material_interface_layers = 1"));
	//    }
	//
	//    @Test
	//    public void testGenerateConfigForNoRaftOnSlic3rDraft() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.DRAFT);
	//        printerSettings.setRaftOverride(false);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        assertTrue(outputData.contains("raft_layers = 0"));
	//    }
	//
	//    @Test
	//    public void testGenerateConfigForRaftOnSlic3rNormal() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
	//        printerSettings.setRaftOverride(true);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        assertTrue(outputData.contains("raft_layers = 2"));
	//    }
	//
	//    @Test
	//    public void testGenerateConfigForNoRaftOnSlic3rNormal() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.NORMAL);
	//        printerSettings.setRaftOverride(false);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        assertTrue(outputData.contains("raft_layers = 0"));
	//    }
	//
	//    @Test
	//    public void testGenerateConfigForRaftOnSlic3rFine() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
	//        printerSettings.setRaftOverride(true);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        assertTrue(outputData.contains("raft_layers = 3"));
	//    }
	//
	//    @Test
	//    public void testGenerateConfigForNoRaftOnSlic3rFine() throws IOException
	//    {
	//        String TEMPFILENAME = "output.roboxprofile";
	//        SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(
	//            SlicerType.Slic3r);
	//        PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
	//        printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
	//        printerSettings.setRaftOverride(false);
	//        String destinationFile = temporaryFolder.getRoot().getAbsolutePath() + File.separator
	//            + TEMPFILENAME;
	//        configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM"), destinationFile);
	//        List<String> outputData = readLines(new File(destinationFile));
	//        assertTrue(outputData.contains("raft_layers = 0"));
	//    }

	@Test
	public void testGenerateConfigForSparseInfillOffCuraFine() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
		printerSettings.setFillDensityOverride(0);
		printerSettings.setFillDensityChangedByUser(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("sparseInfillLineDistance=-1"));
	}

	@Test
	public void testGenerateConfigForSparseInfillOnCuraFine() throws IOException {
		String TEMPFILENAME = "output.roboxprofile";
		SlicerConfigWriter configWriter = SlicerConfigWriterFactory.getConfigWriter(SlicerType.Cura);
		PrinterSettingsOverrides printerSettings = new PrinterSettingsOverrides();
		printerSettings.setPrintQuality(PrintQualityEnumeration.FINE);
		printerSettings.setFillDensityOverride(0.5f);
		printerSettings.setFillDensityChangedByUser(true);

		Path tempFolderPath = temporaryFolder.getRoot().getAbsoluteFile().toPath();
		Path destinationFile = tempFolderPath.resolve(TEMPFILENAME);

		configWriter.generateConfigForSlicer(printerSettings.getSettings("RBX01-SM", SlicerType.Cura), destinationFile);
		List<String> outputData = readLines(destinationFile.toFile());
		assertTrue(outputData.contains("sparseInfillLineDistance=800"));
	}

}
