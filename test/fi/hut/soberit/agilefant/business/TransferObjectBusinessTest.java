package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.TransferObjectBusinessImpl;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

public class TransferObjectBusinessTest {

    private TransferObjectBusinessImpl transferObjectBusiness = new TransferObjectBusinessImpl();
    private ProjectBusiness projectBusiness;
    private HourEntryBusiness hourEntryBusiness;
    private StoryBusiness storyBusiness;
    private UserBusiness userBusiness;
    private TeamBusiness teamBusiness;
    private BacklogBusiness backlogBusiness;
    private IterationBusiness iterationBusiness;
    
    Project project;
    Iteration iteration;
    Story story1;
    Story story2;
    Task task;
    User assignedUser;
    User notAssignedUser;

    @Before
    public void setUp_dependencies() {
        projectBusiness = createMock(ProjectBusiness.class);
        transferObjectBusiness.setProjectBusiness(projectBusiness);
        
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        transferObjectBusiness.setHourEntryBusiness(hourEntryBusiness);
        
        storyBusiness = createMock(StoryBusiness.class);
        transferObjectBusiness.setStoryBusiness(storyBusiness);
        
        userBusiness = createMock(UserBusiness.class);
        transferObjectBusiness.setUserBusiness(userBusiness);
        
        teamBusiness = createMock(TeamBusiness.class);
        transferObjectBusiness.setTeamBusiness(teamBusiness);
        
        backlogBusiness = createMock(BacklogBusiness.class);
        transferObjectBusiness.setBacklogBusiness(backlogBusiness);
        
        iterationBusiness = createMock(IterationBusiness.class);
        transferObjectBusiness.setIterationBusiness(iterationBusiness);
    }
    
    private void verifyAll() {
        verify(projectBusiness, hourEntryBusiness, storyBusiness, userBusiness, teamBusiness, backlogBusiness, iterationBusiness);
    }

