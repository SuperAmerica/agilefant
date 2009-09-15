package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Story;

public class StorySplitActionTest {

    StorySplitAction testable;
    
    StoryBusiness storyBusiness;
    
    @Before
    public void setUp_dependencies() {
        testable = new StorySplitAction();
        
        storyBusiness = createStrictMock(StoryBusiness.class);
        testable.setStoryBusiness(storyBusiness);
    }
    
    private void replayAll() {
        replay(storyBusiness);
    }
    
    private void verifyAll() {
        verify(storyBusiness);
    }    
    
    @Test
    public void testSplit() {
        testable.setOriginalStoryId(123);
        expect(storyBusiness.retrieve(123)).andReturn(new Story());
        replayAll();
        assertEquals(Action.SUCCESS, testable.split());
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testSplit_notFound() {
        testable.setOriginalStoryId(-1);
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.split();
        verifyAll();
    }
}
