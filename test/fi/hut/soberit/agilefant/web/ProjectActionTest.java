package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.ProjectDataContainer;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

public class ProjectActionTest {

    ProjectAction projectAction;
    ProjectBusiness projectBusiness;

    Project project;
    ProjectDataContainer projectDataContainer;

    @Before
    public void setUp_dependencies() {
        
        projectAction = new ProjectAction();
        projectAction.setProjectId(1);
        projectBusiness = createMock(ProjectBusiness.class);
        projectAction.setProjectBusiness(projectBusiness);
    }

    @Before
    public void setUp_data() {
        project = new Project();
        project.setId(1);
    }

    private void verifyAll() {
        verify(projectBusiness);
    }

    private void replayAll() {
        replay(projectBusiness);
    }

    @Test
    public void testProjectMetrics() {
        ProjectMetrics metrics = new ProjectMetrics();
        projectAction.setProjectId(project.getId());
        expect(projectBusiness.retrieve(project.getId())).andReturn(project);
        expect(projectBusiness.getProjectMetrics(project)).andReturn(metrics);
        replayAll();

        assertEquals(Action.SUCCESS, projectAction.projectMetrics());
        assertEquals(metrics, projectAction.getProjectMetrics());

        verifyAll();
    }


    @Test
    public void testProjectData() {
        ProjectTO projTO = new ProjectTO(project);
        projectAction.setProjectId(project.getId());
        expect(projectBusiness.getProjectData(project.getId())).andReturn(projTO);
        replayAll();
        assertEquals(Action.SUCCESS, projectAction.projectData());
        assertEquals(projTO, projectAction.getProject());
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testProjectData_notFound() {
        projectAction.setProjectId(-1);
        expect(projectBusiness.getProjectData(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        projectAction.projectData();
        verifyAll();
    }
    
    @Test
    public void testCreate() {
        projectAction.setProject(null);
        projectAction.create();
        assertNotNull(projectAction.getProject());
        assertNotNull(projectAction.getProject().getStartDate());
        assertNotNull(projectAction.getProject().getEndDate());
    }

    @Test
    public void testRetrieve() {
        projectAction.setProjectId(1);
        expect(projectBusiness.retrieve(1)).andReturn(project);
        replayAll();
        projectAction.retrieve();
        assertEquals(project, projectAction.getProject());
        verifyAll();
    }

    @Test
    public void testStore() {
        ProjectTO dummy = new ProjectTO(new Project());
        projectAction.setProductId(313);
        projectAction.setProjectId(1);
        projectAction.setProject(project);
        expect(projectBusiness.store(1, 313, project, null)).andReturn(dummy);
        replayAll();
        projectAction.store();
        assertEquals(dummy, projectAction.getProject());
        verifyAll();
    }
    
    @Test
    public void testDelete_success() {
        Project proj = new Project();
        Product parent = new Product();
        parent.setId(123);
        proj.setParent(parent);
        
        expect(projectBusiness.retrieve(1)).andReturn(proj);
        projectBusiness.delete(1);
        replayAll();
        projectAction.setConfirmationString("yes");
        assertEquals(Action.SUCCESS, projectAction.delete());
        assertEquals(123, projectAction.getProductId());
        verifyAll();    
    }
    
    @Test
    public void testDelete_failure() {

        replayAll();
        projectAction.setConfirmationString("no");
        assertEquals(Action.ERROR, projectAction.delete());
        
        verifyAll();    
    }

    
    @Test(expected = ObjectNotFoundException.class)
    public void testDelete_noSuchIteration() {
        projectAction.setProjectId(-1);
        projectBusiness.delete(-1);
        expect(projectBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        projectAction.setConfirmationString("yes");
        projectAction.delete();
        
        verifyAll();    
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testDelete_forbidden() {
        Project proj = new Project();
        Product prod = new Product();
        prod.setId(123);
        proj.setParent(prod);
        expect(projectBusiness.retrieve(1)).andReturn(proj);
        projectBusiness.delete(1);
        expectLastCall().andThrow(new ConstraintViolationException(null, null, null));
        expect(projectBusiness.retrieve(1)).andReturn(new Project());
        replayAll();
        projectAction.setConfirmationString("yes");
        projectAction.delete();
        
        verifyAll(); 
    }

    @Test
    public void testInitializePrefetchData() {
        expect(projectBusiness.retrieve(123)).andReturn(project);
        replayAll();
        projectAction.initializePrefetchedData(123);
        assertEquals(project, projectAction.getProject());
        verifyAll();
    }

}
