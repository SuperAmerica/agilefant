package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

public class StoryBusinessTest {

    StoryBusinessImpl storyBusiness;
    
    StoryDAO storyDAO;
    IterationDAO iterationDAO;
    UserDAO userDAO;
    
    BacklogBusiness backlogBusiness;
    ProjectBusiness projectBusiness;
    BacklogHistoryEntryBusiness blheBusiness;
    IterationHistoryEntryBusiness iheBusiness;
    StoryRankBusiness storyRankBusiness;
    TransferObjectBusiness transferObjectBusiness;
    HourEntryDAO hourEntryDAO;
    TaskBusiness taskBusiness;
    HourEntryBusiness hourEntryBusiness;
    
    
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
        
        projectBusiness = createMock(ProjectBusiness.class);
        storyBusiness.setProjectBusiness(projectBusiness);
        
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
        replay(backlogBusiness, storyDAO, iterationDAO, userDAO, projectBusiness, iheBusiness, blheBusiness, transferObjectBusiness, hourEntryDAO, taskBusiness, hourEntryBusiness, storyRankBusiness);
    }
    
    private void verifyAll() {
        verify(backlogBusiness, storyDAO, iterationDAO, userDAO, projectBusiness, iheBusiness, blheBusiness, transferObjectBusiness, hourEntryDAO, taskBusiness, hourEntryBusiness, storyRankBusiness);
    }

    
    @Test
    public void testGetStoriesByBacklog() {
        List<Story> storiesList = Arrays.asList(story1, story2);
        expect(storyDAO.getStoriesByBacklog(backlog)).andReturn(storiesList);
        replayAll();
        
        assertSame(storiesList, storyBusiness.getStoriesByBacklog(backlog));
        
        verifyAll();
    }
       
    @Test
    public void testGetStoryContents_delegate() {
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        expect(iterationDAO.get(iteration.getId()));
    }
    
    @Test
    public void testGetStoryContents() {
        Task task1 = new Task();
        Task task2 = new Task();
        task2.setStory(story1);
        story1.setBacklog(iteration);
        expect(iterationDAO.getAllTasksForIteration(iteration))
            .andReturn(Arrays.asList(task1, task2));
        replayAll();
        assertTrue(storyBusiness.getStoryContents(story1, iteration)
                .contains(task2));
        verifyAll();
    }
    

    @Test
    public void testGetStorysProjectResponsibles_iteration() {
        Collection<User> assignedUsers = Arrays.asList(assignedUser);
        expect(projectBusiness.getAssignedUsers((Project)storyInIteration.getBacklog().getParent()))
            .andReturn(assignedUsers);
        replayAll();
        
        assertEquals(assignedUsers, storyBusiness.getStorysProjectResponsibles(storyInIteration));
        
        verifyAll();
    }
    
    @Test
    public void testGetStorysProjectResponsibles_project() {
        Collection<User> assignedUsers = Arrays.asList(assignedUser);
        expect(projectBusiness.getAssignedUsers((Project)storyInProject.getBacklog()))
            .andReturn(Arrays.asList(assignedUser));
        replayAll();
        
        assertEquals(assignedUsers, storyBusiness.getStorysProjectResponsibles(storyInProject));
        
        verifyAll();
    }
    
    @Test
    public void testGetStorysProjectResponsibles_product() {
        replayAll();       
        assertEquals(0, storyBusiness.getStorysProjectResponsibles(storyInProduct).size());
        verifyAll();
    }

    
    @Test
    public void testGetStoryPointSumByBacklog() {
        expect(storyDAO.getStoryPointSumByBacklog(backlog.getId()))
            .andReturn(6);
        replayAll();
        
        assertEquals(6, storyBusiness.getStoryPointSumByBacklog(backlog));
        
        verifyAll();
    }
    

    
    
    private void store_createMockStoryBusiness() {       
        this.storyBusiness = new StoryBusinessImpl() {
            @Override
            public void moveStoryToBacklog(Story story, Backlog backlog) {
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
                dataItem, null, new HashSet<Integer>(Arrays.asList(123, 222)));
        verifyAll();
        
        assertSame("The backlogs don't match", backlog, actual.getBacklog());
        assertEquals("The responsibles don't match", users, actual.getResponsibles());
        
        assertEquals(dataItem.getName(), actual.getName());
        assertEquals(dataItem.getDescription(), actual.getDescription());
        assertEquals(dataItem.getStoryPoints(), actual.getStoryPoints());
        assertEquals(dataItem.getState(), actual.getState());
        
        assertFalse(storyBacklogUpdated);
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testStore_nullStoryId() {
        this.store_createMockStoryBusiness();
        storyBusiness.store(null, new Story(), 123, new HashSet<Integer>());
    }
    
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStore_noSuchStory() {
        this.store_createMockStoryBusiness();
        expect(storyDAO.get(222)).andReturn(null);
        replayAll();
        storyBusiness.store(222, new Story(), 123, new HashSet<Integer>());
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
                new Story(), newBacklog.getId(), new HashSet<Integer>());
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
    public void testDelete_taskChoice_move() {
        Task task = new Task();
        storyInIteration.getTasks().add(task);
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        expect(taskBusiness.move(task, storyInIteration.getBacklog().getId(), null)).andReturn(task);
        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        replayAll();
        storyBusiness.delete(storyInIteration.getId(), TaskHandlingChoice.MOVE, null, null);
        verifyAll();
        assertTrue(storyInIteration.getTasks().isEmpty());
    }

    @Test
    public void testDelete_taskChoice_delete() {
        Task task = new Task();
        storyInIteration.getTasks().add(task);
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        hourEntryBusiness.moveToBacklog(task.getHourEntries(), storyInIteration.getBacklog());
        taskBusiness.delete(task.getId(), HourEntryHandlingChoice.MOVE);
        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        replayAll();
        storyBusiness.delete(storyInIteration.getId(), TaskHandlingChoice.DELETE, null, HourEntryHandlingChoice.MOVE);
        verifyAll();
        assertNull(task.getStory());
        assertTrue(storyInIteration.getTasks().isEmpty());
    }
    
    @Test
    public void testDelete_hourEntryChoice_move() {
        storyInIteration.getHourEntries().add(new StoryHourEntry());
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        hourEntryBusiness.moveToBacklog(storyInIteration.getHourEntries(), storyInIteration.getBacklog());
        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        replayAll();
        storyBusiness.delete(storyInIteration.getId(), null, HourEntryHandlingChoice.MOVE, null);
        verifyAll();
        assertTrue(storyInIteration.getHourEntries().isEmpty());
    }
    @Test
    public void testDelete_hourEntryChoice_delete() {
        storyInIteration.getHourEntries().add(new StoryHourEntry());
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        hourEntryBusiness.deleteAll(storyInIteration.getHourEntries());
        storyRankBusiness.removeStoryRanks(storyInIteration);
        storyDAO.remove(storyInIteration);
        blheBusiness.updateHistory(storyInIteration.getBacklog().getId());
        replayAll();
        storyBusiness.delete(storyInIteration.getId(), null, HourEntryHandlingChoice.DELETE, null);
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
        storyRankBusiness.removeStoryRanks(child);
        storyDAO.remove(child);
        blheBusiness.updateHistory(child.getBacklog().getId());

        replayAll();
        storyBusiness.delete(child);
        verifyAll();
        assertTrue(parent.getChildren().isEmpty());
        
    }

    @Test
    public void testRankStoryUnder() {
        Backlog blog = new Iteration();
        Story story = new Story();
        story.setBacklog(blog);
        Story ref = new Story();
        ref.setBacklog(blog);
        storyRankBusiness.rankBelow(story, blog, ref);
        replayAll();
        storyBusiness.rankStoryUnder(story, ref, null);
        verifyAll();
    }
    
    @Test
    public void testRankStoryOver() {
        Backlog blog = new Iteration();
        Story story = new Story();
        story.setBacklog(blog);
        Story ref = new Story();
        ref.setBacklog(blog);
        storyRankBusiness.rankAbove(story, blog, ref);
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
        Project proj2 = new Project();
        Iteration iter = new Iteration();
        iter.setParent(proj1);
        Iteration blog = new Iteration();
        blog.setParent(proj2);
        Story story = new Story();
        story.setBacklog(blog);
        Story ref = new Story();
        ref.setBacklog(iter);
        
        replayAll();
        storyBusiness.rankStoryUnder(story, ref, null);
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
    public void testStoreBatch() {
        Story s1 = new Story();
        s1.setId(1);
        Story s2 = new Story();
        s2.setId(2);
        Collection<Story> batch = Arrays.asList(s1,s2);
        storyDAO.store(s1);
        storyDAO.store(s2);
        replayAll();
        storyBusiness.storeBatch(batch);
        verifyAll();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testStoreBatch_nonPersited() {
        Story s1 = new Story();
        Story s2 = new Story();
        Collection<Story> batch = Arrays.asList(s1,s2);
        storyDAO.store(s1);
        storyDAO.store(s2);
        replayAll();
        storyBusiness.storeBatch(batch);
        verifyAll();
    }
    
    @Test
    public void testRetrieveStoryTO() {
        StoryTO storyTo = new StoryTO(story1);
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        expect(transferObjectBusiness.constructStoryTO(story1)).andReturn(storyTo);
        expect(hourEntryDAO.calculateSumByStory(story1.getId())).andReturn(100l);
        replayAll();
        assertEquals(storyTo, storyBusiness.retrieveStoryWithMetrics(story1.getId()));
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
}
