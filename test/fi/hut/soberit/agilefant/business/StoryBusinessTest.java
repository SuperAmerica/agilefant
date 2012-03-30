package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.ChildHandlingChoice;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

public class StoryBusinessTest {

    StoryBusinessImpl storyBusiness;
    
    StoryDAO storyDAO;
    IterationDAO iterationDAO;
    UserDAO userDAO;
    IterationHistoryEntryBusiness iterationBusiness;
    
    BacklogBusiness backlogBusiness;
    BacklogHistoryEntryBusiness blheBusiness;
    IterationHistoryEntryBusiness iheBusiness;
    StoryRankBusiness storyRankBusiness;
    TransferObjectBusiness transferObjectBusiness;
    HourEntryDAO hourEntryDAO;
    TaskBusiness taskBusiness;
    HourEntryBusiness hourEntryBusiness;
    StoryHierarchyBusiness storyHierarchyBusiness;
    
    
    Backlog backlog;
    Iteration iteration;
    Story story1;
    Story story2;
    
    User assignedUser; 
    
    Story storyInIteration;
    Story storyInProject;
    Story storyInProduct;
    
    Boolean storyPriorityUpdated;
    Boolean storyBacklogUpdated;
    
    @Before
    public void setUp() {
        backlog = new Product();
        iteration = new Iteration();
        iteration.setId(5834);
        story1 = new Story();
        story1.setId(666);
        story2 = new Story();
        
        storyPriorityUpdated = false;
        storyBacklogUpdated = false;
    }
    
    @Before
    public void setUp_dependencies() {
        storyBusiness = new StoryBusinessImpl();
        
        backlogBusiness = createMock(BacklogBusiness.class);
        storyBusiness.setBacklogBusiness(backlogBusiness);
        
        storyDAO = createMock(StoryDAO.class);
        storyBusiness.setStoryDAO(storyDAO);
        
        iterationDAO = createMock(IterationDAO.class);
        storyBusiness.setIterationDAO(iterationDAO);

        userDAO = createMock(UserDAO.class);
        storyBusiness.setUserDAO(userDAO);
        
        blheBusiness = createMock(BacklogHistoryEntryBusiness.class);
        storyBusiness.setBacklogHistoryEntryBusiness(blheBusiness);
        
        iheBusiness = createMock(IterationHistoryEntryBusiness.class);
        storyBusiness.setIterationHistoryEntryBusiness(iheBusiness);
        
        storyRankBusiness = createMock(StoryRankBusiness.class);
        storyBusiness.setStoryRankBusiness(storyRankBusiness);
        
        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        storyBusiness.setTransferObjectBusiness(transferObjectBusiness);
        
        hourEntryDAO = createMock(HourEntryDAO.class);
        storyBusiness.setHourEntryDAO(hourEntryDAO);
        
        taskBusiness = createMock(TaskBusiness.class);
        storyBusiness.setTaskBusiness(taskBusiness);
        
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        storyBusiness.setHourEntryBusiness(hourEntryBusiness);
        
        storyHierarchyBusiness = createMock(StoryHierarchyBusiness.class);
        storyBusiness.setStoryHierarchyBusiness(storyHierarchyBusiness);
    }
    
    @Before
    public void setUpStorysProjectResponsiblesData() {
        Iteration iter = new Iteration();
        Project proj = new Project();
        Product prod = new Product();
        iter.setParent(proj);
        proj.setParent(prod);
        
        assignedUser = new User();
        assignedUser.setId(2233);
        
        storyInIteration = new Story();
        storyInProject = new Story();
        storyInProduct = new Story();
        
        storyInIteration.setId(868);
        storyInProduct.setId(951);
        storyInProject.setId(3);
        
        storyInIteration.setBacklog(iter);
        storyInProject.setBacklog(proj);
        storyInProduct.setBacklog(prod);
    }

    private void replayAll() {
        replay(backlogBusiness, storyDAO, iterationDAO, userDAO, iheBusiness, blheBusiness, transferObjectBusiness, hourEntryDAO, taskBusiness, hourEntryBusiness, storyRankBusiness, storyHierarchyBusiness);
    }
    
