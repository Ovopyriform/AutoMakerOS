
package celtech.coreUI.controllers.panels;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import celtech.FXTest;
import celtech.JavaFXConfiguredTest;
import celtech.Lookup;
import celtech.configuration.ApplicationConfiguration;
import celtech.printerControl.model.TestPrinter;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import xyz.openautomaker.base.BaseLookup;
import xyz.openautomaker.base.configuration.fileRepresentation.PrinterDefinitionFile;

/**
 *
 * @author alynch
 */
@Category(FXTest.class)
public class GCodePanelControllerTest extends JavaFXConfiguredTest {

	GCodePanelController controller;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		TestPrinter printer = new TestPrinter();
		printer.addHead();
		PrinterDefinitionFile printerDefinitionFile = new PrinterDefinitionFile();
		printerDefinitionFile.setTypeCode("RBX10");
		printer.setPrinterConfiguration(printerDefinitionFile);

		URL fxmlFileName = getClass().getResource(
				ApplicationConfiguration.fxmlUtilityPanelResourcePath + "GCodePanel.fxml");
		FXMLLoader loader = new FXMLLoader(fxmlFileName, BaseLookup.getLanguageBundle());

		try {
			VBox layout = (VBox) loader.load();
		}
		catch (IOException ex) {
			return;
		}
		controller = (GCodePanelController) loader.getController();
		Lookup.setSelectedPrinter(printer);
	}

	private Optional<String> convertBackslashes(Optional<String> path) {
		// On Windows, paths can have a mixture of forward slashes and
		// backslashes. Convert them all to forward slashes.
		if (path.isPresent())
			return Optional.of(path.get().replace("\\", "/"));
		else
			return path;
	}

	@Test
	public void testGetGCodeFileToUse1() throws IOException {
		String text = "!!Home_all";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/Home_all.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse2() throws IOException {
		String text = "!!Remove_Head#N1";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/RBX10/Remove_Head.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse3() throws IOException {
		String text = "!!Short_Purge#N0";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/RBX10/Short_Purge#N0.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse4() throws IOException {
		String text = "!!Short_Purge#N1";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/RBX10/Short_Purge#N1.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse5() throws IOException {
		String text = "!Short_Purge#N1";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/Short_Purge#N1.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse6() throws IOException {
		String text = "!RBX10/Short_Purge#N1";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/RBX10/Short_Purge#N1.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse7() throws IOException {
		String text = "!PurgeMaterial#RBX01-DM#NB";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/PurgeMaterial#RBX01-DM#NB.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse8() throws IOException {
		String text = "!PurgeMaterial";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/PurgeMaterial.gcode"));
	}

	@Test
	public void testGetGCodeFileToUse9() throws IOException {
		String text = "!!Home_all";
		Optional<String> fileToUse = convertBackslashes(controller.getGCodeFileToUse(text));
		assertTrue(fileToUse.get().endsWith("macros/Home_all.gcode"));
	}
}
