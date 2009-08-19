package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.ProjectDataContainer;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;

public class ProjectActionTest {

    ProjectAction projectAction;
    ProjectBusiness projectBusiness;

    Project project;
    ProjectDataContainer projectDataContainer;

    @Before
    public void setUp_dependencies() {
        projectAction = new ProjectAction();

        projectBusiness = createMock(ProjectBusiness.class);
        projectAction.setProjectBusiness(projectBusiness);
    }

    @Before
    public void setUp_data() {
        project = new Project();
        project.setId(123);
    }

    @Test
    public void testProjectMetrics() {
        ProjectMetrics metrics = new ProjectMetrics();
        projectAction.setProjectId(project.getId());
        expect(projectBusiness.retrieve(project.getId())).andReturn(project);
        expect(projectBusiness.getProjectMetrics(project)).andReturn(metrics);
        replay(projectBusiness);

        assertEquals(Action.SUCCESS, projectAction.projectMetrics());
        assertEquals(metrics, projectAction.getProjectMetrics());

        verify(projectBusiness);
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
        projectAction.setProjectId(123);
        expect(projectBusiness.retrieve(123)).andReturn(project);
        replay(projectBusiness);
        projectAction.retrieve();
        assertEquals(project, projectAction.getProject());
        verify(projectBusiness);
    }

    @Test
    public void testStore() {
        Project dummy = new Project();
        projectAction.setProductId(313);
        projectAction.setProjectId(123);
        projectAction.setProject(project);
        expect(projectBusiness.store(123, 313, project)).andReturn(dummy);
        replay(projectBusiness);
        projectAction.store();
        assertEquals(dummy, projectAction.getProject());
        verify(projectBusiness);
    }

    @Test
    public void testDelete() {
        projectAction.setProjectId(123);
        projectBusiness.delete(123);
        replay(projectBusiness);
        projectAction.delete();
        verify(projectBusiness);
    }

    @Test
    public void testInitializePrefetchData() {
        projectAction.setProjectId(123);
        expect(projectBusiness.retrieve(123)).andReturn(project);
        replay(projectBusiness);
        projectAction.retrieve();
        assertEquals(project, projectAction.getProject());
        verify(projectBusiness);
    }

}
