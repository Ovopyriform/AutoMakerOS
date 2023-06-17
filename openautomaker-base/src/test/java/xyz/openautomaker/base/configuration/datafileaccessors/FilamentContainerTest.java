/*
 * Copyright 2015 CEL UK
 */
package xyz.openautomaker.base.configuration.datafileaccessors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.File;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import xyz.openautomaker.base.configuration.BaseConfiguration;
import xyz.openautomaker.base.configuration.Filament;
import xyz.openautomaker.base.configuration.datafileaccessors.FilamentContainer;
import xyz.openautomaker.base.utils.BaseEnvironmentConfiguredTest;

/**
 *
 * @author tony
 */
public class FilamentContainerTest extends BaseEnvironmentConfiguredTest
{

    @Before
    public void clearUserFilamentDir()
    {
		Path userFilamentDir = BaseConfiguration.getUserFilamentDirectory();
		File[] files = userFilamentDir.toFile().listFiles();
        if (files != null && files.length > 0)
        {
            for (File filamentFile : files)
            {
                System.out.println("Delete file " + filamentFile);
                filamentFile.delete();
            }
        }
    }
    
    @Test
    public void testCreateNewFilament()
    {
        String NEW_ID = "U1234568";
        FilamentContainer filamentContainer = FilamentContainer.getInstance();
        filamentContainer.reload();
        ObservableList<Filament> userFilaments = filamentContainer.getUserFilamentList();
        ObservableList<Filament> completeFilaments = filamentContainer.getCompleteFilamentList();
        int numFilaments = completeFilaments.size();
        Filament greenABSFilament = filamentContainer.getFilamentByID("RBX-ABS-GR499");

        Filament filamentCopy = greenABSFilament.clone();
        filamentCopy.setFilamentID(NEW_ID);
        filamentCopy.setFriendlyFilamentName("GREEN COPY");

        filamentContainer.saveFilament(filamentCopy);
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());
    }    

    @Test
    public void testCreateNewFilamentAndDelete()
    {
        String NEW_ID = "U1234567";
        FilamentContainer filamentContainer = FilamentContainer.getInstance();
        filamentContainer.reload();
        ObservableList<Filament> userFilaments = filamentContainer.getUserFilamentList();
        ObservableList<Filament> completeFilaments = filamentContainer.getCompleteFilamentList();
        int numFilaments = completeFilaments.size();
        Filament greenABSFilament = filamentContainer.getFilamentByID("RBX-ABS-GR499");

        Filament filamentCopy = greenABSFilament.clone();
        filamentCopy.setFilamentID(NEW_ID);
        filamentCopy.setFriendlyFilamentName("GREEN COPY");

        filamentContainer.saveFilament(filamentCopy);
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());

        filamentContainer.deleteFilament(filamentCopy);
        assertEquals(0, userFilaments.size());
        assertEquals(numFilaments, completeFilaments.size());
        Filament retrievedFilament = filamentContainer.getFilamentByID(NEW_ID);
        Assert.assertNull(retrievedFilament);
    }
    
    @Test
    public void testCreateNewFilamentAndChangeAndSave()
    {
        String NEW_ID = "U1234568";
        FilamentContainer filamentContainer = FilamentContainer.getInstance();
        filamentContainer.reload();
        ObservableList<Filament> userFilaments = filamentContainer.getUserFilamentList();
        ObservableList<Filament> completeFilaments = filamentContainer.getCompleteFilamentList();
        int numFilaments = completeFilaments.size();
        Filament greenABSFilament = filamentContainer.getFilamentByID("RBX-ABS-GR499");

        Filament filamentCopy = greenABSFilament.clone();
        filamentCopy.setFilamentID(NEW_ID);
        filamentCopy.setFriendlyFilamentName("GREEN COPY");

        filamentContainer.saveFilament(filamentCopy);
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());
        
        filamentCopy.setBedTemperature(67);
        filamentContainer.saveFilament(filamentCopy);
        filamentContainer.reload();
        
        Filament editedFilament = filamentContainer.getFilamentByID(NEW_ID);
        assertEquals(67, editedFilament.getBedTemperature());
        assertNotSame(filamentCopy, editedFilament);
        
    }       
    
    @Test
    public void testCreateNewFilamentAndChangeNameAndSave()
    {
        String NEW_ID = "U1234569";
        FilamentContainer filamentContainer = FilamentContainer.getInstance();
        filamentContainer.reload();
        ObservableList<Filament> userFilaments = filamentContainer.getUserFilamentList();
        ObservableList<Filament> completeFilaments = filamentContainer.getCompleteFilamentList();
        int numFilaments = completeFilaments.size();
        Filament greenABSFilament = filamentContainer.getFilamentByID("RBX-ABS-GR499");

        Filament filamentCopy = greenABSFilament.clone();
        filamentCopy.setFilamentID(NEW_ID);
        filamentCopy.setFriendlyFilamentName("GREEN COPY");

        filamentContainer.saveFilament(filamentCopy);
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());
        
        String newName = "GREEN COPY 3";
        filamentCopy.setFriendlyFilamentName(newName);
        filamentContainer.saveFilament(filamentCopy);
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());
        
        filamentContainer.reload();
        
        Filament editedFilament = filamentContainer.getFilamentByID(NEW_ID);
        assertEquals(newName, editedFilament.getFriendlyFilamentName());
        assertNotSame(filamentCopy, editedFilament);
        
        assertEquals(numFilaments + 1, completeFilaments.size());
        assertEquals(1, userFilaments.size());
        
    }        

}
