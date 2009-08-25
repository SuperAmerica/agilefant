package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

public class ProjectBusinessTest {

    ProjectBusinessImpl projectBusiness = new ProjectBusinessImpl();
    ProjectDAO projectDAO;
    BacklogDAO backlogDAO;
    ProductBusiness productBusiness;

    Project project;
    Product product;
    User user1;
    User user2;
    Story story1;
    Story story2;

    @Before
    public void setUp_dependencies() {
        projectDAO = createStrictMock(ProjectDAO.class);
        projectBusiness.setProjectDAO(projectDAO);

        backlogDAO = createStrictMock(BacklogDAO.class);
        projectBusiness.setBacklogDAO(backlogDAO);

        productBusiness = createStrictMock(ProductBusiness.class);
        projectBusiness.setProductBusiness(productBusiness);
    }

    @Before
    public void setUp_data() {
        product = new Product();
        product.setId(313);

        project = new Project();
        project.setId(123);
        project.setStartDate(new DateTime(2008, 1, 1, 12, 0, 0, 0));
        project.setEndDate(new DateTime(2008, 3, 1, 12, 0, 0, 0));

        user1 = new User();
        user1.setId(1);
        user2 = new User();
        user2.setId(2);

        story1 = new Story();
        story1.setId(127);
        story2 = new Story();
        story2.setId(130);
        story2.setResponsibles(new HashSet<User>(Arrays.asList(user2)));

        Task task = new Task();
        task.setId(86);
        task.setResponsibles(Arrays.asList(user1));
        story1.setTasks(Arrays.asList(task));
    }

    private void replayAll() {
        replay(projectDAO, backlogDAO, productBusiness);
    }

    private void verifyAll() {
        verify(projectDAO, backlogDAO, productBusiness);
    }

    @Test
    public void testGetProjectMetrics() {
        expect(
                backlogDAO.calculateStoryPointSumIncludeChildBacklogs(project
                        .getId())).andReturn(100);

        replayAll();

        ProjectMetrics actualMetrics = projectBusiness
                .getProjectMetrics(project);

        assertEquals(100, actualMetrics.getStoryPoints());

        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetProjectMetrics_nullProject() {
        projectBusiness.getProjectMetrics(null);
    }

    @Test
    public void testStoreProject_oldProject() {
        expect(projectDAO.get(project.getId())).andReturn(project);
        projectDAO.store(project);
        replayAll();
        assertEquals(project, projectBusiness.store(project.getId(), null, project));
        verifyAll();
    }

    @Test
    public void testStoreProject_newProject() {
        project.setId(0);
        expect(productBusiness.retrieve(313)).andReturn(product);
        expect(projectDAO.create(EasyMock.isA(Project.class))).andReturn(123);
        expect(projectDAO.get(123)).andReturn(project);
        replayAll();
        assertEquals(project, projectBusiness.store(0, 313, project));
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreProject_invalidDates() {
        project.setStartDate(new DateTime(2008, 1, 1, 1, 0, 0, 0));
        project.setEndDate(new DateTime(2007, 1, 1, 1, 0, 0, 0));

        expect(projectDAO.get(project.getId())).andReturn(project);
        projectDAO.store(project);
        replayAll();
        projectBusiness.store(project.getId(), null, project);
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testStoreProject_illegalProjectId() {
        Project falseProject = new Project();
        falseProject.setId(4);
        expect(projectDAO.get(falseProject.getId())).andReturn(null);
        replayAll();
        projectBusiness.store(falseProject.getId(), null, falseProject);
        verifyAll();
    }

    @Test
    public void testStoreProject_withProduct() {
        expect(projectDAO.get(project.getId())).andReturn(project);
        expect(productBusiness.retrieve(313)).andReturn(product);
        projectDAO.store(project);
        replayAll();
        assertEquals(project, projectBusiness.store(project.getId(), 313, project));
        verifyAll();
        assertEquals(product, project.getParent());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testStoreProject_invalidProduct() {
        expect(projectDAO.get(123)).andReturn(project);
        expect(productBusiness.retrieve(313)).andThrow(new ObjectNotFoundException());
        replayAll();
        projectBusiness.store(123, 313, project);
        verifyAll();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreProject_newProjectWithoutParent() {
        projectBusiness.store(0, null, project);
    }

    @Test
    public void testGetAssignedUsers() {
        Collection<User> expected = Arrays.asList(user1, user2);
        expect(projectDAO.getAssignedUsers(project)).andReturn(expected);
        replayAll();

        assertSame("List doesn't contain expected users", expected,
                projectBusiness.getAssignedUsers(project));

        verifyAll();
    }
    
    @Test
    public void testGetProjectData() {
        Project proj = new Project();
        proj.setId(111);
        proj.setName("Foo faa");
        
        DateTime currentDate = new DateTime();
        
        Iteration pastIteration = new Iteration();
        pastIteration.setId(123);
        pastIteration.setStartDate(currentDate.minusDays(120));
        pastIteration.setEndDate(currentDate.minusDays(50));
        
        Iteration currentIteration = new Iteration();
        currentIteration.setId(333);
        currentIteration.setStartDate(currentDate.minusDays(1));
        currentIteration.setEndDate(currentDate.plusDays(10));
        
        Iteration futureIteration = new Iteration();
        futureIteration.setId(444);
        futureIteration.setStartDate(currentDate.plusDays(1));
        futureIteration.setEndDate(currentDate.plusDays(18));
        
        proj.getChildren().addAll(Arrays.asList(pastIteration, currentIteration, futureIteration));
        
        expect(projectDAO.get(111)).andReturn(proj);
        replayAll();
        ProjectTO actual = projectBusiness.getProjectData(111);
        verifyAll();
        
        assertEquals(111, actual.getId());
        assertEquals("Foo faa", actual.getName());
        
        assertEquals(1, actual.getFutureIterations().size());
        assertEquals(1, actual.getOngoingIterations().size());
        assertEquals(1, actual.getPastIterations().size());
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testGetProjectData_noSuchProject() {
        expect(projectDAO.get(-1)).andReturn(null);
        replayAll();
        projectBusiness.getProjectData(-1);
        verifyAll();
    }
}
