package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

public class StoryBusinessTest {

    StoryBusinessImpl storyBusiness = new StoryBusinessImpl();
    StoryDAO storyDAO;
    IterationDAO iterationDAO;
    BacklogDAO backlogDAO;
    UserDAO userDAO;
    ProjectBusiness projectBusiness;
    
    Backlog backlog;
    Iteration iteration;
    Story story1;
    Story story2;
    
    User assignedUser; 
    
    Story storyInIteration;
    Story storyInProject;
    Story storyInProduct;
    
    @Before
    public void setUp() {
        backlog = new Product();
        iteration = new Iteration();
        iteration.setId(5834);
        story1 = new Story();
        story1.setId(666);
        story2 = new Story();
    }
    
    @Before
    public void setUp_dependencies() {
        backlogDAO = createMock(BacklogDAO.class);
        storyBusiness.setBacklogDAO(backlogDAO);
        
        storyDAO = createMock(StoryDAO.class);
        storyBusiness.setStoryDAO(storyDAO);
        
        iterationDAO = createMock(IterationDAO.class);
        storyBusiness.setIterationDAO(iterationDAO);

        userDAO = createMock(UserDAO.class);
        storyBusiness.setUserDAO(userDAO);
        
        projectBusiness = createMock(ProjectBusiness.class);
        storyBusiness.setProjectBusiness(projectBusiness);
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
        replay(backlogDAO, storyDAO, iterationDAO, userDAO, projectBusiness);
    }
    
    private void verifyAll() {
        verify(backlogDAO, storyDAO, iterationDAO, userDAO, projectBusiness);
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
    public void testGetStoryResponsibles() {
        User user = new User();
        story1.getResponsibles().add(user);
        ResponsibleContainer respCont = new ResponsibleContainer(user, true);
        Collection<ResponsibleContainer> responsibles = Arrays.asList(respCont);
        
        assertEquals(responsibles.size(), storyBusiness.getStoryResponsibles(story1).size());
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
    
    public void testUpdateStoryPriority_noUpdate() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        storyBusiness.updateStoryPriority(stories.get(4), 4);
    }
    
    public void testUpdateStoryPriority_oneUp() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        replayAll();
        storyBusiness.updateStoryPriority(stories.get(4), 3);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(3, stories.get(4).getPriority());
        assertSame(5, stories.get(5).getPriority());
        verifyAll();
    }
    
    public void testUpdateStoryPriority_oneDown() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        replayAll();
        storyBusiness.updateStoryPriority(stories.get(4), 5);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(3, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(4, stories.get(5).getPriority());
        verifyAll();
    }
    
    public void testUpdateStoryPriority_toTop() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);

        storyDAO.store(stories.get(0));
        storyDAO.store(stories.get(1));
        storyDAO.store(stories.get(2));
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        replayAll();
        storyBusiness.updateStoryPriority(stories.get(5), 0);
        assertSame(1, stories.get(0).getPriority());
        assertSame(2, stories.get(1).getPriority());
        assertSame(3, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(0, stories.get(5).getPriority());
        verifyAll();
    }


    
    public void testUpdateStoryPriority_insertNew() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        Story newStory = new Story();
        newStory.setBacklog(iter);
        newStory.setPriority(-1);
        
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        storyDAO.store(newStory);
        replayAll();
        storyBusiness.updateStoryPriority(newStory, 3);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(6, stories.get(5).getPriority());
        assertSame(3, newStory.getPriority());
        verifyAll();
    }
    
    private List<Story> createUpdatePrioData(Iteration iter) {
        List<Story> stories = new ArrayList<Story>();
        for(int i = 0 ; i < 6; i++) {
            Story tmp = new Story();
            tmp.setPriority(i);
            tmp.setBacklog(iter);
            stories.add(tmp);
            iter.getStories().add(tmp);
        }
        return stories;
    }
    
    @Test
    public void testGetStoryPointSumByBacklog() {
        expect(storyDAO.getStoryPointSumByBacklog(backlog.getId()))
            .andReturn(6);
        replayAll();
        
        assertEquals(6, storyBusiness.getStoryPointSumByBacklog(backlog));
        
        verifyAll();
    }
    
    
    
    @Test
    public void testStore_updateResponsibles() {
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

    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testStore_nullStoryId() {
        storyBusiness.store(null, new Story(), 123, new HashSet<Integer>());
    }
    
    
    @Test(expected = ObjectNotFoundException.class)
    public void testStore_noSuchStory() {
        expect(storyDAO.get(222)).andReturn(null);
        replayAll();
        storyBusiness.store(222, new Story(), 123, new HashSet<Integer>());
        verifyAll();
    }
    
    
    @Test
    public void testStore_updateBacklogAndClearResponsibles() {
        Backlog newBacklog = new Project();
        newBacklog.setId(123);
        Set<User> users = new HashSet<User>(Arrays.asList(new User(), new User()));
        storyInIteration.setResponsibles(users);
        
        expect(storyDAO.get(storyInIteration.getId())).andReturn(storyInIteration);
        expect(backlogDAO.get(newBacklog.getId())).andReturn(newBacklog);
        storyDAO.store(EasyMock.isA(Story.class));
        
        replayAll();
        Story actual = storyBusiness.store(storyInIteration.getId(),
                new Story(), newBacklog.getId(), new HashSet<Integer>());
        verifyAll();
        
        assertSame(newBacklog, actual.getBacklog());
        assertEquals(0, actual.getResponsibles().size());
    }

}
