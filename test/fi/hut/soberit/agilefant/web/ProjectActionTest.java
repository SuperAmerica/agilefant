package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
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
    
    @Before
    public void setUp_projectDataContainer() {
        projectDataContainer = new ProjectDataContainer();
        
        Story story1 = new Story();
        story1.setName("Story 1");
        Story story2 = new Story();
        story2.setName("Story 2");
        
        projectDataContainer.setStories(Arrays.asList(story1, story2));
    }
    
    @Test
    public void testProjectContents() {
        projectAction.setProjectId(project.getId());
        
        expect(projectBusiness.getProjectContents(project.getId()))
            .andReturn(projectDataContainer);
        replay(projectBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, projectAction.projectContents());
        assertEquals('{', projectAction.getJsonData().charAt(0));
        assertTrue(projectAction.getJsonData().contains("Story 1"));
        assertTrue(projectAction.getJsonData().contains("Story 2"));
        
        verify(projectBusiness);
    }
    
    @Test
    public void testProjectContents_secondInput() {
        projectAction.setProjectId(project.getId());
        
        Story anotherStory = new Story();
        anotherStory.setName("some characters with no meaning");
        projectDataContainer.setStories(Arrays.asList(anotherStory));
        
        expect(projectBusiness.getProjectContents(project.getId()))
            .andReturn(projectDataContainer);
        replay(projectBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, projectAction.projectContents());
        assertEquals('{', projectAction.getJsonData().charAt(0));
        assertTrue(projectAction.getJsonData().contains("some characters with no meaning"));
        
        verify(projectBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testProjectContents_nonExistentProject() {
        projectAction.setProjectId(-1);
        
        expect(projectBusiness.getProjectContents(-1))
            .andThrow(new ObjectNotFoundException());
        replay(projectBusiness);
        
        projectAction.projectContents();
        
        verify(projectBusiness);
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
}
