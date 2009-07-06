package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectDataContainer;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;
import fi.hut.soberit.agilefant.transfer.StoryTO;


public class ProjectBusinessTest {

    ProjectBusinessImpl projectBusiness = new ProjectBusinessImpl();
    ProjectDAO projectDAO;
    BacklogDAO backlogDAO;
    AssignmentDAO assignmentDAO;
    UserBusiness userBusiness;
    TransferObjectBusiness transferObjectBusiness;
    
    Project project;
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
        
        userBusiness = createStrictMock(UserBusiness.class);
        projectBusiness.setUserBusiness(userBusiness);
        
        assignmentDAO = createStrictMock(AssignmentDAO.class);
        projectBusiness.setAssignmentDAO(assignmentDAO);
        
        transferObjectBusiness = createStrictMock(TransferObjectBusiness.class);
        projectBusiness.setTransferObjectBusiness(transferObjectBusiness);
    }
    
    @Before
    public void setUp_data() {
        project = new Project();
        project.setId(123);
        project.setStartDate(new DateTime(2008,1,1,12,0,0,0).toDate());
        project.setEndDate(new DateTime(2008,3,1,12,0,0,0).toDate());
        
        user1 = new User();
        user1.setId(1);
        user2 = new User();
        user2.setId(2);
        
        story1 = new Story();
        story1.setId(127);
        story2 = new Story();
        story2.setId(130);
        story2.setResponsibles(Arrays.asList(user2));
        
        Task task = new Task();
        task.setId(86);
        task.setResponsibles(Arrays.asList(user1));
        story1.setTasks(Arrays.asList(task));
    }
    
    @Test
    public void testGetProjectContents() {
        Collection<User> assignees = Arrays.asList(user1);
        
        expect(projectDAO.get(project.getId())).andReturn(project);
        expect(projectDAO.getAssignedUsers(project)).andReturn(assignees);
        expect(transferObjectBusiness.constructBacklogDataWithUserData(project, assignees))
            .andReturn(Arrays.asList(new StoryTO(story1), new StoryTO(story2)));
        replay(projectDAO, transferObjectBusiness);
        
        ProjectDataContainer actualData = projectBusiness.getProjectContents(project.getId());
        
        assertEquals(2, actualData.getStories().size());
        assertEquals(127, actualData.getStories().get(0).getId());
        assertEquals(130, actualData.getStories().get(1).getId());
        
        verify(projectDAO, transferObjectBusiness);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testGetProjectContents_emptyProject() {
        Collection<User> assignees = Arrays.asList(user1);
        
        expect(projectDAO.get(project.getId())).andReturn(project);
        expect(projectDAO.getAssignedUsers(project)).andReturn(assignees);
        expect(transferObjectBusiness.constructBacklogDataWithUserData(project, assignees))
            .andReturn(Collections.EMPTY_LIST);
        replay(projectDAO, transferObjectBusiness);
        
        ProjectDataContainer actualData = projectBusiness.getProjectContents(project.getId());
        
        assertEquals(0, actualData.getStories().size());
        
        verify(projectDAO, transferObjectBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testGetProjectContents_nonExistentProject() {
        expect(projectDAO.get(-1)).andReturn(null);
        replay(projectDAO);
        
        projectBusiness.getProjectContents(-1);
        
        verify(projectDAO);
    }
    
    @Test
    public void testGetProjectMetrics() {
        expect(backlogDAO.calculateStoryPointSumIncludeChildBacklogs(project.getId()))
            .andReturn(100);
        replay(backlogDAO);
        
        ProjectMetrics actualMetrics = projectBusiness.getProjectMetrics(project);
        
        assertEquals(100, actualMetrics.getStoryPoints());
        
        verify(backlogDAO);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetProjectMetrics_nullProject() {
        projectBusiness.getProjectMetrics(null);
    }
    
    @Test
    public void testStoreProject_oldProject() {
        expect(projectDAO.get(project.getId())).andReturn(project);
        projectDAO.store(project);
        replay(projectDAO, assignmentDAO);
        assertEquals(project, projectBusiness.storeProject(project, null));
        verify(projectDAO, assignmentDAO);
    }
    
    @Test
    public void testStoreProject_newProject() {
        project.setId(0);
        expect(projectDAO.create(project)).andReturn(123);
        expect(projectDAO.get(123)).andReturn(project);
        replay(projectDAO, assignmentDAO);
        assertEquals(project, projectBusiness.storeProject(project, null));
        verify(projectDAO, assignmentDAO);
    }
    
    @Test( expected = IllegalArgumentException.class )
    public void testStoreProject_invalidDates() {
        project.setStartDate(new DateTime(2008,1,1,1,0,0,0).toDate());
        project.setEndDate(new DateTime(2007,1,1,1,0,0,0).toDate());
        
        expect(projectDAO.get(project.getId())).andReturn(project);
        projectDAO.store(project);
        replay(projectDAO, assignmentDAO);
        projectBusiness.storeProject(project, null);
        verify(projectDAO, assignmentDAO);
    }
    
    @Test
    public void testStoreProject_withAssignments() {
        Assignment ass = new Assignment();
        ass.setBacklog(project);
        ass.setPersonalLoad(new ExactEstimate(180));
        ass.setUser(user1);
        Collection<Assignment> assignments = Arrays.asList(ass);
        
        expect(projectDAO.get(project.getId())).andReturn(project);
        projectDAO.store(project);
        assignmentDAO.store(ass);
        replay(projectDAO, assignmentDAO);
        
        Project actualProject = projectBusiness.storeProject(project, assignments);
        
        assertTrue("The assignments are mismatched", verifyAssignments(
                assignments, actualProject.getAssignments()));
        
        verify(projectDAO, assignmentDAO);
    }
    
    /** Checks that the assignment collections are equal. */
    private boolean verifyAssignments(Collection<Assignment> expected, Collection<Assignment> actual) {
        boolean allFound = true;
        for (Assignment exp : expected) {
            if (!actual.contains(exp)) {
                allFound = false;
                break;
            }
        }
        return allFound;
    }
    
    @Test( expected = ObjectNotFoundException.class )
    public void testStoreProject_illegalProjectId() {
        Project falseProject = new Project();
        falseProject.setId(4);
        expect(projectDAO.get(falseProject.getId())).andReturn(null);
        replay(projectDAO);
        projectBusiness.storeProject(falseProject, null);
        verify(projectDAO);
    }
    
    @Test
    public void testGetUsersAssignableToProject() {     
        expect(userBusiness.getEnabledUsers()).andReturn(Arrays.asList(user1, user2));
        expect(projectDAO.getAssignedUsers(project)).andReturn(Arrays.asList(user1));
        replay(userBusiness);
        replay(projectDAO);
        
        Collection<User> assignableUsers = projectBusiness.getUsersAssignableToProject(project);
        assertTrue("List does not contain an enabled user", assignableUsers.contains(user1));
        assertTrue("List does not contain an assigned user", assignableUsers.contains(user2));
        assertEquals("List probably contains duplicates", 2, assignableUsers.size());
        
        verify(userBusiness);
        verify(projectDAO);
    }
 
    @Test
    public void testGetAssignedUsers() {
        Collection<User> expected = Arrays.asList(user1, user2);
        expect(projectDAO.getAssignedUsers(project)).andReturn(expected);
        replay(projectDAO);
        
        assertSame("List doesn't contain expected users", expected, projectBusiness.getAssignedUsers(project));
        
        verify(projectDAO);
    }
}
