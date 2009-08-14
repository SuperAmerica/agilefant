package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.Arrays;
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
import fi.hut.soberit.agilefant.model.Project;
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
        
        iterationBusiness = createStrictMock(IterationBusiness.class);
        taskBusiness.setIterationBusiness(iterationBusiness);
        
        storyBusiness = createStrictMock(StoryBusiness.class);
        taskBusiness.setStoryBusiness(storyBusiness);
        
        userBusiness = createStrictMock(UserBusiness.class);
        taskBusiness.setUserBusiness(userBusiness);
        
        iterationHistoryEntryBusiness = createStrictMock(IterationHistoryEntryBusiness.class);
        taskBusiness.setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);    
    }
    
    private void replayAll() {
        replay(taskDAO, iterationBusiness, storyBusiness, userBusiness, iterationHistoryEntryBusiness);
    }
    
    private void verifyAll() {
        verify(taskDAO, iterationBusiness, storyBusiness, userBusiness, iterationHistoryEntryBusiness);
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
    
    
    /*
     * TEST STORING.
     */
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreTask_storyAndIterationGiven() {
        taskBusiness.storeTask(task, iteration.getId(), story.getId(), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreTask_nullTask() {
        taskBusiness.storeTask(null, iteration.getId(), null, null);
    }
    
    @Test
    public void testStoreTask_newTaskToIteration() {
        task.setCreatedDate(null);
        task.setCreator(null);
        
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        expect(taskDAO.create(task)).andReturn(1351);
        expect(taskDAO.get(1351)).andReturn(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replayAll();
        
        Task actualTask = taskBusiness.storeTask(task, iteration.getId(), null, null);
        
        assertNotNull(actualTask.getCreatedDate());
        assertEquals(loggedInUser, actualTask.getCreator());
        assertEquals(actualTask.getIteration(), iteration);
        
        verifyAll();
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
        
        replayAll();
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertNotNull(actualTask.getCreatedDate());
        assertEquals(loggedInUser, actualTask.getCreator());
        assertEquals(actualTask.getStory(), story);
        
        verifyAll();
    }
    
    @Test
    public void testStoreTask_existingTask() {
        task.setId(54326);
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replayAll();
        
        Task actualTask =
            taskBusiness.storeTask(task, iteration.getId(), null, null);
        
        assertEquals(task.getId(), actualTask.getId());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verifyAll();
    }
    
    @Test
    public void testStoreTask_updateEffortLeftWhenSettingOriginalEstimate() {
        task.setId(12);
        task.setEffortLeft(null);
        task.setOriginalEstimate(new ExactEstimate(120));
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        taskDAO.store(task);
        
        replayAll();
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertEquals(new ExactEstimate(120).getMinorUnits(), actualTask.getOriginalEstimate().getMinorUnits());
        assertEquals(new ExactEstimate(120).getMinorUnits(), actualTask.getEffortLeft().getMinorUnits());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verifyAll();
    }
    
    @Test
    public void testStoreTask_updateNullOriginalEstimateWhenSettingEffortLeft() {
        task.setId(12);
        task.setEffortLeft(new ExactEstimate(90));
        task.setOriginalEstimate(null);
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        taskDAO.store(task);
        
        replayAll();
        
        Task actualTask = taskBusiness.storeTask(task, null, story.getId(), null);
        
        assertEquals(new ExactEstimate(90).getMinorUnits(), actualTask.getOriginalEstimate().getMinorUnits());
        assertEquals(new ExactEstimate(90).getMinorUnits(), actualTask.getEffortLeft().getMinorUnits());
        assertEquals(createdDate.toDate(), actualTask.getCreatedDate());
        assertEquals(creatorUser, actualTask.getCreator());
        
        verifyAll();
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
        
        replayAll();
        
        taskBusiness.storeTask(task, null, story.getId(), userIdsSet);
        
        assertEquals(userIdsSet.size(), task.getResponsibles().size());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStoreTask_nonExistentIteration() {
        expect(iterationBusiness.retrieve(0))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replayAll();
        
        taskBusiness.storeTask(task, 0, null, null);
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStoreTask_nonExistentStory() {
        expect(storyBusiness.retrieve(0))
            .andThrow(new ObjectNotFoundException("Story not found"));
        replayAll();
        
        taskBusiness.storeTask(task, null, 0, null);
        
        verifyAll();
    }
    
    /*
     * TEST ORIGINAL ESTIMATE RESETING
     */
    
    @Test
    public void testResetOriginalEstimate_underIteration() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        task.setIteration(iteration);
        task.setStory(null);
        expect(taskDAO.get(task.getId())).andReturn(task);
        taskDAO.store(task);
        
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        
        replayAll();
        
        Task returnedTask = taskBusiness.resetOriginalEstimate(task.getId());

        assertNull(returnedTask.getEffortLeft());
        assertNull(returnedTask.getOriginalEstimate());
        
        verifyAll();
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
        
        replayAll();
        
        Task returnedTask = taskBusiness.resetOriginalEstimate(task.getId());

        assertNull(returnedTask.getEffortLeft());
        assertNull(returnedTask.getOriginalEstimate());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testResetOriginalEstimate_nonExistentTask() {
        task.setEffortLeft(new ExactEstimate());
        task.setOriginalEstimate(new ExactEstimate());
        expect(taskDAO.get(task.getId())).andReturn(null);
        
        replayAll();
        
        taskBusiness.resetOriginalEstimate(task.getId());

        verifyAll();
    }
    
    /*
     * TEST DELETION
     */
    
    @Test
    public void testDelete_underIteration() {
        task.setStory(null);
        task.setIteration(iteration);
        
        taskDAO.remove(task.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        replayAll();
        
        taskBusiness.delete(task);
        
        verifyAll();
    }
    
    @Test
    public void testDelete_underIterationStory() {
        task.setStory(story);
        task.setIteration(null);
        story.setBacklog(iteration);
        
        taskDAO.remove(task.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(iteration.getId());
        replayAll();
        
        taskBusiness.delete(task);
        
        verifyAll();
    }
    
    @Test
    public void testDelete_underProductStory() {
        task.setStory(story);
        task.setIteration(null);
        story.setBacklog(new Product());
        
        taskDAO.remove(task.getId());
        replayAll();
        
        taskBusiness.delete(task);
        
        verifyAll();
    }
    
    /*
     * TEST ASSIGNING PARENT 
     */
    
    @Test(expected = IllegalArgumentException.class)
    public void testAssignParentForTask_nullTask() {
        taskBusiness.assignParentForTask(null, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAssignParentForTask_bothIdsNull() {
        taskBusiness.assignParentForTask(task, null, null);
    }
    
    @Test
    public void testAssignParentForTask_storyIdGiven() {       
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        replayAll();
        
        taskBusiness.assignParentForTask(task, null, story.getId());
        
        assertEquals(story, task.getStory());
        assertNull(task.getIteration());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAssignParentForTask_nonExistentStoryIdGiven() {
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskBusiness.assignParentForTask(task, null, -1);
        
        verifyAll();
    }
    
    @Test
    public void testAssignParentForTask_iterationIdGiven() {
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(iteration);
        replayAll();
        
        taskBusiness.assignParentForTask(task, iteration.getId(), null);
        
        assertEquals(iteration, task.getIteration());
        assertNull(task.getStory());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAssignParentForTask_nonExistentIterationIdGiven() {
        expect(iterationBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        
        taskBusiness.assignParentForTask(task, -1, null);
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAssignParentForTask_backlogNotIteration() {
        expect(iterationBusiness.retrieve(123)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskBusiness.assignParentForTask(task, 123, null);
        
        verifyAll();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testAssignParentForTask_bothIdsGiven() {
        taskBusiness.assignParentForTask(task, 123, 456);
    }
    
    /*
     * TEST MOVING TASKS.
     * 
     * Moving tasks should update both the source and destination
     * backlogs' histories.
     */
       
    @Test
    public void testMove_fromIterationToIteration() {
        Iteration source = new Iteration();
        source.setId(222);
        Iteration destination = new Iteration();
        destination.setId(666);
        
        task.setIteration(source);
        
        expect(iterationBusiness.retrieve(destination.getId())).andReturn(destination);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(source.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(destination.getId());
        
        replayAll();
        
        taskBusiness.move(task, destination.getId(), null);
        
        assertEquals(destination, task.getIteration());
        assertNull(task.getStory());
        
        verifyAll();
    }
    
    @Test
    public void testMove_fromIterationStoryToIterationStory() {
        Iteration iter = new Iteration();
        iter.setId(654);
        Iteration iter2 = new Iteration();
        iter2.setId(999);
        
        Story source = new Story();
        source.setId(397);
        source.setBacklog(iter);
        
        Story destination = new Story();
        destination.setId(1223);
        destination.setBacklog(iter2);
        
        task.setStory(source);
        
        expect(storyBusiness.retrieve(destination.getId())).andReturn(destination);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iter.getId());
        iterationHistoryEntryBusiness.updateIterationHistory(iter2.getId());
        
        replayAll();
        
        taskBusiness.move(task, null, destination.getId());
        
        assertEquals(destination, task.getStory());
        assertNull(task.getIteration());
        
        verifyAll();
    }
    
    @Test
    public void testMove_fromProjectStoryToIterationStory() {
        Project proj = new Project();
        proj.setId(654);
        Iteration iter2 = new Iteration();
        iter2.setId(999);
        
        Story source = new Story();
        source.setId(397);
        source.setBacklog(proj);
        
        Story destination = new Story();
        destination.setId(1223);
        destination.setBacklog(iter2);
        
        task.setStory(source);
        
        expect(storyBusiness.retrieve(destination.getId())).andReturn(destination);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iter2.getId());
        
        replayAll();
        
        taskBusiness.move(task, null, destination.getId());
        
        assertEquals(destination, task.getStory());
        assertNull(task.getIteration());
        
        verifyAll();  
    }
    
    @Test
    public void testMove_fromIterationToProject() {
        Project proj = new Project();
        proj.setId(654);
        Iteration iter = new Iteration();
        iter.setId(999);
        
        Story source = new Story();
        source.setId(397);
        source.setBacklog(iter);
        
        Story destination = new Story();
        destination.setId(1223);
        destination.setBacklog(proj);
        
        task.setStory(source);
        
        expect(storyBusiness.retrieve(destination.getId())).andReturn(destination);
        taskDAO.store(task);
        iterationHistoryEntryBusiness.updateIterationHistory(iter.getId());
        
        replayAll();
        
        taskBusiness.move(task, null, destination.getId());
        
        assertEquals(destination, task.getStory());
        assertNull(task.getIteration());
        
        verifyAll();  
    }
    
    @Test
    public void testMove_toStoryUnderSameIteration() {
        Iteration iter = new Iteration();
        
        Story source = new Story();
        source.setId(397);
        source.setBacklog(iter);
        
        Story destination = new Story();
        destination.setId(1223);
        destination.setBacklog(iter);
        
        task.setStory(source);
        
        expect(storyBusiness.retrieve(destination.getId())).andReturn(destination);
        taskDAO.store(task);
        replayAll();
        
        taskBusiness.move(task, null, destination.getId());
        
        assertEquals(destination, task.getStory());
        assertNull(task.getIteration());
        
        verifyAll();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMove_nullTask() {
        taskBusiness.move(null, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMove_bothParentIdsNull() {
        taskBusiness.move(task, null, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMove_bothIdsGiven() {
        taskBusiness.move(task, 123, 456);
    }
    
    
    /*
     * RANKING 
     */
    
    Story rankParentStory;
    
    Task firstTaskInRank;
    Task secondTaskInRank;
    Task thirdTaskInRank;
    Task fourthTaskInRank;
    
    @Before
    public void setUp_ranking() {
        firstTaskInRank = new Task();
        secondTaskInRank = new Task();
        thirdTaskInRank = new Task();
        fourthTaskInRank = new Task();
        
        firstTaskInRank.setId(111);
        secondTaskInRank.setId(222);
        thirdTaskInRank.setId(333);
        fourthTaskInRank.setId(444);
        
        firstTaskInRank.setRank(0);
        secondTaskInRank.setRank(1);
        thirdTaskInRank.setRank(5);
        fourthTaskInRank.setRank(6);

        rankParentStory = new Story();
        rankParentStory.setId(22);
        
        firstTaskInRank.setStory(rankParentStory);
        secondTaskInRank.setStory(rankParentStory);
        thirdTaskInRank.setStory(rankParentStory);
        fourthTaskInRank.setStory(rankParentStory);
    }
    
    private void checkRanks(int first, int second, int third, int fourth) {
        assertEquals("First rank does not match", first, firstTaskInRank.getRank());
        assertEquals("Second rank does not match", second, secondTaskInRank.getRank());
        assertEquals("Third rank does not match", third, thirdTaskInRank.getRank());
        assertEquals("Fourth rank does not match", fourth, fourthTaskInRank.getRank());
    }
    
    @Test
    public void testRankUnderTask_twoUpwards() {
        expect(taskDAO.getTasksWithRankBetween(fourthTaskInRank.getStory(), 1, 5))
            .andReturn(Arrays.asList(secondTaskInRank, thirdTaskInRank));
        replayAll();
        Task actual = taskBusiness.rankUnderTask(fourthTaskInRank, firstTaskInRank);
        verifyAll();
        checkRanks(0, 2, 6, 1);
        assertSame(fourthTaskInRank, actual);
    }
    
    @Test
    public void testRankUnderTask_twoDownwards() {
        expect(taskDAO.getTasksWithRankBetween(firstTaskInRank.getStory(), 1, 5))
            .andReturn(Arrays.asList(secondTaskInRank, thirdTaskInRank));
        replayAll();
        taskBusiness.rankUnderTask(firstTaskInRank, thirdTaskInRank);
        verifyAll();
        checkRanks(5, 0, 4, 6);
    }
    
    @Test
    public void testRankUnderTask_toTop() {
        expect(taskDAO.getTasksWithRankBetween(firstTaskInRank.getStory(), 0, 4))
            .andReturn(Arrays.asList(firstTaskInRank, secondTaskInRank, thirdTaskInRank));
        replayAll();
        taskBusiness.rankUnderTask(thirdTaskInRank, null);
        verifyAll();
        checkRanks(1, 2, 0, 6);
    }
    
    @Test
    public void testRankUnderTask_toBottom() {
        expect(taskDAO.getTasksWithRankBetween(firstTaskInRank.getStory(), 1, 6))
            .andReturn(Arrays.asList(secondTaskInRank, thirdTaskInRank, fourthTaskInRank));
        replayAll();
        taskBusiness.rankUnderTask(firstTaskInRank, fourthTaskInRank);
        verifyAll();
        checkRanks(6, 0, 4, 5);
    }
       
    @Test(expected = IllegalArgumentException.class)
    public void testRankUnderTask_nullTaskGiven() {
        replayAll();
        taskBusiness.rankUnderTask(null, null);
        verifyAll();
    }
    
    @Test
    public void testRankUnderTask_rankUnderSelf() {
        Task switchUnder = new Task();
        switchUnder.setRank(4);
        expect(taskDAO.getNextTaskInRank(secondTaskInRank.getStory(), secondTaskInRank.getRank()))
            .andReturn(switchUnder);
        
        replayAll();
        Task actual = taskBusiness.rankUnderTask(secondTaskInRank, secondTaskInRank);
        verifyAll();
        
        assertEquals(1, switchUnder.getRank());
        assertEquals(4, actual.getRank());
    }
    
    @Test
    public void testRankUnderTask_checkUnderSameIteration() {
        Task first = new Task();
        Task second = new Task();
        Iteration iter = new Iteration();
        
        first.setIteration(iter);
        second.setIteration(iter);
        
        expect(taskDAO.getTasksWithRankBetween(iter, 1, -1)).andReturn(Arrays.asList(new Task()));        
        replayAll();
        taskBusiness.rankUnderTask(first, second);
        verifyAll();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRankUnderTask_noStory_differentIteration() {
        Task first = new Task();
        Task second = new Task();
       
        first.setIteration(new Iteration());
        second.setIteration(new Iteration());
        
        replayAll();
        taskBusiness.rankUnderTask(first, second);
        verifyAll();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRankUnderTask_tasksNotUnderSameStory() {
        Task newTask = new Task();
        newTask.setStory(new Story());
        
        replayAll();
        taskBusiness.rankUnderTask(firstTaskInRank, newTask);
        verifyAll();
    }

   
}
