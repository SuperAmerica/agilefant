package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;


public class ProjectBusinessTest {

    ProjectBusinessImpl projectBusiness = new ProjectBusinessImpl();
    ProjectDAO projectDAO;
    UserBusiness userBusiness;
    
    @Before
    public void setUp() {
        projectDAO = createMock(ProjectDAO.class);
        projectBusiness.setProjectDAO(projectDAO);
        userBusiness = createMock(UserBusiness.class);
        projectBusiness.setUserBusiness(userBusiness);
    }
    
    @Test
    public void testGetUsersAssignableToProject() {
        Project project = new Project();
        // Belongs to project
        User user1 = new User();
        user1.setId(1);
        // Doesn't belog to project
        User user2 = new User();
        user2.setId(2);
        
        expect(userBusiness.getEnabledUsers()).andReturn(Arrays.asList(user1, user2));
        projectBusiness.getAssignedUsers(project);
        replay(userBusiness);
        
        Collection<User> assignableUsers = projectBusiness.getUsersAssignableToProject(project);
        assertTrue("List does not contain an enabled user", assignableUsers.contains(user1));
        assertTrue("List does not contain an assigned user", assignableUsers.contains(user2));
        assertEquals("List probably contains duplicates", 2, assignableUsers.size());
        
        verify(userBusiness);
    }
 
    @Test
    public void testGetAssignedUsers() {
        assertNotNull(projectBusiness.getAssignedUsers(null));
        assertTrue("TODO: Write working test and code to getAssignedUsers",false);
    }
}
