package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration test for BacklogItemAction.
 * 
 * @author rstrom
 */
public class BacklogItemActionTest extends SpringTestCase {
    // class under test
    private BacklogItemAction action = null;

    private BacklogDAO backlogDAO = null;
    private BacklogItemDAO backlogItemDAO = null;
    private TaskDAO taskDAO = null;
    private IterationGoalDAO iterationGoalDAO = null;
    private UserDAO userDAO = null;
//    private UserBusiness userBusiness = null;

    private int backlogId;
    private int bliId;
    private int taskId;
    private int goalId;
    private int userId;

    /**
     * Create test data.
     */
    public void onSetUpInTransaction() throws Exception {
        // create iteration and bli
        Backlog backlog = new Iteration();
        backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // set bli fields
        bli.setName("Test Name");
        bli.setDescription("Test Description");
        bli.setPriority(Priority.BLOCKER);
        bli.setOriginalEstimate(new AFTime("2h 15min"));
        bli.setState(State.BLOCKED);
        bli.setEffortLeft(new AFTime("2h 15min"));
        backlogItemDAO.store(bli);

        // set bli tasks
        ArrayList<Task> tasks = new ArrayList<Task>();
        Task task = new Task();
        task.setName("Test Task");
        task.setBacklogItem(bli);
        taskId = (Integer) taskDAO.create(task);
        task = taskDAO.get(taskId);
        tasks.add(task);
        bli.setTasks(tasks);
        backlogItemDAO.store(bli);

        // set bli goal
        IterationGoal goal = new IterationGoal();
        goal.setName("Test Goal");
        goal.setIteration((Iteration) backlog);
        goalId = (Integer) iterationGoalDAO.create(goal);
        goal = iterationGoalDAO.get(goalId);
        bli.setIterationGoal(goal);
        backlogDAO.store(backlog);
        backlogItemDAO.store(bli);

        // set bli assignee
        User user = new User();
        user.setLoginName("Test User");
        userId = (Integer) userDAO.create(user);
        user = userDAO.get(userId);
        bli.setAssignee(user);
        backlogItemDAO.store(bli);
    }

