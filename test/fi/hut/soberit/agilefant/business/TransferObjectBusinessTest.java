package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.AssignedWorkTO;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.transfer.IterationTO;
import fi.hut.soberit.agilefant.transfer.ProjectTO;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO.TaskClass;

public class TransferObjectBusinessTest {

    private TransferObjectBusinessImpl transferObjectBusiness = new TransferObjectBusinessImpl();
    private HourEntryBusiness hourEntryBusiness;
    private UserBusiness userBusiness;
    private TeamBusiness teamBusiness;
    private BacklogBusiness backlogBusiness;
    private ProductBusiness productBusiness;
    private ProjectBusiness projectBusiness;
    private IterationBusiness iterationBusiness;
    private StoryHierarchyBusiness storyHierarchyBusiness;
    
    Project project;
    Iteration iteration;
    Story story1;
    Story story2;
    Task task;
    User assignedUser;
    User notAssignedUser;

    @Before
    public void setUp_dependencies() {
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        transferObjectBusiness.setHourEntryBusiness(hourEntryBusiness);
        
        userBusiness = createMock(UserBusiness.class);
        transferObjectBusiness.setUserBusiness(userBusiness);
        
        teamBusiness = createMock(TeamBusiness.class);
        transferObjectBusiness.setTeamBusiness(teamBusiness);
        
        backlogBusiness = createMock(BacklogBusiness.class);
        transferObjectBusiness.setBacklogBusiness(backlogBusiness);
        
        productBusiness = createMock(ProductBusiness.class);
        transferObjectBusiness.setProductBusiness(productBusiness);
        
        projectBusiness = createMock(ProjectBusiness.class);
        transferObjectBusiness.setProjectBusiness(projectBusiness);
        
        iterationBusiness = createMock(IterationBusiness.class);
        transferObjectBusiness.setIterationBusiness(iterationBusiness);
        
        storyHierarchyBusiness = createMock(StoryHierarchyBusiness.class);
        transferObjectBusiness.setStoryHierarchyBusiness(storyHierarchyBusiness);
    }
    
    private void verifyAll() {
        verify(hourEntryBusiness, userBusiness, teamBusiness, backlogBusiness, productBusiness, projectBusiness, iterationBusiness, storyHierarchyBusiness);
    }

