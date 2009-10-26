package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Story;

public class StoryHierarchyActionTest {
    
    private StoryHierarchyAction storyHierarchyAction;
    
    private StoryBusiness storyBusiness;

    
    
    @Before
    public void setUp_dependencies() {
        storyHierarchyAction = new StoryHierarchyAction();
        
        storyBusiness = createStrictMock(StoryBusiness.class);
        storyHierarchyAction.setStoryBusiness(storyBusiness);
    }
    
    private void replayAll() {
        replay(storyBusiness);
    }

    private void verifyAll() {
        verify(storyBusiness);
    }
    
    @Test
    public void testRecurseHierarchyAsList() {
        Story story = new Story();
        Story parent = new Story();
        Story ancestor = new Story();
        parent.setParent(ancestor);
        story.setParent(parent);
        storyHierarchyAction.setStoryId(123);
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.recurseHierarchyAsList());
        verifyAll();
        
        assertEquals(2, storyHierarchyAction.getHierarchy().size());
        assertSame(ancestor, storyHierarchyAction.getHierarchy().get(0));
        assertSame(parent, storyHierarchyAction.getHierarchy().get(1));
    }
    
    @Test
    public void testRecurseHierarchyAsList_noParent() {
        Story story = new Story();
        storyHierarchyAction.setStoryId(123);
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.recurseHierarchyAsList());
        verifyAll();
        
        assertEquals(0, storyHierarchyAction.getHierarchy().size());
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRecurseHierarchyAsList_noSuchStory() {
        storyHierarchyAction.setStoryId(-1);
        
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        storyHierarchyAction.recurseHierarchyAsList();
        verifyAll();
    }


}
