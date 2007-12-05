package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration test for BacklogItemAction.
 * 
 * @author rstrom
 */
public class BacklogItemActionTest extends SpringTestCase {
    /* Class under test */
    private BacklogItemAction action = null;

    private BacklogDAO backlogDAO = null;
    private BacklogItemDAO backlogItemDAO = null;

    /**
     * Test edit operation.
     */
    public void testEdit() {
        // test data
        Backlog backlog = new Project();
        int backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        int bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // test begins
        action.setBacklogItemId(bliId);
        assertNull(action.getBacklogItem());
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());
    }

    /**
     * Test store operation.
     */
    public void testStore() {
        // test data
        Backlog backlog = new Project();
        int backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        int bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // test begins
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());
        // update name and store
        action.getBacklogItem().setName("Updated Name");
        assertEquals("success", action.store());
        assertEquals("Updated Name", backlogItemDAO.get(bliId).getName());
    }

    /**
     * Test delete operation.
     */
    public void testDelete() {
        // test data
        Backlog backlog = new Project();
        int backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        int bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // test begins
        action.setBacklogItemId(bliId);
        assertEquals("success", action.delete());
        assertEquals("error", action.edit());
    }

    /**
     * Test create operation.
     */
    public void testCreate() {
        // test data
        Backlog backlog = new Project();
        int backlogId = (Integer) backlogDAO.create(backlog);

        // test begins
        action.setBacklogId(backlogId);
        assertEquals("success", action.create());
        assertNotNull(action.getBacklogItem());
        assertEquals(1, backlogDAO.get(backlogId).getBacklogItems().size());
    }

    /**
     * Test quickStoreBacklogItem used by tasklist.tag
     */
    public void testQuickStoreBacklogItem() {
        // test data
        Backlog backlog = new Project();
        int backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        int bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // test begins
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());
        // update status and effortLeft
        action.setState(State.DONE);
        action.setEffortLeft(new AFTime("2h 15min"));
        assertEquals("success", action.quickStoreBacklogItem());
        assertEquals(State.DONE, action.getBacklogItem().getState());
        assertEquals("2h 15min", action.getBacklogItem().getEffortLeft()
                .toString());
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogItemAction(BacklogItemAction action) {
        this.action = action;
    }
}
