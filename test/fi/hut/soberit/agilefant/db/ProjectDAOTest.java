package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class ProjectDAOTest extends AbstractHibernateTests {

    @Autowired
    private ProjectDAO projectDAO;
    
    @Test
    public void testGetAssignedUsers_noAssignees() {
        executeClassSql();
        Project proj = new Project();
        proj.setId(3);
        assertEquals(0, projectDAO.getAssignedUsers(proj).size());
    }
    
    @Test
    public void testGetAssignedUsers_hasAssignees() {
        executeClassSql();
        Project proj = new Project();
        proj.setId(1);
        assertEquals(2, projectDAO.getAssignedUsers(proj).size());
    }
    
    @Test 
    public void testGetProjectsWithUserAssigned_noAssignments() {
        executeClassSql();
        User user = new User();
        user.setId(2);
        assertEquals(0, projectDAO.getProjectsWithUserAssigned(user).size());
    }
    
    @Test 
    public void testGetProjectsWithUserAssigned_hasAssignments() {
        executeClassSql();
        User user = new User();
        user.setId(1);
        assertEquals(2, projectDAO.getProjectsWithUserAssigned(user).size());
    }
    
    @Test 
    public void testGetProject() {
        executeClassSql();
        Project proj = projectDAO.get(1);
        assertNotNull(proj);
        assertEquals(2, proj.getAssignments().size());
    }
}
