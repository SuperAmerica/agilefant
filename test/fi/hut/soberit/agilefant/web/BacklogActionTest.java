package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration test for BacklogAction.
 * 
 * @author
 */
public class BacklogActionTest extends SpringTestCase {
    // class under test
    private BacklogAction backlogAction = null;

    private BacklogDAO backlogDAO = null;
    private BacklogItemDAO backlogItemDAO = null;

    private Backlog product;
    private Backlog project;
    private Backlog iteration;
    private BacklogItem bli1;
    private BacklogItem bli2;
    private int[] bliIds = new int[2];

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
        project.setId((Integer) backlogDAO.create(project));
        ((Project) project).setProduct((Product) product);
        ArrayList<Project> projects = new ArrayList<Project>();
        projects.add((Project) project);
        ((Product) product).setProjects(projects);
        backlogDAO.store(project);
        backlogDAO.store(product);
        project = backlogDAO.get(project.getId());
        // create iteration to project
        iteration = new Iteration();
        iteration.setId((Integer) backlogDAO.create(iteration));
        ((Iteration) iteration).setProject((Project) project);
        ArrayList<Iteration> iterations = new ArrayList<Iteration>();
        iterations.add((Iteration) iteration);
        ((Project) project).setIterations(iterations);
        backlogDAO.store(iteration);
        backlogDAO.store(project);
        iteration = backlogDAO.get(iteration.getId());
        // create test blis to product
        bli1 = new BacklogItem();
        bli2 = new BacklogItem();
        bli1.setBacklog(backlogDAO.get(product.getId()));
        bli2.setBacklog(backlogDAO.get(product.getId()));
        bli1.setId((Integer) backlogItemDAO.create(bli1));
        bli2.setId((Integer) backlogItemDAO.create(bli2));
        bli1 = backlogItemDAO.get(bli1.getId());
        bli2 = backlogItemDAO.get(bli2.getId());
        backlogDAO.get(product.getId()).getBacklogItems().add(bli1);
        backlogDAO.get(product.getId()).getBacklogItems().add(bli2);
        bliIds[0] = bli1.getId();
        bliIds[1] = bli2.getId();
    }

    /**
     * Test edit operation.
     */
    public void testEdit() {
        // test product
        backlogAction.setBacklogId(product.getId());
        assertEquals("editProduct", backlogAction.edit());
        // test project
        backlogAction.setBacklogId(project.getId());
        assertEquals("editProject", backlogAction.edit());
        // test iteration
        backlogAction.setBacklogId(iteration.getId());
        assertEquals("editIteration", backlogAction.edit());
    }

    /**
     * Test edit operation with invalid id.
     */
    public void testEdit_invalidId() {
        backlogAction.setBacklogId(-100);
        assertEquals("error", backlogAction.edit());
    }

    /**
     * Test MoveBacklogItem operation.
     */
    public void testMoveBacklogItem() {
        // move from product to project
        backlogAction.setBacklogItemId(bli1.getId());
        backlogAction.setBacklogId(project.getId());
        assertEquals("editProject", backlogAction.moveBacklogItem());
        assertEquals(project.getId(), backlogItemDAO.get(bli1.getId())
                .getBacklog().getId());
        assertFalse(backlogDAO.get(product.getId()).getBacklogItems().contains(
                bli1));
        assertTrue(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli1));

        // move from project to iteration
        backlogAction.setBacklogId(iteration.getId());
        assertEquals("editIteration", backlogAction.moveBacklogItem());
        assertEquals(iteration.getId(), backlogItemDAO.get(bli1.getId())
                .getBacklog().getId());
        assertFalse(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli1));
        assertTrue(backlogDAO.get(iteration.getId()).getBacklogItems()
                .contains(bli1));
    }

    /**
     * Test MoveBacklogItem operation with invalid bli id.
     */
    public void testMoveBacklogItem_invalidBliId() {
        backlogAction.setBacklogItemId(-100);
        backlogAction.setBacklogId(project.getId());
        assertEquals("error", backlogAction.moveBacklogItem());
    }

    /**
     * Test MoveBacklogItem operation with invalid backlog id.
     */
    public void testMoveBacklogItem_invalidBacklogId() {
        // execute move operation
        backlogAction.setBacklogItemId(bli1.getId());
        backlogAction.setBacklogId(-100);
        assertEquals("error", backlogAction.moveBacklogItem());
    }

    /**
     * Test MoveSelectedItems operation.
     */
    public void testMoveSelectedItems() {
        // move from product to project
        backlogAction.setBacklogId(product.getId());
        backlogAction.setTargetBacklog(project.getId());
        backlogAction.setSelected(bliIds);
        assertEquals("editProduct", backlogAction.moveSelectedItems());
        assertEquals(project.getId(), backlogItemDAO.get(bli1.getId())
                .getBacklog().getId());
        assertEquals(project.getId(), backlogItemDAO.get(bli2.getId())
                .getBacklog().getId());
        assertFalse(backlogDAO.get(product.getId()).getBacklogItems().contains(
                bli1));
        assertFalse(backlogDAO.get(product.getId()).getBacklogItems().contains(
                bli2));
        assertTrue(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli1));
        assertTrue(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli2));

        // move from project to iteration
        backlogAction.setBacklogId(project.getId());
        backlogAction.setTargetBacklog(iteration.getId());
        backlogAction.setSelected(bliIds);
        assertEquals("editProject", backlogAction.moveSelectedItems());
        assertEquals(iteration.getId(), backlogItemDAO.get(bli1.getId())
                .getBacklog().getId());
        assertEquals(iteration.getId(), backlogItemDAO.get(bli2.getId())
                .getBacklog().getId());
        assertFalse(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli1));
        assertFalse(backlogDAO.get(project.getId()).getBacklogItems().contains(
                bli2));
        assertTrue(backlogDAO.get(iteration.getId()).getBacklogItems()
                .contains(bli1));
        assertTrue(backlogDAO.get(iteration.getId()).getBacklogItems()
                .contains(bli2));

    }

    /**
     * Test MoveSelectedItems operation with invalid target backlog id.
     */
    public void testMoveSelectedItem_invalidTargetBacklogId() {
        // move from product to project
        backlogAction.setBacklogId(product.getId());
        backlogAction.setTargetBacklog(-100);
        backlogAction.setSelected(bliIds);
        assertEquals("error", backlogAction.moveSelectedItems());
    }

    /**
     * Test MoveSelectedItems operation with invalid current backlog id.
     */
    public void testMoveSelectedItem_invalidCurrentBacklogId() {
        // move from product to project
        backlogAction.setBacklogId(-100);
        backlogAction.setTargetBacklog(project.getId());
        backlogAction.setSelected(bliIds);
        assertEquals("error", backlogAction.moveSelectedItems());
    }

    /**
     * Test MoveSelectedItems operation with invalid bli ids.
     */
    public void testMoveSelectedItem_invalidBliIds() {
        backlogAction.setBacklogId(product.getId());
        backlogAction.setTargetBacklog(project.getId());
        assertEquals("error", backlogAction.moveSelectedItems());
    }

    /**
     * Test ChangePriorityOfSelectedItems operation.
     */
    public void testChangePriorityOfSelectedItems() {
        backlogAction.setBacklogId(product.getId());
        backlogAction.setTargetPriority(Priority.BLOCKER);
        backlogAction.setSelected(bliIds);

        // execute changepriorityofselecteditems operation
        assertEquals("editProduct", backlogAction
                .changePriorityOfSelectedItems());
        assertEquals(Priority.BLOCKER, backlogItemDAO.get(bli1.getId())
                .getPriority());
        assertEquals(Priority.BLOCKER, backlogItemDAO.get(bli2.getId())
                .getPriority());
    }

    /**
     * Test ChangePriorityOfSelectedItems operation with invalid priority.
     */
    public void testChangePriorityOfSelectedItems_invalidPriority() {
        backlogAction.setBacklogId(product.getId());
        backlogAction.setSelected(bliIds);

        // execute changepriorityofselecteditems operation
        assertEquals("error", backlogAction.changePriorityOfSelectedItems());
    }

    /**
     * Test ChangePriorityOfSelectedItems operation with invalid bli ids.
     */
    public void testChangePriorityOfSelectedItems_invalidBliIds() {
        backlogAction.setBacklogId(product.getId());
        backlogAction.setTargetPriority(Priority.BLOCKER);

        // execute changepriorityofselecteditems operation
        assertEquals("error", backlogAction.changePriorityOfSelectedItems());
        int[] ids = {-100, -200};
        backlogAction.setSelected(ids);
        assertEquals("error", backlogAction.changePriorityOfSelectedItems());
    }

    /**
     * Test ChangePriorityOfSelectedItems operation with invalid backlog id.
     */
    public void testChangePriorityOfSelectedItems_invalidBacklogId() {
        backlogAction.setTargetPriority(Priority.BLOCKER);
        backlogAction.setSelected(bliIds);

        // execute operation
        assertEquals("error", backlogAction.changePriorityOfSelectedItems());
    }

    /**
     * Test DeleteSelectedItems operation.
     */
    public void testDeleteSelectedItems() {
        backlogAction.setBacklogId(product.getId());
        backlogAction.setSelected(bliIds);

        // execute operation
        assertEquals("editProduct", backlogAction.deleteSelectedItems());
        assertNull(backlogItemDAO.get(bli1.getId()));
        assertNull(backlogItemDAO.get(bli2.getId()));
        assertFalse(backlogDAO.get(product.getId()).getBacklogItems().contains(
                bli1));
        assertFalse(backlogDAO.get(product.getId()).getBacklogItems().contains(
                bli2));
    }

    /**
     * Test DeleteSelectedItems operation with invalid bli ids.
     */
    public void testDeleteSelectedItems_invalidBliIds() {
        backlogAction.setBacklogId(product.getId());

        // execute operation
        assertEquals("error", backlogAction.deleteSelectedItems());
    }

    /**
     * Test DeleteSelectedItems operation with invalid backlog id.
     */
    public void testDeleteSelectedItems_invalidBacklogId() {
        backlogAction.setSelected(bliIds);

        // execute operation
        assertEquals("error", backlogAction.deleteSelectedItems());
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogAction(BacklogAction backlogAction) {
        this.backlogAction = backlogAction;
    }
}
