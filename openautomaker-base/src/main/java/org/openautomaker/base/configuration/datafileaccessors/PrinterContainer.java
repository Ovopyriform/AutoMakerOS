package org.openautomaker.base.configuration.datafileaccessors;

import static org.openautomaker.environment.OpenAutomakerEnv.PRINTERS;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openautomaker.base.configuration.PrinterFileFilter;
import org.openautomaker.base.configuration.fileRepresentation.PrinterDefinitionFile;
import org.openautomaker.environment.OpenAutomakerEnv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

/**
 *
 * @author ianhudson
 */
public class PrinterContainer
{

	private static final Logger LOGGER = LogManager.getLogger();

    private static PrinterContainer instance = null;
    private static ObservableList<PrinterDefinitionFile> completePrinterList;
    private static ObservableMap<String, PrinterDefinitionFile> completePrinterMap;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static final String defaultPrinterID = "RBX01";

    private PrinterContainer()
    {
        completePrinterList = FXCollections.observableArrayList();
        completePrinterMap = FXCollections.observableHashMap();
		File printerDirHandle = OpenAutomakerEnv.get().getApplicationPath(PRINTERS).toFile();
        File[] printerFiles = printerDirHandle.listFiles(new PrinterFileFilter());
        if (printerFiles == null)
        {
			LOGGER.error("Error loading printer list from \"" + printerDirHandle.getAbsolutePath() + "\"");
        }
        else
        {
            ArrayList<PrinterDefinitionFile> printers = ingestPrinters(printerFiles);
            completePrinterList.addAll(printers);
        }
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    private ArrayList<PrinterDefinitionFile> ingestPrinters(File[] printerFilesToIngest)
    {
        ArrayList<PrinterDefinitionFile> printerList = new ArrayList<>();

        for (File printerFile : printerFilesToIngest)
        {
            try
            {
                PrinterDefinitionFile printerData = mapper.readValue(printerFile, PrinterDefinitionFile.class);

                printerList.add(printerData);
                completePrinterMap.put(printerData.getTypeCode(), printerData);

            } catch (IOException ex)
            {
				LOGGER.error("Error loading printer " + printerFile.getAbsolutePath());
            }
        }

        return printerList;
    }

    public static PrinterContainer getInstance()
    {
        if (instance == null)
        {
            instance = new PrinterContainer();
        }

        return instance;
    }

    public static PrinterDefinitionFile getPrinterByID(String printerID)
    {
        if (instance == null)
        {
            PrinterContainer.getInstance();
        }

        PrinterDefinitionFile returnedPrinter = completePrinterMap.get(printerID);
        return returnedPrinter;
    }

    public static ObservableList<PrinterDefinitionFile> getCompletePrinterList()
    {
        if (instance == null)
        {
            instance = new PrinterContainer();
        }

        return completePrinterList;
    }
}