    private void verifyAll() {
        verify(backlogBusiness, storyDAO, iterationDAO, userDAO, iheBusiness, blheBusiness, transferObjectBusiness, hourEntryDAO, taskBusiness, hourEntryBusiness, storyRankBusiness, storyHierarchyBusiness);
    }

    
    private void store_createMockStoryBusiness() {       
        this.storyBusiness = new StoryBusinessImpl() {
            @Override
            public void moveStoryAway(Story story, Backlog backlog) {
                storyBacklogUpdated = true;
            }
            
//            @Override
//            public void updateStoryPriority(Story story, int insertAtPriority) {
//                storyPriorityUpdated = true;
//            }
        };
        backlogBusiness = createMock(BacklogBusiness.class);
        storyBusiness.setBacklogBusiness(backlogBusiness);
        
        storyDAO = createMock(StoryDAO.class);
        storyBusiness.setStoryDAO(storyDAO);
        
        userDAO = createMock(UserDAO.class);
        storyBusiness.setUserDAO(userDAO);
        
        iterationBusiness = createMock(IterationHistoryEntryBusiness.class);
        storyBusiness.setIterationHistoryEntryBusiness(iterationBusiness);
        
        blheBusiness = createMock(BacklogHistoryEntryBusiness.class);
        storyBusiness.setBacklogHistoryEntryBusiness(blheBusiness);
    }
    
    @Test
    public void testStore_updateResponsibles() {
        this.store_createMockStoryBusiness();
        
        Backlog backlog = storyInIteration.getBacklog();
        User user1 = new User();
        User user2 = new User();
        Set<User> users = new HashSet<User>(Arrays.asList(user1, user2));
        
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        expect(userDAO.get(123)).andReturn(user1);
        expect(userDAO.get(222)).andReturn(user2);
        storyDAO.store(EasyMock.isA(Story.class));
        
        Story dataItem = new Story();
        dataItem.setName("Foo item");
        dataItem.setDescription("Fubar");
        dataItem.setStoryPoints(333);
        dataItem.setState(StoryState.PENDING);
        
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        
        replayAll();
        Story actual = storyBusiness.store(storyInIteration.getId(),
                dataItem, null, new HashSet<Integer>(Arrays.asList(123, 222)), false);
        verifyAll();
        
        assertSame("The backlogs don't match", backlog, actual.getBacklog());
        assertEquals("The responsibles don't match", users, actual.getResponsibles());
        
        assertEquals(dataItem.getName(), actual.getName());
        assertEquals(dataItem.getDescription(), actual.getDescription());
        assertEquals(dataItem.getStoryPoints(), actual.getStoryPoints());
        assertEquals(dataItem.getState(), actual.getState());
        
        assertFalse(storyBacklogUpdated);
    }
    
    @Test
    public void testStore_tasksToDone() {
        Task task1 = new Task();
        task1.setId(11);
        task1.setState(TaskState.BLOCKED);
        
        Task task2 = new Task();
        task2.setId(12);
        task2.setState(TaskState.PENDING);
        
        story1.setBacklog(iteration);
        story1.setTasks(new HashSet<Task>(Arrays.asList(task1, task2)));
        
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        storyDAO.store(story1);
        
        taskBusiness.setTaskToDone(task1);
        taskBusiness.setTaskToDone(task2);
        iheBusiness.updateIterationHistory(story1.getBacklog().getId());
        
        blheBusiness.updateHistory(story1.getBacklog().getId());
        
        replayAll();
        storyBusiness.store(story1.getId(), story1, null, null, true);
        verifyAll();
    }
    
