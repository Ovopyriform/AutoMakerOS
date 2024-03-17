package org.openautomaker.base.configuration.slicer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.datafileaccessors.HeadContainer;
import org.openautomaker.base.configuration.datafileaccessors.PrinterContainer;
import org.openautomaker.base.configuration.fileRepresentation.HeadFile;
import org.openautomaker.base.configuration.fileRepresentation.PrinterDefinitionFile;
import org.openautomaker.base.printerControl.model.Head;
import org.openautomaker.base.printerControl.model.Nozzle;
import org.openautomaker.base.printerControl.model.Printer;
import org.openautomaker.base.utils.cura.CuraDefaultSettingsEditor;
import org.openautomaker.base.utils.models.PrintableMeshes;
import org.openautomaker.environment.Slicer;

/**
 *
 * @author George Salter
 */
// Renamed to Cura4PlusConfigConverter as we're using it for Cura 5 also
public class Cura4PlusConfigConvertor {

	private static final Logger LOGGER = LogManager.getLogger();

	private final Printer printer;
	private final PrintableMeshes printableMeshes;
	private final Slicer slicerType;

	private CuraDefaultSettingsEditor curaDefaultSettingsEditor;

	public Cura4PlusConfigConvertor(Printer printer, PrintableMeshes printableMeshes, Slicer slicerType) {
		this.printer = printer;
		this.printableMeshes = printableMeshes;
		this.slicerType = slicerType;
	}

	public void injectConfigIntoCura4SettingsFile(Path configFile, Path storageDirectory) {
		curaDefaultSettingsEditor = new CuraDefaultSettingsEditor(printableMeshes.getNumberOfNozzles() <= 1, slicerType);
		curaDefaultSettingsEditor.beginEditing();

		addDefaultsForPrinter();
		addExtrudersAndDefaults();
		addMappedSettings(configFile);

		curaDefaultSettingsEditor.endEditing(storageDirectory);
	}

	private void addDefaultsForPrinter() {
		int width;
		int depth;
		int height;

		if (printer == null) {
			PrinterDefinitionFile printerDef = PrinterContainer.getPrinterByID(PrinterContainer.defaultPrinterID);
			width = printerDef.getPrintVolumeWidth();
			depth = printerDef.getPrintVolumeDepth();
			height = printerDef.getPrintVolumeHeight();
		} else {
			width = printer.printerConfigurationProperty().get().getPrintVolumeWidth();
			depth = printer.printerConfigurationProperty().get().getPrintVolumeDepth();
			height = printer.printerConfigurationProperty().get().getPrintVolumeHeight();
		}
		curaDefaultSettingsEditor.editDefaultFloatValue("machine_width", width);
		curaDefaultSettingsEditor.editDefaultFloatValue("machine_depth", depth);
		curaDefaultSettingsEditor.editDefaultFloatValue("machine_height", height);

		// Currently need to move origin back to corner of bed, not center.
		curaDefaultSettingsEditor.editDefaultFloatValue("mesh_position_x", -(width / 2));
		curaDefaultSettingsEditor.editDefaultFloatValue("mesh_position_y", -(depth / 2));

		int numberOfNozzles = printableMeshes.getNumberOfNozzles();
		curaDefaultSettingsEditor.editDefaultIntValue("machine_extruder_count", numberOfNozzles);
		curaDefaultSettingsEditor.editDefaultIntValue("extruders_enabled_count", numberOfNozzles);
	}

	private void addExtrudersAndDefaults() {
		Head headOnPrinter;
		if (printer == null || printer.headProperty() == null || printer.headProperty().get() == null) {
			HeadFile defaultHeadData = HeadContainer.getHeadByID(HeadContainer.defaultHeadID);
			headOnPrinter = new Head(defaultHeadData);
		} else {
			headOnPrinter = printer.headProperty().get();
		}

		List<Nozzle> nozzles = headOnPrinter.getNozzles();
		for (int i = 0; i < nozzles.size(); i++) {
			String nozzleReference = "noz" + String.valueOf(i + 1);
			curaDefaultSettingsEditor.beginNewExtruderFile(nozzleReference);
			Nozzle nozzle = nozzles.get(i);
			curaDefaultSettingsEditor.editExtruderValue("machine_nozzle_id", nozzleReference, nozzleReference);
			curaDefaultSettingsEditor.editExtruderValue("machine_nozzle_size", nozzleReference, String.valueOf(nozzle.diameterProperty().get()));
		}
	}

	private void addMappedSettings(Path configFile) {

		try (BufferedReader fileReader = new BufferedReader(new FileReader(configFile.toFile()))) {
			String readLine = null;
			while ((readLine = fileReader.readLine()) != null) {
				if (!readLine.startsWith("#")) {
					String[] settingAndValue = readLine.split("=");
					String settingName = settingAndValue[0];
					String value = settingAndValue[1];

					if (!value.contains(":")) {
						curaDefaultSettingsEditor.editDefaultValue(settingName, value);
						continue;
					}

					String[] valuesForNozzles = value.split(":");
					for (int i = 0; i < valuesForNozzles.length; i++) {
						String nozzleReference = "noz" + String.valueOf(i + 1);
						curaDefaultSettingsEditor.editExtruderValue(settingName, nozzleReference, valuesForNozzles[i]);
					}
				}
			}
		} catch (FileNotFoundException ex) {
			LOGGER.error("Config file: " + configFile + " could not be found.", ex);
			//LOGGER.error(ex.getMessage());
		} catch (IOException ex) {
			LOGGER.error("Error while reading config file: " + configFile, ex);
			//LOGGER.error(ex.getMessage());
		}
	}
}