    /**
     * Test edit operation.
     */
    @SuppressWarnings("unchecked")
    public void testEdit() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());

        assertEquals(bliId, action.getBacklogItem().getId());
        assertEquals("Test Name", action.getBacklogItem().getName());
        assertEquals("Test Description", action.getBacklogItem()
                .getDescription());
        assertEquals(Priority.BLOCKER, action.getBacklogItem().getPriority());
        assertEquals("2h 15min", action.getBacklogItem().getOriginalEstimate()
                .toString());
        assertEquals("Test Task", ((Task) ((List) action.getBacklogItem()
                .getTasks()).get(0)).getName());
        assertEquals(State.BLOCKED, action.getBacklogItem().getState());
        assertEquals("2h 15min", action.getBacklogItem().getEffortLeft()
                .toString());
        assertEquals("Test Goal", action.getBacklogItem().getIterationGoal()
                .getName());
        assertEquals("Test User", action.getBacklogItem().getAssignee()
                .getLoginName());
    }

    /**
     * Test edit operation with invalid bli id.
     */
    public void testEdit_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.edit());
    }

    /**
     * Test store operation.
     * 
     * TODO: store operation with invalid id. operation needs to be refactored
     * first to get rid of fillStorable cludge.
     */
    public void testStore() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli and execute store
        action.getBacklogItem().setName("Updated");
        action.getBacklogItem().setDescription("Updated");
        action.getBacklogItem().setPriority(Priority.CRITICAL);
        action.getBacklogItem().setOriginalEstimate(new AFTime("1h 15min"));
        action.getBacklogItem().setState(State.IMPLEMENTED);
        action.getBacklogItem().setEffortLeft(new AFTime("1h 15min"));
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that fields are updated
        assertEquals("Updated", backlogItemDAO.get(bliId).getName());
        assertEquals("Updated", backlogItemDAO.get(bliId).getDescription());
        assertEquals(Priority.CRITICAL, backlogItemDAO.get(bliId).getPriority());
        assertEquals("1h 15min", backlogItemDAO.get(bliId)
                .getOriginalEstimate().toString());
        assertEquals(State.IMPLEMENTED, backlogItemDAO.get(bliId).getState());
        assertEquals("1h 15min", backlogItemDAO.get(bliId).getEffortLeft()
                .toString());
    }

    /**
     * Test store operation with updating tasks.
     */
    @SuppressWarnings("unchecked")
    public void testStore_updateTasks() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli tasks and execute store
        List<Task> tasks = (List) action.getBacklogItem().getTasks();
        // edit existing task
        Task task = tasks.get(0);
        task.setName("Updated");
        // create new task
        Task task2 = new Task();
        task2.setName("Test Task2");
        task2.setBacklogItem(action.getBacklogItem());
        int task2Id = (Integer) taskDAO.create(task2);
        task2 = taskDAO.get(task2Id);
        tasks.add(task2);
        // execute store operation
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that tasks are updated
        assertEquals("Updated", ((Task) ((List) backlogItemDAO.get(bliId)
                .getTasks()).get(0)).getName());
        assertEquals("Test Task2", ((Task) ((List) backlogItemDAO.get(
                bliId).getTasks()).get(1)).getName());
    }

    /**
     * Test store operation with updating goal.
     */
    public void testStore_updateGoal() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli goal
        IterationGoal goal2 = new IterationGoal();
        goal2.setName("Test Goal2");
        goal2.setIteration((Iteration) backlogDAO.get(backlogId));
        int goal2Id = (Integer) iterationGoalDAO.create(goal2);
        goal2 = iterationGoalDAO.get(goal2Id);
        action.getBacklogItem().setIterationGoal(goal2);

        // execute store operation
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that goal is updated
        assertEquals("Test Goal2", backlogItemDAO.get(bliId).getIterationGoal()
                .getName());
    }

	/**
     * Test delete operation.
     */
    public void testDelete() {
        // execute delete operation
        assertNotNull(backlogItemDAO.get(bliId));
        action.setBacklogItemId(bliId);
        assertEquals("success", action.delete());
        // check that bli was deleted from database
        assertNull(backlogItemDAO.get(bliId));
    }

    /**
     * Test delete operation with invalid bli id.
     */
    public void testDelete_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.delete());
    }

    /**
     * Test create operation.
     */
    public void testCreate() {
        // execute create operation
        assertNull(action.getBacklogItem());
        assertEquals(1, backlogDAO.get(backlogId).getBacklogItems().size());
        action.setBacklogId(backlogId);
        assertEquals("success", action.create());
        assertNotNull(action.getBacklogItem());
        // check that new bli was created to backlog
        assertEquals(2, backlogDAO.get(backlogId).getBacklogItems().size());
    }

    /**
     * Test create operation with invalid backlog id.
     */
    public void testCreate_invalidId() {
        action.setBacklogId(-500);
        assertEquals("error", action.create());
    }

    /**
     * Test quickStoreBacklogItem used by tasklist.tag
     */

    public void testQuickStoreTaskList() {
        // execute edit operation
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());
        // update status and effortLeft
        action.setState(State.PENDING);
        action.setEffortLeft(new AFTime("3h 15min"));

        // Get first task from backlog item
        Task task = backlogItemDAO.get(bliId).getTasks().iterator().next();
        // Create Map of new task states
        Map<Integer, State> newTaskStates = new HashMap<Integer, State>();
        newTaskStates.put(task.getId(), State.PENDING);
        action.setTaskStates(newTaskStates);

        // execute quickStoreBacklogItem operation
        assertEquals("ajax_success", action.quickStoreTaskList());
        // check that bli was updated both to action and database
        assertEquals(State.PENDING, action.getBacklogItem().getState());
        assertEquals("3h 15min", action.getBacklogItem().getEffortLeft()
                .toString());
        assertEquals(State.PENDING, action.getTaskStates().get(task.getId()));

        assertEquals(State.PENDING, backlogItemDAO.get(bliId).getState());
        assertEquals("3h 15min", backlogItemDAO.get(bliId).getEffortLeft()
                .toString());
        // Check that the new status is updated for the task
        assertEquals(State.PENDING, backlogItemDAO.get(bliId).getTasks()
                .iterator().next().getState());

    }

    /**
     * Test create operation with invalid backlog id.
     */
    public void testQuickStoreTaskList_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("ajax_error", action.quickStoreTaskList());
    }

    /**
     * Test ResetBliOrigEstAndEffortLeft operation.
     */
    public void testResetBliOrigEstAndEffortLeft() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // execute reset operation
        action.resetBliOrigEstAndEffortLeft();

        // check that original estimate and effort left are null
        assertEquals(null, backlogItemDAO.get(bliId).getOriginalEstimate());
        assertEquals(null, backlogItemDAO.get(bliId).getEffortLeft());
    }

    /**
     * Test reset operation with invalid bli id.
     */
    public void testResetBliOrigEstAndEffortLeft_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.resetBliOrigEstAndEffortLeft());
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

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setAction(BacklogItemAction action) {
        this.action = action;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public void setBliId(int bliId) {
        this.bliId = bliId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
