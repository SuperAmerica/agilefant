package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.model.Story;

import static org.junit.Assert.*;

public class StoryTreeIntegrityUtilsTest {

    StoryTreeIntegrityMessage fatalError;
    StoryTreeIntegrityMessage nonFatalError;
    
    @Before
    public void setUp() {
        Story source = new Story();
        source.setName("Source");
        Story target = new Story();
        target.setName("Target");
        
        fatalError = new StoryTreeIntegrityMessage(source, target,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_DEEPER_IN_HIERARCHY);
        
        nonFatalError = new StoryTreeIntegrityMessage(source, target,
                StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH);
    }
    
    @Test
    public void testGetFatalMessages() {
        List<StoryTreeIntegrityMessage> list = new ArrayList<StoryTreeIntegrityMessage>(
                Arrays.asList(fatalError, nonFatalError));
        StoryTreeIntegrityUtils.getFatalMessages(list);
        
        assertEquals(1, list.size());
        assertTrue(list.contains(fatalError));
    }
    
    @Test
    public void testGetFatalMessages_noFatals() {
        List<StoryTreeIntegrityMessage> list = new ArrayList<StoryTreeIntegrityMessage>(
                Arrays.asList(nonFatalError, nonFatalError));
        StoryTreeIntegrityUtils.getFatalMessages(list);
        
        assertEquals(2, list.size());
        assertFalse(list.contains(fatalError));
    }
}
