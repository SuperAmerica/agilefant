package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StorySplitBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Story;

public class StorySplitActionTest {

    StorySplitAction testable;
    
    StoryBusiness storyBusiness;
    
    StorySplitBusiness storySplitBusiness;
    
    @Before
    public void setUp_dependencies() {
        testable = new StorySplitAction();
        
        storyBusiness = createStrictMock(StoryBusiness.class);
        testable.setStoryBusiness(storyBusiness);
        
        storySplitBusiness = createStrictMock(StorySplitBusiness.class);
        testable.setStorySplitBusiness(storySplitBusiness);
    }
    
    private void replayAll() {
        replay(storyBusiness, storySplitBusiness);
    }
    
    private void verifyAll() {
        verify(storyBusiness, storySplitBusiness);
    }    
    
    @Test
    public void testSplit() {
        testSplitting(false);
    }
    
    @Test
    public void testSplit_checkMoveOriginalStory() {
        testable.setMoveOriginalStory(true);
        testSplitting(true);
    }

    private void testSplitting(boolean moveOriginal) {
        testable.setOriginalStoryId(123);
        Story story = new Story();
        testable.setOriginal(story);
        testable.setOldStories(null);
        expect(storySplitBusiness.splitStory(story, testable.getNewStories(), null, moveOriginal)).andReturn(null);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.split());
        verifyAll();
    }
    
    @Test
    public void testPrepare() {
        Story story = new Story();       
        expect(storyBusiness.retrieve(123)).andReturn(story);
        expect(storyBusiness.retrieveMultiple(null)).andReturn(null);
        replayAll();
        testable.setOriginalStoryId(123);
        testable.setOldStories(null);
        testable.prepare();
        verifyAll();
        assertEquals(story, testable.getOriginal());
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testPrepare_notFound() {     
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.setOriginalStoryId(-1);
        testable.prepare();
        verifyAll();
    }
}