    @Test
    public void testStore_dontSetTasksToDone() {
        this.store_createMockStoryBusiness();
        Task task1 = new Task();
        task1.setId(11);
        task1.setState(TaskState.BLOCKED);
        
        Task task2 = new Task();
        task2.setId(12);
        task2.setState(TaskState.BLOCKED);
        
        story1.setBacklog(iteration);
        story1.setTasks(new HashSet<Task>(Arrays.asList(task1, task2)));
        
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        storyDAO.store(story1);
        blheBusiness.updateHistory(story1.getBacklog().getId());
        replayAll();
        Story actual = storyBusiness.store(story1.getId(), story1, null, null, false);
        verifyAll();
        
        for (Task t : actual.getTasks()) {
            assertEquals(TaskState.BLOCKED, t.getState());
        }
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testStore_nullStoryId() {
        this.store_createMockStoryBusiness();
        storyBusiness.store(null, new Story(), 123, new HashSet<Integer>(), false);
    }
    
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStore_noSuchStory() {
        this.store_createMockStoryBusiness();
        expect(storyDAO.get(222)).andReturn(null);
        replayAll();
        storyBusiness.store(222, new Story(), 123, new HashSet<Integer>(), false);
        verifyAll();
    }
    
    
    @Test
    public void testStore_updateBacklogAndClearResponsibles() {
        this.store_createMockStoryBusiness();
        Backlog newBacklog = new Project();
        newBacklog.setId(123);
        Set<User> users = new HashSet<User>(Arrays.asList(new User(), new User()));
        storyInIteration.setResponsibles(users);
        
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        expect(backlogBusiness.retrieve(newBacklog.getId())).andReturn(newBacklog);
        
        storyDAO.store(EasyMock.isA(Story.class));
        
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        
        replayAll();
        Story actual = storyBusiness.store(storyInIteration.getId(),
                new Story(), newBacklog.getId(), new HashSet<Integer>(), false);
        verifyAll();
        
        assertEquals(0, actual.getResponsibles().size());
        
        assertTrue(storyBacklogUpdated);
    }
    
    
 
    
    @Test(expected = OperationNotPermittedException.class)
    public void testDelete_withTasks() {
        Task task = new Task();
        story1.getTasks().add(task);
        storyBusiness.delete(story1);
    }
    
    @Test(expected = OperationNotPermittedException.class)
    public void testDelete_withHourEntries() {
        StoryHourEntry he = new StoryHourEntry();
        story1.getHourEntries().add(he);
        storyBusiness.delete(story1);
    }

    @Test
    public void testDeleteAndUpdateHistory() {
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        iheBusiness.updateIterationHistory(storyInIteration.getBacklog().getId());
        replayAll();
        
        storyBusiness.deleteAndUpdateHistory(storyInIteration.getId(), null, null, null, null);
        verifyAll();
    }
    
    @Test
    public void testDelete_deleteChoices_withChildren() {
        Story child = new Story();
        Story storyParent = new Story();
        storyParent.setBacklog(new Product());
        child.setParent(storyInIteration);
        storyInIteration.setParent(storyParent);
        storyInIteration.getChildren().add(child);

        storyHierarchyBusiness.updateChildrenTreeRanks(storyParent);
        hourEntryBusiness.deleteAll(storyInIteration.getHourEntries());
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        replayAll();
        storyBusiness.delete(storyInIteration,
                TaskHandlingChoice.DELETE,
                HourEntryHandlingChoice.DELETE,
                HourEntryHandlingChoice.DELETE,
                ChildHandlingChoice.MOVE);
        assertEquals(storyParent, child.getParent());
        assertTrue(storyInIteration.getChildren().isEmpty());
        verifyAll();
    }
    
    @Test
    public void testDelete_deleteChoices_withChildren_deleteChildren() {
        Story child = new Story();
        child.setBacklog(storyInIteration.getBacklog());
        child.setId(2333);
        child.setParent(storyInIteration);
        storyInIteration.getChildren().add(child);

        blheBusiness.updateHistory(child.getBacklog().getId());
        iheBusiness.updateIterationHistory(child.getBacklog().getId());
        
        hourEntryBusiness.deleteAll(child.getHourEntries());
        hourEntryBusiness.deleteAll(storyInIteration.getHourEntries());
        
//        storyRankBusiness.removeStoryRanks(child);
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        
//        expect(storyDAO.get(2333)).andReturn(child);
        
        storyDAO.remove(child.getId());
        storyDAO.remove(storyInIteration);
        
        replayAll();
        storyBusiness.delete(storyInIteration,
                TaskHandlingChoice.DELETE,
                HourEntryHandlingChoice.DELETE,
                HourEntryHandlingChoice.DELETE,
                ChildHandlingChoice.DELETE);
//        assertNull(child.getParent());
        assertTrue(storyInIteration.getChildren().isEmpty());
        verifyAll();
    }

    @Test
    public void testDelete_taskChoice_move() {
        Task task = new Task();
        storyInIteration.getTasks().add(task);
        expect(taskBusiness.move(task, storyInIteration.getBacklog().getId(), null)).andReturn(task);
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        replayAll();
        storyBusiness.delete(storyInIteration, TaskHandlingChoice.MOVE, null, null, null);
        verifyAll();
        assertTrue(storyInIteration.getTasks().isEmpty());
    }

    @Test
    public void testDelete_taskChoice_delete() {
        Task task = new Task();
        storyInIteration.getTasks().add(task);
        hourEntryBusiness.moveToBacklog(task.getHourEntries(), storyInIteration.getBacklog());
        taskBusiness.delete(task.getId(), HourEntryHandlingChoice.MOVE);
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        replayAll();
        storyBusiness.delete(storyInIteration, TaskHandlingChoice.DELETE, null, HourEntryHandlingChoice.MOVE, null);
        verifyAll();
        assertNull(task.getStory());
        assertTrue(storyInIteration.getTasks().isEmpty());
    }
    
    @Test
    public void testDelete_hourEntryChoice_move() {
        storyInIteration.getHourEntries().add(new StoryHourEntry());
        hourEntryBusiness.moveToBacklog(storyInIteration.getHourEntries(), storyInIteration.getBacklog());
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        replayAll();
        storyBusiness.delete(storyInIteration, null, HourEntryHandlingChoice.MOVE, null, null);
        verifyAll();
        assertTrue(storyInIteration.getHourEntries().isEmpty());
    }
    @Test
    public void testDelete_hourEntryChoice_delete() {
        storyInIteration.getHourEntries().add(new StoryHourEntry());
        hourEntryBusiness.deleteAll(storyInIteration.getHourEntries());
//        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        replayAll();
        storyBusiness.delete(storyInIteration, null, HourEntryHandlingChoice.DELETE, null, null);
        verifyAll();
        assertTrue(storyInIteration.getHourEntries().isEmpty());
    }
    
    @Test
    public void testDelete_onlyChildRemoved() {
        Story parent = new Story();
        Story child = new Story();
        Backlog backlog = new Project();
        backlog.setId(1);
        
        parent.getChildren().add(child);
        child.setParent(parent);
        child.setBacklog(backlog);
        parent.setBacklog(backlog);
        
        storyRankBusiness.rankToBottom(parent, backlog);
//        storyRankBusiness.removeStoryRanks(child);
        storyHierarchyBusiness.updateChildrenTreeRanks(parent);
        storyDAO.remove(child);
        replayAll();
        storyBusiness.delete(child, null, null, null, null);
        verifyAll();
        assertTrue(parent.getChildren().isEmpty());
        
    }

    @Test
    public void testRankStoryUnder() {
        Backlog blog = new Project();
        Story story = new Story();
        story.setBacklog(blog);
        Story ref = new Story();
        ref.setBacklog(blog);
        storyRankBusiness.rankBelow(story, blog, ref);
        replayAll();
        storyBusiness.rankStoryUnder(story, ref, blog);
        verifyAll();
    }
    
    @Test
    public void testRankStoryOver() {
        Backlog blog = new Project();
        Story story = new Story();
        story.setBacklog(blog);
        Story ref = new Story();
        ref.setBacklog(blog);
        storyRankBusiness.rankAbove(story, blog, ref);
        replayAll();
        storyBusiness.rankStoryOver(story, ref, blog);
        verifyAll();
    }

    @Test
    public void checksThatStorysIterationMatches_rankUnder() {
        Iteration ite = new Iteration();
        Story story = new Story();
        story.setIteration(ite);
        Story ref = new Story();
        ref.setIteration(ite);
        storyRankBusiness.rankBelow(story, ite, ref);
        replayAll();
        storyBusiness.rankStoryUnder(story, ref, null);
        verifyAll();
    }
    
    @Test
    public void checksThatStorysIterationMatches_rankOver() {
        Iteration ite = new Iteration();
        Story story = new Story();
        story.setIteration(ite);
        Story ref = new Story();
        ref.setIteration(ite);
        storyRankBusiness.rankAbove(story, ite, ref);
        replayAll();
        storyBusiness.rankStoryOver(story, ref, null);
        verifyAll();
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void testRankStoryUnder_null() {
        Backlog blog = new Iteration();
        Story story = null;
        Story ref = new Story();
        ref.setBacklog(blog);
        
        replayAll();
        storyBusiness.rankStoryUnder(story, ref, null);
        verifyAll();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRankStoryUnder_invalidbacklogs() {
        Project proj1 = new Project();
        Iteration iter = new Iteration();
        iter.setParent(proj1);
        
        Project proj2 = new Project();
        Iteration iter2 = new Iteration();
        iter2.setParent(proj2);
        
        Story story1 = new Story();
        story1.setBacklog(proj1);
        Story story2 = new Story();
        story2.setBacklog(proj2);
        
        replayAll();
        storyBusiness.rankStoryUnder(story1, story2, proj1);
        verifyAll();
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void testRankStory_underNull() {
        Backlog blog = new Iteration();
        Story story = new Story();
        story.setBacklog(blog);
        replayAll();
        storyBusiness.rankStoryUnder(story, null, null);
        verifyAll();
    }
    
    @Test
    public void testRankStoryToTop() {
        Story story = new Story();
        storyRankBusiness.rankToHead(story, backlog);
        replayAll();
        Story actual = storyBusiness.rankStoryToTop(story, backlog);
        verifyAll();
        assertSame(actual, story);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRankStoryToTop_noBacklog() {
        Story story = new Story();
        storyBusiness.rankStoryToTop(story, null);
    }
    
    @Test
    public void testRankStoryToBottom() {
        Story story = new Story();
        storyRankBusiness.rankToBottom(story, backlog);
        replayAll();
        Story actual = storyBusiness.rankStoryToBottom(story, backlog);
        verifyAll();
        assertSame(actual, story);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRankStoryToBottom_noBacklog() {
        Story story = new Story();
        storyBusiness.rankStoryToBottom(story, null);
    }
    
    @Test
    public void testRetrieveStoryTO() {
        StoryTO storyTo = new StoryTO(story1);
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        expect(transferObjectBusiness.constructStoryTO(story1)).andReturn(storyTo);
        expect(storyDAO.calculateMetrics(story1.getId())).andReturn(new StoryMetrics());
        expect(hourEntryDAO.calculateSumByStory(story1.getId())).andReturn(100l);
        replayAll();
        assertEquals(storyTo, storyBusiness.retrieveStoryWithMetrics(story1.getId()));
        verifyAll();
    }
    
    @Test
    public void testUpdateRanks_noChildren_HasRank() {
        Story story = new Story();
        Project backlog = new Project();
        story.setBacklog(backlog);
        StoryRank rank = new StoryRank();
        story.getStoryRanks().add(rank);
        replayAll();
        storyBusiness.updateStoryRanks(story);
        verifyAll();
    }
    
    @Test
    public void testUpdateRanks_noChildren_NoRank() {
        Story story = new Story();
        Project backlog = new Project();
        story.setBacklog(backlog);
        storyRankBusiness.rankToBottom(story, backlog);
        replayAll();
        storyBusiness.updateStoryRanks(story);
        verifyAll();
    }
    
    @Test
    public void testUpdateRanks_children_HasRank() {
        Story story = new Story();
        Project backlog = new Project();
        story.setBacklog(backlog);
        StoryRank rank = new StoryRank();
        story.getStoryRanks().add(rank);
        Story child = new Story();
        story.getChildren().add(child);
        
        storyRankBusiness.removeStoryRanks(story);
        replayAll();
        storyBusiness.updateStoryRanks(story);
        verifyAll();
    }
    
    
    /**
     * TEST FORCE DELETING
     */
    
    @Test
    public void testForceDelete() {
        Story story = new Story();
        story.setId(1);
        
        Story child = new Story();
        child.setParent(story);
        story.setChildren(new ArrayList<Story>(Arrays.asList(child)));
        
        story.setTasks(new HashSet<Task>(Arrays.asList(new Task(), new Task())));
        story.setHourEntries(new HashSet<StoryHourEntry>(Arrays.asList(new StoryHourEntry(), new StoryHourEntry(), new StoryHourEntry())));
        
        taskBusiness.delete(EasyMock.isA(Task.class), EasyMock.same(HourEntryHandlingChoice.DELETE));
        expectLastCall().times(2);
        
        hourEntryBusiness.deleteAll(story.getHourEntries());
        
        storyDAO.remove(1);
        
        replayAll();
        storyBusiness.forceDelete(story);
        verifyAll();
        
        assertNull("Child story's parent not null", child.getParent());
        assertEquals("Parent story's children not empty", 0, story.getChildren().size());
    }

    
    
    
    
    
    
    
}
