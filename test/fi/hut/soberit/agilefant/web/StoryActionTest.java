package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryTO;


public class StoryActionTest {

    StoryAction storyAction = new StoryAction();
    StoryBusiness storyBusiness;
    TransferObjectBusiness transferObjectBusiness;
    
    Story story;
    Iteration iter;
    
    @Before
    public void setUp_dependencies() {
        storyBusiness = createStrictMock(StoryBusiness.class);
        storyAction.setStoryBusiness(storyBusiness);
        
        transferObjectBusiness = createStrictMock(TransferObjectBusiness.class);
        storyAction.setTransferObjectBusiness(transferObjectBusiness);
    }
    
    private void replayAll() {
        replay(storyBusiness, transferObjectBusiness);
    }
    
    private void verifyAll() {
        verify(storyBusiness, transferObjectBusiness);
    }
    
    private void expectStoryToTransferObject(Collection<User> responsibles) {
        expect(storyBusiness.getStorysProjectResponsibles(story)).andReturn(responsibles);
        expect(transferObjectBusiness.constructStoryTO(story, responsibles)).andReturn(new StoryTO(story));
    }

    
    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        iter = new Iteration();
        iter.setId(6446);
        
        storyAction.setStory(story);        
    }
    
    @Test
    public void testRetrieve() {
        Collection<User> responsibles = Arrays.asList(new User());

        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expectStoryToTransferObject(responsibles);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, storyAction.retrieve());
        assertTrue(storyAction.getStory() instanceof StoryTO);
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
    public void testStore() {
        expect(storyBusiness.store(storyAction.getStoryId(),
                storyAction.getBacklogId(), story, new HashSet<Integer>(),
                storyAction.getPriority()))
                .andReturn(story);
        expectStoryToTransferObject(null);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.store());
        verifyAll();
    }
    
    
    @Test
    public void testStoryContents() {
        storyAction.setIterationId(iter.getId());
        story.setBacklog(iter);
        storyAction.setStoryId(story.getId());
        
        Collection<Task> tasks = Arrays.asList(new Task(), new Task());
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
        storyBusiness.attachStoryToBacklog(story.getId(), iter.getId(), false);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, storyAction.moveStory());
        
        verifyAll();
    }
    
    /*
     * TEST DELETION
     */
    
    @Test
    public void testDelete() {
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
       expect(storyBusiness.retrieve(story.getId())).andReturn(story);
       storyBusiness.delete(story.getId());
       expectLastCall().andThrow(new ConstraintViolationException("Action not allowed", null, null));
       replayAll();
       
       storyAction.delete();
       
       verifyAll();
    }
    
    /*
     * TEST PREFETCHING
     */
    
    @Test
    public void getIdFieldName() {
        assertEquals("storyId", storyAction.getIdFieldName());
    }
    
    @Test
    public void testInitializePrefetchingData() {
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        
        replayAll();
        
        storyAction.initializePrefetchedData(story.getId());
        assertEquals(story, storyAction.getStory());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testInitializePrefetchingData_noSuchStory() {
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        
        storyAction.initializePrefetchedData(-1);
        
        verifyAll();
    }
}
