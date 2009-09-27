package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class StoryActionTest {

    StoryAction storyAction;
    
    StoryBusiness storyBusiness;
    BacklogBusiness backlogBusiness;
    
    Story story;
    Iteration iter;
    

    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        iter = new Iteration();
        iter.setId(6446);
    }

    
    @Before
    public void setUp_dependencies() {
        storyAction = new StoryAction();
        
        storyBusiness = createStrictMock(StoryBusiness.class);
        storyAction.setStoryBusiness(storyBusiness);
        
        backlogBusiness = createStrictMock(BacklogBusiness.class);
        storyAction.setBacklogBusiness(backlogBusiness);
    }
    
    private void replayAll() {
        replay(storyBusiness, backlogBusiness);
    }
    
    private void verifyAll() {
        verify(storyBusiness, backlogBusiness);
    }
    
    @Test
    public void testRetrieve() {
        storyAction.setStoryId(story.getId());
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
       
        replayAll();
        
        assertEquals(Action.SUCCESS, storyAction.retrieve());
        assertEquals(1234, storyAction.getStory().getId());
        
        verifyAll();
    }


    
    @Test(expected = ObjectNotFoundException.class)
    public void testRetrieve_noSuchStory() {
        storyAction.setStoryId(-1);
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        storyAction.retrieve();
        
        verifyAll();
    }
    
    @Test
    public void testCreate() {
        Story returnedStory = new Story();
        expect(storyBusiness.create(storyAction.getStory(), storyAction.getBacklogId(), storyAction.getUserIds())).andReturn(returnedStory);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.create());
        verifyAll();
        
        assertEquals(storyAction.getStory(), returnedStory);
    }
    
    
    @Test
    public void testStore() {
        storyAction.setStory(story);
        storyAction.setStoryId(story.getId());
        storyAction.setUsersChanged(false);
        
        expect(storyBusiness.store(story.getId(), story, null, null))
                .andReturn(story);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.store());
        verifyAll();
    }
    
    @Test
    public void testStore_changeResponsibles() {
        storyAction.setStory(story);
        storyAction.setStoryId(story.getId());
        storyAction.setUsersChanged(true);
        
        expect(storyBusiness.store(story.getId(), story, null, storyAction.getUserIds()))
                .andReturn(story);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.store());
        verifyAll();
    }
    
    @Test
    public void testStoryContents() {
        storyAction.setIterationId(iter.getId());
        story.setBacklog(iter);
        storyAction.setStoryId(story.getId());
        
        Set<Task> tasks = new HashSet<Task>(Arrays.asList(new Task(), new Task()));
        story.setTasks(tasks);
        
        expect(storyBusiness.getStoryContents(story.getId(), iter.getId()))
            .andReturn(tasks);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, storyAction.storyContents());
        assertEquals(tasks, storyAction.getStoryContents());
        
        verifyAll();
    }
        
    @Test
    public void testMoveStory() {
        storyAction.setStoryId(story.getId());
        storyAction.setBacklogId(iter.getId());
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expect(backlogBusiness.retrieve(iter.getId())).andReturn(iter);
        storyBusiness.moveStoryToBacklog(story, iter);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.moveStory());
        verifyAll();
    }

    /*
     * TEST DELETION
     */
    
    @Test
    public void testDelete() {
       storyAction.setStoryId(story.getId());
       expect(storyBusiness.retrieve(story.getId())).andReturn(story);
       storyBusiness.delete(story.getId());
       replayAll();
       
       assertEquals(Action.SUCCESS, storyAction.delete());
       
       verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testDelete_noSuchStory() {
        storyAction.setStoryId(-1);
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        storyAction.delete();
        
        verifyAll(); 
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testDelete_storyHasHourEntries() {
       storyAction.setStoryId(story.getId());
       expect(storyBusiness.retrieve(story.getId())).andReturn(story);
       storyBusiness.delete(story.getId());
       expectLastCall().andThrow(new ConstraintViolationException("Action not allowed", null, null));
       replayAll();
       
       storyAction.delete();
       
       verifyAll();
    }
    
    @Test
    public void testInitializePrefetchingData() {
        Story newStory = new Story();
        newStory.setId(2222);
        expect(storyBusiness.retrieve(newStory.getId())).andReturn(newStory);
        
        replayAll();
        
        storyAction.initializePrefetchedData(newStory.getId());
        assertEquals(newStory, storyAction.getStory());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testInitializePrefetchingData_noSuchStory() {
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        
        storyAction.initializePrefetchedData(-1);
        
        verifyAll();
    }
    
    
    @Test
    public void testRankStory() {
        storyAction.setStoryId(123);
        storyAction.setRankUnderId(666);
        storyAction.setBacklogId(222);
        
        Story lower = new Story();
        Story upper = new Story();
        Story returned = new Story();
        Backlog parent = new Project();
        
        expect(storyBusiness.retrieve(123)).andReturn(lower);
        expect(storyBusiness.retrieveIfExists(666)).andReturn(upper);
        expect(backlogBusiness.retrieveIfExists(222)).andReturn(parent);
        
        expect(storyBusiness.rankAndMove(lower, upper, parent)).andReturn(returned);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.rankStory());
        verifyAll();
        
        assertEquals(returned, storyAction.getStory());
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRankStory_notFound() {
        storyAction.setStoryId(-1);
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        storyAction.rankStory();
        verifyAll();
    }
    
}
