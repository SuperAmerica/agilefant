package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration test for ProjectAction.
 * 
 * @author
 */
public class ProjectActionTest extends SpringTestCase {
    // Class under test
    private ProjectAction projectAction = null;

    private ProjectDAO projectDAO = null;
    private BacklogDAO backlogDAO = null;
    private ProjectTypeDAO projectTypeDAO = null;
    private BacklogItemDAO backlogItemDAO = null;
    private UserDAO userDAO = null;
    
    //private BacklogHourEntryDAO projectHourEntryDAO = null; 

    private Backlog product;
    private Backlog project;
    private ProjectType projectType;
    private User user1, user2;
    
    

    /**
     * Create test data.
     */
    public void onSetUpInTransaction() throws Exception {
        // create product
        product = new Product();
        product.setId((Integer) backlogDAO.create(product));
        product = backlogDAO.get(product.getId());
        // create project to product
        project = new Project();
        project.setName("Test Name");
        project.setDescription("Test Description");
        project.setId((Integer) backlogDAO.create(project));
        ((Project) project)
                .setStartDate("2007-11-13 06:00", "yyyy-MM-dd HH:mm");
        ((Project) project).setEndDate("2007-11-13 16:00", "yyyy-MM-dd HH:mm");
        ((Project) project).setProduct((Product) product);
        ArrayList<Project> projects = new ArrayList<Project>();
        projects.add((Project) project);
        ((Product) product).setProjects(projects);
        backlogDAO.store(project);
        backlogDAO.store(product);
        project = backlogDAO.get(project.getId());
        // create project type
        projectType = new ProjectType();
        projectType.setName("Test Name");
        ((Project) project).setProjectType(projectType);
        projectTypeDAO.store(projectType);
        backlogDAO.store(project);
        // create users
        user1 = new User();
        user1.setLoginName("user1");
        user2 = new User();
        user2.setLoginName("user2");
        user1.setId((Integer) userDAO.create(user1));
        user2.setId((Integer) userDAO.create(user2));
    }

    /**
     * Test create operation.
     */
    public void testCreate() {
        assertEquals("success", projectAction.create());
        assertNotNull(projectAction.getProject());
        assertNotNull(projectAction.getBacklog());
    }

    public void testCreate_withAssignments() {
        int[] selectedUserIds = {user1.getId(), user2.getId()};
        projectAction.setSelectedUserIds(selectedUserIds);
        projectAction.setProject((Project) project);
        projectAction.setProjectId(project.getId());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2007-11-14 06:00");
        projectAction.setProductId(product.getId());
        projectAction.setProjectTypeId(projectType.getId());
        assertEquals(0, project.getAssignments().size());
        assertEquals("success", projectAction.store());
        assertEquals(2, project.getAssignments().size());
    }
    
    public void testCreate_withInvalidAssignments() {
        int[] selectedUserIds = {-500, user2.getId()};
        projectAction.setSelectedUserIds(selectedUserIds);
        projectAction.setProject((Project) project);
        projectAction.setProjectId(project.getId());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2007-11-14 06:00");
        projectAction.setProductId(product.getId());
        projectAction.setProjectTypeId(projectType.getId());
        assertEquals(0, project.getAssignments().size());
        assertEquals("success", projectAction.store());
        assertEquals(1, project.getAssignments().size());
    }

    /**
     * Test edit operation.
     */
    public void testEdit() {
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        assertNotNull(projectAction.getProject());
        assertEquals("Test Name", projectAction.getProject().getName());
        assertEquals("Test Description", projectAction.getProject()
                .getDescription());
    }

    /**
     * Test edit operation with invalid project type.
     */
    public void testEdit_invalidProjectType() {
        ((Project) project).setProjectType(null);
        projectTypeDAO.remove(projectType.getId());
        projectAction.setProjectId(project.getId());
        assertEquals("error", projectAction.edit());
    }

    /**
     * Test edit operation with invalid project id.
     */
    public void testEdit_invalidProjectId() {
        assertEquals("error", projectAction.edit());
    }

    /**
     * Test store operation.
     * 
     * TODO: Refactor fillStorable and create test for invalid project id.
     */
    public void testStore() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());

        // update action fields
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2007-11-13 16:00");
        projectAction.getProject().setName("Updated Name");
        projectAction.getProject().setDescription("Updated Name");
        ProjectType projectType2 = new ProjectType();
        projectType2.setName("Updated ProjectType");
        projectType2.setId((Integer) projectTypeDAO.create(projectType2));
        projectAction.setProjectTypeId(projectType2.getId());
        projectTypeDAO.store(projectType2);

        // execute store operation
        assertEquals("success", projectAction.store());
    }

    /**
     * Test store operation with invalid start date.
     */
    public void testStore_invalidStartDate() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        projectAction.setEndDate("2008-11-13 06:00");
        projectAction.getProject().setName("Updated Name");

        // check for null startdate
        projectAction.setStartDate(null);
        assertEquals("error", projectAction.store());

        // check for invalid startdate
        projectAction.setStartDate("ABC");
        assertEquals("error", projectAction.store());
    }

    /**
     * Test store operation with invalid end date.
     */
    public void testStore_invalidEndDate() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.getProject().setName("Updated Name");

        // check for null enddate
        projectAction.setEndDate(null);
        assertEquals("error", projectAction.store());

        // check for invalid enddate
        projectAction.setEndDate("2007-11-13 06:00 ABC");
        assertEquals("error", projectAction.store());

    }

    /**
     * Test store operation with end date before start date.
     */
    public void testStore_invalidDates() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2006-11-13 06:00");
        projectAction.getProject().setName("Updated Name");

        // execute store operation
        assertEquals("error", projectAction.store());
    }

    /**
     * Test store operation with invalid name.
     */
    public void testStore_invalidName() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2008-11-13 06:00");

        // set invalid name
        projectAction.getProject().setName(null);

        // execute store operation
        assertEquals("error", projectAction.store());
    }

    /**
     * Test store operation with invalid project type.
     */
    public void testStore_invalidProjectType() {
        // execute edit operation
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.edit());
        projectAction.setStartDate("2007-11-13 06:00");
        projectAction.setEndDate("2008-11-13 06:00");
        projectAction.getProject().setName("Test Name");

        // set invalid ProjectType
        projectAction.setProjectTypeId(-100);

        // execute store operation
        assertEquals("error", projectAction.store());
    }

    /**
     * Test delete operation.
     */
    public void testDelete() {
        
        
        projectAction.setProjectId(project.getId());
        assertEquals("success", projectAction.delete());
        assertNull(projectDAO.get(project.getId()));
        assertFalse(((Product) backlogDAO.get(product.getId())).getProjects()
                .contains(project));
    }

    /**
     * Test delete operation with invalid id.
     */
    public void testDelete_invalidId() {
        projectAction.setProjectId(-100);
        assertEquals("error", projectAction.delete());
    }

    /**
     * Test delete operation with project that has blis.
     */
    public void testDelete_notEmpty() {
        projectAction.setProjectId(project.getId());
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(project);
        int bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        project.getBacklogItems().add(bli);
        assertEquals("error", projectAction.delete());
    }

    public void setProjectAction(ProjectAction projectAction) {
        this.projectAction = projectAction;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