    private void replayAll() {
        replay(projectBusiness, hourEntryBusiness, storyBusiness, userBusiness, teamBusiness, backlogBusiness, iterationBusiness);
    }
    
    
    @Before
    public void setUp() {
        iteration = new Iteration();
        project = new Project();
        project.setId(8474);
        iteration.setParent(project);

        assignedUser = new User();
        assignedUser.setId(666);
        notAssignedUser = new User();
        notAssignedUser.setId(515);

        story1 = new Story();
        story2 = new Story();
        story1.setBacklog(iteration);
        task = new Task();
        story1.setId(1265);
        story2.setId(8472);
        task.setId(1236);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testContructBacklogDataWithUserData() {
        story1.getTasks().add(task);
        task.setHourEntries(null);
        iteration.setStories(Arrays.asList(story1, story2));

        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        replayAll();

        Collection<Story> actualStories = new ArrayList<Story>();
        actualStories.addAll(transferObjectBusiness
                .constructBacklogDataWithUserData(iteration, Arrays
                        .asList(assignedUser)));

        verifyAll();

        assertTrue("List does not contain correct story transfer object",
                containsStoryWithId(story1.getId(), actualStories));
        assertTrue("List does not contain correct story transfer object",
                containsStoryWithId(story2.getId(), actualStories));
        assertTrue("Story 1 transfer object does not contain correct task transfer object",
                containsTaskWithId(task.getId(), story1.getId(), actualStories));
    }

    @Test
    public void testContructBacklogDataWithUserData_emptyIteration() {
        iteration.getStories().clear();
        Collection<StoryTO> stories = transferObjectBusiness
                .constructBacklogDataWithUserData(iteration, null);
        assertEquals(0, stories.size());
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testConstructTaskTO_delegate() {
        task.setStory(null);
        task.setIteration(iteration);
        task.setHourEntries(null);
        
        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        expect(projectBusiness.getAssignedUsers((Project) task.getIteration()
                        .getParent())).andReturn(Arrays.asList(assignedUser));

        replayAll();

        assertEquals(task.getId(), transferObjectBusiness.constructTaskTO(task)
                .getId());

        verifyAll();
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testConstructTaskTO_delegateForStory() {
        task.setStory(story1);
        task.setIteration(null);
        task.setHourEntries(null);

        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        expect(storyBusiness.getStorysProjectResponsibles(story1))
            .andReturn(Arrays.asList(assignedUser));

        replayAll();

        assertEquals(task.getId(), transferObjectBusiness.constructTaskTO(task)
                .getId());

        verifyAll();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructTaskTO() {
        task.setIteration(iteration);
        task.setResponsibles(new ArrayList<User>(Arrays.asList(assignedUser, notAssignedUser)));
        task.setHourEntries(null);

        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        replayAll();
        TaskTO actualTO = transferObjectBusiness.constructTaskTO(task,
                new ArrayList<User>(Arrays.asList(assignedUser)));
        verifyAll();

        assertEquals("Task and transfer object id's not equal", task.getId(),
                actualTO.getId());

        boolean assignedUserFound = false;
        boolean notAssignedUserFound = false;
        for (ResponsibleContainer rc : actualTO.getUserData()) {
            if (rc.getUser() == assignedUser) {
                assignedUserFound = true;
                assertTrue(
                        "The assigned user seems not to be assigned to project",
                        rc.isInProject());
            }
            if (rc.getUser() == notAssignedUser) {
                notAssignedUserFound = true;
                assertFalse(
                        "The not assigned user seems to be assigned to project",
                        rc.isInProject());
            }
        }
        assertTrue("User not found in responsible containers",
                assignedUserFound && notAssignedUserFound);
    }

    @Test
    public void testConstructStoryTO() {
        story1.setBacklog(iteration);
        story1.setResponsibles(new HashSet<User>(Arrays.asList(assignedUser, notAssignedUser)));

        replayAll();
        StoryTO actualTO = transferObjectBusiness.constructStoryTO(story1,
                Arrays.asList(assignedUser));
        verifyAll();

        assertEquals("Task and transfer object id's not equal", story1.getId(),
                actualTO.getId());

        boolean assignedUserFound = false;
        boolean notAssignedUserFound = false;
        for (ResponsibleContainer rc : actualTO.getUserData()) {
            if (rc.getUser() == assignedUser) {
                assignedUserFound = true;
                assertTrue(
                        "The assigned user seems not to be assigned to project",
                        rc.isInProject());
            }
            if (rc.getUser() == notAssignedUser) {
                notAssignedUserFound = true;
                assertFalse(
                        "The not assigned user seems to be assigned to project",
                        rc.isInProject());
            }
        }
        assertTrue("User not found in responsible containers",
                assignedUserFound && notAssignedUserFound);
    }
    
    @Test
    public void testConstructUserAutocompleteData() {
        User user = new User();
        user.setId(1);
        user.setFullName("daadaa");
        user.setLoginName("additional");
        
        expect(userBusiness.retrieveAll()).andReturn(Arrays.asList(user));
        
        replayAll();
        List<AutocompleteDataNode> actual = this.transferObjectBusiness.constructUserAutocompleteData();
        assertEquals(1, actual.size());
        assertEquals(1, (int)actual.get(0).getId());
        assertEquals("daadaa", actual.get(0).getName());
        assertEquals("daadaa additional", actual.get(0).getMatchedString());
        assertEquals("fi.hut.soberit.agilefant.model.User", actual.get(0).getBaseClassName());
        assertNull(actual.get(0).getIdList());
        verifyAll();
    }
    
    @Test
    public void testConstructTeamAutocompleteData() {
        User user1 = new User();
        User user2 = new User();
        Team team = new Team();
        user1.setId(1);
        user2.setId(2);
        team.setName("daa");
        team.setId(1);
        team.setUsers(Arrays.asList(user1,user2));
        
        expect(teamBusiness.retrieveAll()).andReturn(Arrays.asList(team));
        
        replayAll();
        List<AutocompleteDataNode> actual = this.transferObjectBusiness.constructTeamAutocompleteData();
        assertEquals(1, actual.size());
        assertEquals(1, (int)actual.get(0).getId());
        assertEquals("daa", actual.get(0).getName());
        assertEquals("daa", actual.get(0).getMatchedString());
        assertEquals("fi.hut.soberit.agilefant.model.Team", actual.get(0).getBaseClassName());
        assertEquals(2, actual.get(0).getIdList().size());
        verifyAll();
    }

    /**
     * Helper method to check that the stories list contains a story with a
     * specific id.
     */
    private boolean containsStoryWithId(int expectedId,
            Collection<Story> storiesList) {
        boolean idFound = false;
        for (Story actualStory : storiesList) {
            if (actualStory.getId() == expectedId) {
                idFound = true;
                break;
            }
        }
        return idFound;
    }

    /**
     * Helper method to check that the task list contains a task with a specific
     * id.
     * 
     * @param storyId
     *            TODO
     */
    private boolean containsTaskWithId(int expectedId, int storyId,
            Collection<Story> storiesList) {
        boolean idFound = false;
        for (Story actualStory : storiesList) {
            if (actualStory.getId() == storyId) {
                for (Task task : actualStory.getTasks()) {
                    if (task.getId() == expectedId) {
                        idFound = true;
                        break;
                    }
                }
                break;
            }
        }
        return idFound;
    }

    /*
     * BACKLOG AUTOCOMPLETE DATA
     */
    @Test
    public void testGetBacklogAutocompleteData() {
        Backlog product = new Product();
        product.setId(1);
        product.setName("Product");
        
        Backlog project = new Project();
        project.setId(7);
        project.setParent(product);
        project.setName("Project");
        
        Backlog iterationUnderProject = new Iteration();
        iterationUnderProject.setId(333);
        iterationUnderProject.setParent(project);
        iterationUnderProject.setName("Iter 1");
        
        Backlog iterationUnderProduct = new Iteration();
        iterationUnderProduct.setId(615);
        iterationUnderProduct.setParent(product);
        iterationUnderProduct.setName("Iter 2");
        
        expect(backlogBusiness.retrieveAll())
            .andReturn(Arrays.asList(product, project, iterationUnderProject, iterationUnderProduct));
        
        replayAll();
        
        List<AutocompleteDataNode> nodes = transferObjectBusiness
                .constructBacklogAutocompleteData();
        
        verifyAll();
        
        assertEquals(4, nodes.size());
        
        AutocompleteDataNode node = getDataNodeById(1, nodes);
        assertEquals(Backlog.class.getCanonicalName(), node.getBaseClassName());
        assertEquals("Product", node.getName());
        
        node = getDataNodeById(7, nodes);
        assertEquals("Product > Project", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        
        node = getDataNodeById(333, nodes);
        assertEquals("Product > Project > Iter 1", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        
        node = getDataNodeById(615, nodes);
        assertEquals("Product > Iter 2", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
    }

    @Test
    public void testGetCurrentIterationAutocompleteData() {
        Backlog product = new Product();
        product.setId(1);
        product.setName("Product");
        
        Backlog project = new Project();
        project.setId(7);
        project.setParent(product);
        project.setName("Project");
        
        Iteration iterationUnderProject = new Iteration();
        iterationUnderProject.setId(333);
        iterationUnderProject.setParent(project);
        iterationUnderProject.setName("Iter 1");
        
        Iteration iterationUnderProduct = new Iteration();
        iterationUnderProduct.setId(615);
        iterationUnderProduct.setParent(product);
        iterationUnderProduct.setName("Iter 2");
        
        expect(iterationBusiness.retrieveCurrentAndFutureIterations())
            .andReturn(Arrays.asList(iterationUnderProject, iterationUnderProduct));
        
        replayAll();
        
        List<AutocompleteDataNode> nodes = transferObjectBusiness
                .constructCurrentIterationAutocompleteData();
        
        verifyAll();
        
        assertEquals(2, nodes.size());
        AutocompleteDataNode node = getDataNodeById(333, nodes);
        assertEquals("Product > Project > Iter 1", node.getName());
        
        node = getDataNodeById(615, nodes);
        assertEquals("Product > Iter 2", node.getName());
    }

    
    /**
     * Helper method to get a <code>AutocompleteDataNode</code> with specified id.
     */
    private AutocompleteDataNode getDataNodeById(int id, Collection<AutocompleteDataNode> nodes) {
        for (AutocompleteDataNode adn : nodes) {
            if (adn.getId() == id) {
                return adn;
            }
        }
        return null;
    }
    
    @Test
    public void testCalculateBacklogScheduleStatus_forProduct() {
        Product product = new Product();
        assertEquals(ScheduleStatus.ONGOING, transferObjectBusiness.getBacklogScheduleStatus(product));
    }
    
    @Test
    public void testCalculateBacklogScheduleStatus_forPastIteration() {
        Iteration iter = new Iteration();
        iter.setStartDate(new DateTime().minusYears(3));
        iter.setEndDate(new DateTime().minusYears(3).plusMonths(1));
        assertEquals(ScheduleStatus.PAST, transferObjectBusiness.getBacklogScheduleStatus(iter));
    }
    
    @Test
    public void testCalculateBacklogScheduleStatus_forCurrentIteration() {
        Iteration iter = new Iteration();
        iter.setStartDate(new DateTime());
        iter.setEndDate(new DateTime().plusMonths(1));
        assertEquals(ScheduleStatus.ONGOING, transferObjectBusiness.getBacklogScheduleStatus(iter));
    }
    
    @Test
    public void testCalculateBacklogScheduleStatus_forFutureIteration() {
        Iteration iter = new Iteration();
        iter.setStartDate(new DateTime().plusYears(3));
        iter.setEndDate(new DateTime().plusYears(3).plusMonths(1));
        assertEquals(ScheduleStatus.FUTURE, transferObjectBusiness.getBacklogScheduleStatus(iter));
    }
}
