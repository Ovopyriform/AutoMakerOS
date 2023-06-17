/*
 * Copyright 2014 CEL UK
 */

package xyz.openautomaker.base.utils;

import java.util.Collection;
import java.util.HashSet;
import org.junit.Test;

import xyz.openautomaker.base.utils.DeDuplicator;

import static org.junit.Assert.*;

/**
 *
 * @author tony
 */
public class DeDuplicatorTest
{
    
    @Test
    public void testSuggestNonDuplicateNameForNewName()
    {
        Collection<String> currentNames = new HashSet<>();
        currentNames.add("TIM");
        currentNames.add("PETER");
        currentNames.add("PETER1");
        String INITIAL_NAME = "JOHN";
        String suggestedName = DeDuplicator.suggestNonDuplicateNameCopy(INITIAL_NAME, currentNames);
        assertEquals(INITIAL_NAME, suggestedName);
    }
    
    @Test
    public void testSuggestNonDuplicateNameForNonNewName()
    {
        Collection<String> currentNames = new HashSet<>();
        currentNames.add("TIM");
        currentNames.add("PETER");
        currentNames.add("PETER1");
        String INITIAL_NAME = "TIM";
        String suggestedName = DeDuplicator.suggestNonDuplicateNameCopy(INITIAL_NAME, currentNames);
        assertEquals(INITIAL_NAME + " (Copy)", suggestedName);
    }    
    
    @Test
    public void testSuggestNonDuplicateNameForNonNewNameGoingTo3()
    {
        Collection<String> currentNames = new HashSet<>();
        currentNames.add("TIM");
        currentNames.add("PETER");
        currentNames.add("PETER1");
        currentNames.add("PETER2");
        String INITIAL_NAME = "PETER";
        String suggestedName = DeDuplicator.suggestNonDuplicateNameCopy(INITIAL_NAME, currentNames);
        assertEquals(INITIAL_NAME + " (Copy)", suggestedName);
    }        
    
}
