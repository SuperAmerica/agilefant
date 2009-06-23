package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.*;

import fi.hut.soberit.agilefant.business.impl.TaskBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import static org.junit.Assert.*;

public class TaskBusinessTest {
    
    private TaskBusinessImpl taskBusiness = new TaskBusinessImpl() {
        // Overrided to skip static call to SecurityUtil
        @Override
        public User getLoggedInUser() {
            return loggedInUser;
        }
    };
    private IterationBusiness iterationBusiness;
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness; 
    private StoryBusiness storyBusiness;
    private UserBusiness userBusiness;
    private TaskDAO taskDAO;
    
    private Iteration iteration;
    private Story story;
    private Task task;
    private User loggedInUser;
    private User creatorUser;
    private DateTime createdDate;
   
    @Before
    public void setUp_dependencies() {
        taskDAO = createMock(TaskDAO.class);
        taskBusiness.setTaskDAO(taskDAO);
        
        iterationBusiness = createMock(IterationBusiness.class);
        taskBusiness.setIterationBusiness(iterationBusiness);
        
        storyBusiness = createMock(StoryBusiness.class);
        taskBusiness.setStoryBusiness(storyBusiness);
        
        userBusiness = createMock(UserBusiness.class);
        taskBusiness.setUserBusiness(userBusiness);
        
        iterationHistoryEntryBusiness = createMock(IterationHistoryEntryBusiness.class);
        taskBusiness.setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);    
    }
    
    @Before
    public void setUp() {
        task = new Task();
        iteration = new Iteration();
        iteration.setId(2);
        story = new Story();
        story.setId(123);
        task.setId(0);
        
        loggedInUser = new User();
        loggedInUser.setId(666);
        
        creatorUser = new User();
        creatorUser.setId(567);
        
        createdDate = new DateTime().minusDays(1233);
        
        task.setCreator(creatorUser);
        task.setCreatedDate(createdDate.toDate());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreTask_storyAndIterationGiven() {
        taskBusiness.storeTask(task, iteration.getId(), story.getId(), null);
    }
    
    @Test
    public void testStoreTask_newTaskToIteration() {
        
        task.setCreatedDate(null);
        task.setCreator(null);
        
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        expect(taskDAO.create(task)).andReturn(1351);
        expect(taskDAO.get(1351)).andReturn(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replay(iterationBusiness, storyBusiness, taskDAO, iterationHistoryEntryBusiness);
        
        Task actualTask = taskBusiness.storeTask(task, iteration.getId(), null, null);
        
        assertNotNull(actualTask.getCreatedDate());
        assertEquals(loggedInUser, actualTask.getCreator());
        assertEquals(actualTask.getIteration(), iteration);
        
        verify(iterationBusiness, storyBusiness, taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testStoreTask_newTaskToStory() {

        task.setCreatedDate(null);
        task.setCreator(null);
        
        story.setBacklog(iteration);
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expect(taskDAO.create(task)).andReturn(1351);
        expect(taskDAO.get(1351)).andReturn(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replay(iterationBusiness, storyBusiness, taskDAO);
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertNotNull(actualTask.getCreatedDate());
        assertEquals(loggedInUser, actualTask.getCreator());
        assertEquals(actualTask.getStory(), story);
        
        verify(iterationBusiness, storyBusiness, taskDAO);
    }
    
    @Test
    public void testStoreTask_existingTask() {
        task.setId(54326);
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replay(iterationBusiness, storyBusiness, taskDAO, iterationHistoryEntryBusiness);
        
        Task actualTask =
            taskBusiness.storeTask(task, iteration.getId(), null, null);
        
        assertEquals(task.getId(), actualTask.getId());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verify(iterationBusiness, storyBusiness, taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testStoreTask_updateEffortLeftWhenSettingOriginalEstimate() {
        task.setId(12);
        task.setEffortLeft(null);
        task.setOriginalEstimate(new ExactEstimate(120));
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        taskDAO.store(task);
        
        replay(iterationBusiness, storyBusiness, taskDAO);
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertEquals(new ExactEstimate(120).getMinorUnits(), actualTask.getOriginalEstimate().getMinorUnits());
        assertEquals(new ExactEstimate(120).getMinorUnits(), actualTask.getEffortLeft().getMinorUnits());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verify(iterationBusiness, storyBusiness, taskDAO);
    }
    
    @Test
    public void testStoreTask_updateNullOriginalEstimateWhenSettingEffortLeft() {
        task.setId(12);
        task.setEffortLeft(new ExactEstimate(90));
        task.setOriginalEstimate(null);
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        taskDAO.store(task);
        
        replay(iterationBusiness, storyBusiness, taskDAO);
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertEquals(new ExactEstimate(90).getMinorUnits(), actualTask.getOriginalEstimate().getMinorUnits());
        assertEquals(new ExactEstimate(90).getMinorUnits(), actualTask.getEffortLeft().getMinorUnits());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verify(iterationBusiness, storyBusiness, taskDAO);
    }
    
    @Test
    public void testStoreTask_responsibles() {
        task.setId(123515);
        User user1 = new User();
        user1.setId(3);
        User user2 = new User();
        user2.setId(8);
        Set<Integer> userIdsSet = new HashSet<Integer>();
        userIdsSet.add(user1.getId());
        userIdsSet.add(user2.getId());
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        taskDAO.store(task);
        expect(userBusiness.retrieveIfExists(user1.getId())).andReturn(user1);
        expect(userBusiness.retrieveIfExists(user2.getId())).andReturn(user2);
        
        replay(iterationBusiness, storyBusiness, taskDAO, userBusiness);
        
        taskBusiness.storeTask(task, null, story.getId(), userIdsSet);
        
        assertEquals(userIdsSet.size(), task.getResponsibles().size());
        
        verify(iterationBusiness, storyBusiness, taskDAO, userBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStoreTask_nonExistentIteration() {
        expect(iterationBusiness.retrieve(0))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replay(iterationBusiness);
        
        taskBusiness.storeTask(task, 0, null, null);
        
        verify(iterationBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStoreTask_nonExistentStory() {
        expect(storyBusiness.retrieve(0))
            .andThrow(new ObjectNotFoundException("Story not found"));
        replay(storyBusiness);
        
        taskBusiness.storeTask(task, null, 0, null);
        
        verify(storyBusiness);
    }
    
    @Test
    public void testResetOriginalEstimate_underIteration() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        task.setIteration(iteration);
        task.setStory(null);
        expect(taskDAO.get(task.getId())).andReturn(task);
        taskDAO.store(task);
        
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replay(taskDAO, iterationHistoryEntryBusiness);
        
        Task returnedTask = taskBusiness.resetOriginalEstimate(task.getId());

        assertNull(returnedTask.getEffortLeft());
        assertNull(returnedTask.getOriginalEstimate());
        
        verify(taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testResetOriginalEstimate_underStory() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        task.setStory(story);
        story.setBacklog(iteration);
        task.setIteration(null);
        expect(taskDAO.get(task.getId())).andReturn(task);
        taskDAO.store(task);
        
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replay(taskDAO, iterationHistoryEntryBusiness);
        
        Task returnedTask = taskBusiness.resetOriginalEstimate(task.getId());

        assertNull(returnedTask.getEffortLeft());
        assertNull(returnedTask.getOriginalEstimate());
        
        verify(taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testResetOriginalEstimate_nonExistentTask() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        expect(taskDAO.get(task.getId())).andReturn(null);
        
        replay(taskDAO);
        
        taskBusiness.resetOriginalEstimate(task.getId());

        verify(taskDAO);
    }
    
    
    @Test
    public void testDelete_underIteration() {
        task.setStory(null);
        task.setIteration(iteration);
        
        taskDAO.remove(task.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        replay(taskDAO, iterationHistoryEntryBusiness);
        
        taskBusiness.delete(task);
        
        verify(taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testDelete_underIterationStory() {
        task.setStory(story);
        task.setIteration(null);
        story.setBacklog(iteration);
        
        taskDAO.remove(task.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        replay(taskDAO, iterationHistoryEntryBusiness);
        
        taskBusiness.delete(task);
        
        verify(taskDAO, iterationHistoryEntryBusiness);
    }
    
    @Test
    public void testDelete_underProductStory() {
        task.setStory(story);
        task.setIteration(null);
        story.setBacklog(new Product());
        
        taskDAO.remove(task.getId());
        replay(taskDAO, iterationHistoryEntryBusiness);
        
        taskBusiness.delete(task);
        
        verify(taskDAO, iterationHistoryEntryBusiness);    }

}
