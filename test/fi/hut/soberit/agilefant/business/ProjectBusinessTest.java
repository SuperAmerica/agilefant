package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;


public class ProjectBusinessTest {

    ProjectBusinessImpl projectBusiness = new ProjectBusinessImpl();
    ProjectDAO projectDAO;
    AssignmentDAO assignmentDAO;
    UserBusiness userBusiness;
    Project project;
    User user1;
    User user2;
    
    @Before
    public void setUp() {
        project = new Project();
        project.setId(123);
        project.setStartDate(new DateTime(2008,1,1,12,0,0,0).toDate());
        project.setEndDate(new DateTime(2008,3,1,12,0,0,0).toDate());
        user1 = new User();
        user1.setId(1);
        user2 = new User();
        user2.setId(2);
        
        projectDAO = createMock(ProjectDAO.class);
        projectBusiness.setProjectDAO(projectDAO);
        
        userBusiness = createMock(UserBusiness.class);
        projectBusiness.setUserBusiness(userBusiness);
        
        assignmentDAO = createMock(AssignmentDAO.class);
        projectBusiness.setAssignmentDAO(assignmentDAO);
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
        ass.setProject(project);
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