    private void replayAll() {
        replay(hourEntryBusiness, userBusiness, teamBusiness, backlogBusiness, productBusiness, projectBusiness, iterationBusiness, storyHierarchyBusiness);
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
        iteration.setStories(new HashSet<Story>(Arrays.asList(story1, story2)));

        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        replayAll();

        Collection<Story> actualStories = new ArrayList<Story>();
        actualStories.addAll(transferObjectBusiness
                .constructBacklogData(iteration));

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
                .constructBacklogData(iteration);
        assertEquals(0, stories.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConstructTaskTO() {
        List<User> responsibles = Arrays.asList(assignedUser, notAssignedUser);
        
        task.setIteration(iteration);
        task.setResponsibles(responsibles);
        task.setHourEntries(null);

        expect(hourEntryBusiness.calculateSum((Collection<? extends HourEntry>) isNull()))
                .andReturn(Long.valueOf(0)).anyTimes();

        replayAll();
        TaskTO actualTO = transferObjectBusiness.constructTaskTO(task);
        verifyAll();

        assertEquals("Task and transfer object id's not equal", task.getId(),
                actualTO.getId());

        assertEquals(responsibles, actualTO.getResponsibles());
    }

    @Test
    public void testConstructStoryTO() {
        Set<User> responsibles = new HashSet<User>(Arrays.asList(assignedUser, notAssignedUser));
        story1.setBacklog(iteration);
        story1.setResponsibles(responsibles);

        replayAll();
        StoryTO actualTO = transferObjectBusiness.constructStoryTO(story1);
        verifyAll();

        assertEquals("Task and transfer object id's not equal", story1.getId(),
                actualTO.getId());

        assertEquals(responsibles, actualTO.getResponsibles());
    }
    
    @Test
    public void constructIterationTO() {
        Iteration past = new Iteration();
        past.setStartDate(new DateTime().minusMonths(2));
        past.setEndDate(new DateTime().minusMonths(1));
        replayAll();
        IterationTO actual = transferObjectBusiness.constructIterationTO(past);
        verifyAll();
        assertEquals(ScheduleStatus.PAST, actual.getScheduleStatus());
    }
    
    @Test
    public void constructProjectTO() {
        Project past = new Project();
        past.setStartDate(new DateTime().minusMonths(2));
        past.setEndDate(new DateTime().minusMonths(1));
        
        List<Story> stories = new ArrayList<Story>(Arrays.asList(new Story(), new Story()));
        
        expect(storyHierarchyBusiness.retrieveProjectLeafStories(past))
            .andReturn(stories);
        
        replayAll();
        ProjectTO actual = transferObjectBusiness.constructProjectTO(past);
        verifyAll();
        
        assertEquals(ScheduleStatus.PAST, actual.getScheduleStatus());
        assertTrue(actual.getLeafStories().containsAll(stories));
        assertEquals(2, actual.getLeafStories().size());
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
    public void testConstructTeamAutocompleteData_withUserIds() {
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
        List<AutocompleteDataNode> actual = this.transferObjectBusiness.constructTeamAutocompleteData(true);
        assertEquals(1, actual.size());
        assertEquals(1, (int)actual.get(0).getId());
        assertEquals("daa", actual.get(0).getName());
        assertEquals("daa", actual.get(0).getMatchedString());
        assertEquals("fi.hut.soberit.agilefant.model.Team", actual.get(0).getBaseClassName());
        assertSame(team, actual.get(0).getOriginalObject());
        assertEquals(2, actual.get(0).getIdList().size());
        verifyAll();
    }
    
    @Test
    public void testConstructTeamAutocompleteData_withoutUserIds() {
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
        List<AutocompleteDataNode> actual = this.transferObjectBusiness.constructTeamAutocompleteData(false);
        assertEquals(1, actual.size());
        assertEquals(1, (int)actual.get(0).getId());
        assertEquals("daa", actual.get(0).getName());
        assertEquals("daa", actual.get(0).getMatchedString());
        assertEquals("fi.hut.soberit.agilefant.model.Team", actual.get(0).getBaseClassName());
        assertSame(team, actual.get(0).getOriginalObject());
        assertNull(actual.get(0).getIdList());
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
                .constructBacklogAutocompleteData(null);
        
        verifyAll();
        
        assertEquals(4, nodes.size());
        
        AutocompleteDataNode node = getDataNodeById(1, nodes);
        assertEquals(Backlog.class.getCanonicalName(), node.getBaseClassName());
        assertEquals("Product", node.getName());
        assertEquals(product, node.getOriginalObject());
        
        node = getDataNodeById(7, nodes);
        assertEquals("Product > Project", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        assertEquals(project, node.getOriginalObject());
        
        node = getDataNodeById(333, nodes);
        assertEquals("Product > Project > Iter 1", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        assertEquals(iterationUnderProject, node.getOriginalObject());
        
        node = getDataNodeById(615, nodes);
        assertEquals("Product > Iter 2", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        assertEquals(iterationUnderProduct, node.getOriginalObject());
    }
    
    @Test
    public void testGetBacklogAutocompleteData_filterByBacklog() {
        Product product = new Product();
        product.setId(1);
        product.setName("Product");
        
        Product product2 = new Product();
        product2.setId(123);
        product2.setName("Wrong");
        
        Backlog project = new Project();
        project.setId(7);
        project.setParent(product);
        project.setName("Project");
        
        expect(backlogBusiness.retrieveAll()).andReturn(Arrays.asList(product, project, product2));

        expect(backlogBusiness.retrieve(7)).andReturn(project);
        expect(backlogBusiness.getParentProduct(project)).andReturn(product);
        
        expect(backlogBusiness.getParentProduct(product)).andReturn(product);
        expect(backlogBusiness.getParentProduct(project)).andReturn(product);
        expect(backlogBusiness.getParentProduct(product2)).andReturn(product2);
        
        replayAll();
        
        // Supply the projcet id
        List<AutocompleteDataNode> nodes = transferObjectBusiness
                .constructBacklogAutocompleteData(7);
        
        verifyAll();
        
        assertEquals(2, nodes.size());
        
        AutocompleteDataNode node = getDataNodeById(1, nodes);
        assertEquals(Backlog.class.getCanonicalName(), node.getBaseClassName());
        assertEquals("Product", node.getName());
        assertEquals(product, node.getOriginalObject());
        
        node = getDataNodeById(7, nodes);
        assertEquals("Product > Project", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        assertEquals(project, node.getOriginalObject());
    }
    
    /**
     * Project autocomplete data.
     */
    @Test
    public void testGetProjectAutocompleteData() {
        Product product = new Product();
        product.setId(1);
        product.setName("Product");
        
        Project project = new Project();
        project.setId(7);
        project.setParent(product);
        project.setName("Project");
        
        expect(projectBusiness.retrieveAll()).andReturn(Arrays.asList(project));
        
        replayAll();
        
        List<AutocompleteDataNode> nodes = transferObjectBusiness
                .constructProjectAutocompleteData();
        
        verifyAll();
        
        assertEquals(1, nodes.size());
               
        AutocompleteDataNode node = getDataNodeById(7, nodes);
        assertEquals("Product > Project", node.getName());
        assertEquals(node.getName(), node.getMatchedString());
        assertEquals(project, node.getOriginalObject());

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
        assertEquals(iterationUnderProject, node.getOriginalObject());
        
        node = getDataNodeById(615, nodes);
        assertEquals("Product > Iter 2", node.getName());
        assertEquals(iterationUnderProduct, node.getOriginalObject());
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
    public void testConstructProductAutocompleteData() {
        Product product1 = new Product();
        product1.setId(756);
        product1.setName("Test product no. 1");
        Product product2 = new Product();
        product2.setName("Foo bar");
        product2.setId(918);
        
        expect(productBusiness.retrieveAll()).andReturn(Arrays.asList(product1, product2));
        replayAll();
        List<AutocompleteDataNode> nodes = transferObjectBusiness
                .constructProductAutocompleteData();
        verifyAll();
        
        AutocompleteDataNode actual = getDataNodeById(756, nodes);
        assertEquals("Test product no. 1", actual.getName());
        assertEquals(product1, actual.getOriginalObject());
        
        actual = getDataNodeById(918, nodes);
        assertEquals("Foo bar", actual.getName());
        assertEquals(product2, actual.getOriginalObject());
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
    
    @Test
    public void testCreateQueueudDailyWorkTaskTO_withStoryTask() {
        Task task = new Task();

        WhatsNextEntry entry = new WhatsNextEntry();
        User user = new User();
        entry.setUser(user);
        entry.setTask(task);
        entry.setRank(2);
        
        Story story = new Story();
        story.setName("story");
        story.setId(3);
        
        task.setId(5);
        
        Iteration iteration = new Iteration();
        iteration.setName("iter");
        iteration.setId(4);
        
        story.setBacklog(iteration);
        
        task.setStory(story);
        task.setResponsibles(Arrays.asList(new User[] { user } ));

        DailyWorkTaskTO transferObj = transferObjectBusiness.constructQueuedDailyWorkTaskTO(entry);
        assertEquals("iter > story", transferObj.getContextName());
        assertEquals(2, transferObj.getWorkQueueRank());
        assertEquals(3, transferObj.getParentStoryId());
        assertEquals(4, transferObj.getBacklogId());
        assertEquals(5, transferObj.getId());
        
        
        assertEquals(TaskClass.NEXT_ASSIGNED, transferObj.getTaskClass());
    };

    @Test
    public void testCreateUnqueueudDailyWorkTaskTO_withIterationTask() {
        Task task = new Task();
        task.setId(5);
        
        Iteration iteration = new Iteration();
        iteration.setName("iter");
        iteration.setId(4);
        
        task.setIteration(iteration);

        DailyWorkTaskTO transferObj = transferObjectBusiness.constructUnqueuedDailyWorkTaskTO(task);
        assertEquals("iter", transferObj.getContextName());
        assertEquals(-1, transferObj.getWorkQueueRank());
        assertEquals(0, transferObj.getParentStoryId());
        assertEquals(4, transferObj.getBacklogId());
        assertEquals(5, transferObj.getId());
        assertEquals(TaskClass.ASSIGNED, transferObj.getTaskClass());
    };

    @Test
    public void testCreateAssignedWorkTO() {
        Iteration iteration = new Iteration();
        iteration.setId(7);

        Story story = new Story();
        story.setId(8);

        Story story2 = new Story();
        story2.setId(10);
        
        Task task = new Task();
        task.setId(5);
        task.setStory(story);
        
        Task task2 = new Task();
        task2.setId(6);
        task2.setIteration(iteration);
        
        AssignedWorkTO assigned = transferObjectBusiness.constructAssignedWorkTO(
            Arrays.asList(task, task2),
            Arrays.asList(story, story2)
        );
        
        assertEquals(assigned.getStories().get(0).getId(), story.getId());
        assertEquals(2, assigned.getStories().size());
        assertEquals(assigned.getTasksWithoutStory().get(0).getId(), task2.getId());
        assertEquals(1, assigned.getTasksWithoutStory().size());
    };
}
