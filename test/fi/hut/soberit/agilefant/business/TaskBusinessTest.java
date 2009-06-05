package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.*;

import fi.hut.soberit.agilefant.business.impl.TaskBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import static org.junit.Assert.*;

public class TaskBusinessTest {
    
    private TaskBusinessImpl taskBusiness = new TaskBusinessImpl() {
        // Overrided to skip static call to SecurityUtil
        @Override
        public User getLoggedInUser() {
            return user;
        }
    };
    private IterationBusiness iterationBusiness;
    private StoryBusiness storyBusiness;
    private UserBusiness userBusiness;
    private TaskDAO taskDAO;
    
    private Iteration iteration;
    private Story story;
    private Task task;
    private User user;
   
    @Before
    public void setUp() {
        taskDAO = createMock(TaskDAO.class);
        taskBusiness.setTaskDAO(taskDAO);
        iterationBusiness = createMock(IterationBusiness.class);
        taskBusiness.setIterationBusiness(iterationBusiness);
        storyBusiness = createMock(StoryBusiness.class);
        taskBusiness.setStoryBusiness(storyBusiness);
        userBusiness = createMock(UserBusiness.class);
        taskBusiness.setUserBusiness(userBusiness);
        
        task = new Task();
        iteration = new Iteration();
        iteration.setId(2);
        story = new Story();
        story.setId(123);
        task.setId(0);
        
        user = new User();
        user.setId(666);
    }
    
    @Test
    public void testStoreTask_newTask() {
        
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        expect(storyBusiness.retrieveIfExists(story.getId())).andReturn(story);
        expect(taskDAO.create(task)).andReturn(1351);
        expect(taskDAO.get(1351)).andReturn(task);
        
        replay(iterationBusiness, storyBusiness, taskDAO);
        
        Task actualTask = taskBusiness.storeTask(task, iteration.getId(), story.getId(), null);
        
        assertNotNull(actualTask.getCreatedDate());
        assertEquals(user, actualTask.getCreator());
        
        verify(iterationBusiness, storyBusiness, taskDAO);
    }
    
    @Test
    public void testStoreTask_existingTask() {
        task.setId(54326);
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        expect(storyBusiness.retrieveIfExists(story.getId())).andReturn(story);
        taskDAO.store(task);
        
        replay(iterationBusiness, storyBusiness, taskDAO);
        
        assertSame(task,
                taskBusiness.storeTask(task, iteration.getId(), story.getId(), null));
        
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
        
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        expect(storyBusiness.retrieveIfExists(story.getId())).andReturn(story);
        taskDAO.store(task);
        expect(userBusiness.retrieveIfExists(user1.getId())).andReturn(user1);
        expect(userBusiness.retrieveIfExists(user2.getId())).andReturn(user2);
        
        replay(iterationBusiness, storyBusiness, taskDAO, userBusiness);
        
        taskBusiness.storeTask(task, iteration.getId(), story.getId(), userIdsSet);
        
        assertEquals(userIdsSet.size(), task.getResponsibles().size());
        
        verify(iterationBusiness, storyBusiness, taskDAO, userBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStoreTask_nonExistentIteration() {
        expect(iterationBusiness.retrieve(0))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replay(iterationBusiness);
        
        taskBusiness.storeTask(task, 0, 0, null);
        
        verify(iterationBusiness);
    }
    
    @Test
    public void testResetOriginalEstimate() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        expect(taskDAO.get(task.getId())).andReturn(task);
        
        replay(taskDAO);
        
        Task returnedTask = taskBusiness.resetOriginalEstimate(task.getId());

        assertNull(returnedTask.getEffortLeft());
        assertNull(returnedTask.getOriginalEstimate());
        
        verify(taskDAO);
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

}
