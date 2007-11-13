package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WorkType;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

/**
 * JUnit integration test for PerformWorkAction.
 * <p>
 * To create a performed work event with PerformWorkAction, we need a full
 * fledging hierarchy of a backlog, a backlogitem and a task. The test needs to
 * create entities for all those. A product is used as the backlog. Creating the
 * test entities is done in methods beginning with createTest.
 * 
 * @author Turkka Äijälä
 */
public class PerformWorkActionTest extends SpringTestCase {

    private static final String ACTIVITYTYPE_NAME = "Test ActivityType Name";

    private static final String ACTIVITYTYPE_DESCRIPTION = "Test ActivityType Description";

    private static final int ACTIVITYTYPE_PERCENTAGE = 55;

    private static final String WORKTYPE_NAME = "Test WorkType Name";

    private static final String WORKTYPE_DESCRIPTION = "Test WorkType Description";

    private static final String PRODUCT_NAME = "Test Product Name";

    private static final String PRODUCT_DESCRIPTION = "Test Product Description";

    private static final String BACKLOGITEM_NAME = "Test BacklogItem Name";

    private static final String BACKLOGITEM_DESCRIPTION = "Test BacklogItem Description";

    private static final String TASK_NAME = "Test Task Name";

    private static final String TASK_DESCRIPTION = "Test Task Description";

    private static final String TASK_EFFORT = "10h";

    private static final String PERFORMWORK_EFFORT = "2h";

    private static final String PERFORMWORK_COMMENT = "Test PerformWork Comment";

    private static final String USER_LOGIN = "Test User Login";

    private static final String USER_FULLNAME = "Test User full name";

    // all these DAOs are needed to create the needed infrastructure for the
    // work event
    private PerformWorkAction performWorkAction;

    private UserAction userAction;

    private PerformedWorkDAO performedWorkDAO;

    private WorkTypeDAO workTypeDAO;

    private ActivityTypeDAO activityTypeDAO;

    private BacklogItemDAO backlogItemDAO;

    private ProductDAO productDAO;

    private TaskDAO taskDAO;

    private UserDAO userDAO;

    /**
     * Create a test ActivityType.
     */
    private ActivityType createTestActivityType(String name,
            String description, int percentage) {
        ActivityType activityType = new ActivityType();
        activityType.setName(name);
        activityType.setDescription(description);
        activityType.setTargetSpendingPercentage(percentage);

        activityTypeDAO.store(activityType);

        assertTrue("stored activitytype had an invalid id", activityType
                .getId() > 0);

        ActivityType result = activityTypeDAO.get(activityType.getId());

        assertNotNull("getting stored failed", result);
        assertEquals("stored activitytype had an invalid name", result
                .getName(), name);
        assertEquals("stored activitytype had an invalid description", result
                .getDescription(), description);
        assertEquals("stored activitytype had invalid spending percantage",
                result.getTargetSpendingPercentage(), percentage);

        return result;
    }

    /**
     * Create a test WorkType, under a activityType.
     */
    private WorkType createTestWorkType(String name, String description,
            ActivityType activityType) {
        WorkType workType = new WorkType();

        workType.setName(name);
        workType.setDescription(description);
        workType.setActivityType(activityType);

        workTypeDAO.store(workType);

        // test if it was created ok
        assertTrue("stored worktype had an invalid id", workType.getId() > 0);

        activityTypeDAO.refresh(activityType);

        WorkType result = workTypeDAO.get(workType.getId());

        // test that all fields were stored properly
        assertNotNull("getting stored worktype failed", result);
        assertEquals("stored worktype had an invalid name", result.getName(),
                name);
        assertEquals("stored worktype had an invalid description", result
                .getDescription(), description);
        assertEquals("stored worktype had invalid activitype", result
                .getActivityType().getId(), activityType.getId());

        return result;
    }

    /**
     * Create a test Product to function as a backlog.
     */
    private Product createTestProduct(String name, String description) {

        Product prod = new Product();

        prod.setDescription(description);
        prod.setName(name);

        productDAO.store(prod);

        assertTrue("stored product had an invalid id", prod.getId() > 0);

        Product result = productDAO.get(prod.getId());

        assertNotNull("getting stored product failed", result);
        assertEquals("stored product had an invalid name", result.getName(),
                name);
        assertEquals("stored product had an invalid description", result
                .getDescription(), description);

        return result;
    }

    /**
     * Create a test backlogitem under a backlog.
     */
    private BacklogItem createTestBacklogItem(Backlog backlog, String name,
            String description) {

        BacklogItem bli = new BacklogItem();

        bli.setBacklog(backlog);
        bli.setDescription(description);
        bli.setName(name);

        backlogItemDAO.store(bli);

        assertTrue("stored backlogitem had an invalid id", bli.getId() > 0);

        productDAO.refresh((Product) backlog);

        BacklogItem result = backlogItemDAO.get(bli.getId());

        assertNotNull("getting stored backlogitem failed", result);
        assertEquals("stored product had an invalid name", result.getName(),
                name);
        assertEquals("stored product had an invalid description", result
                .getDescription(), description);
        assertEquals("stored worktype had invalid backlog", result.getBacklog()
                .getId(), backlog.getId());

        return result;
    }

    /**
     * Create a test task under a backlogitem.
     */
    private Task createTestTask(BacklogItem bli, AFTime effortEstimate,
            String name, String description) {
        Task task = new Task();
        User creator = TestUtility.initUser(userAction, userDAO);

        task.setBacklogItem(bli);
        task.setDescription(description);
        task.setName(name);
        task.setEffortEstimate(effortEstimate);
        task.setCreator(creator);

        int taskId = (Integer) taskDAO.create(task); // Autounboxing in use

        assertTrue("stored backlogitem had an invalid id", task.getId() > 0);

        backlogItemDAO.refresh(bli);

        Task result = taskDAO.get(taskId);

        assertNotNull("getting stored task failed", result);
        assertEquals("stored task had an invalid name", result.getName(), name);
        assertEquals("stored task had an invalid description", result
                .getDescription(), description);
        assertEquals("stored task had invalid backlogitem", result
                .getBacklogItem().getId(), bli.getId());
        assertEquals("stored task had an invalid effort estimate", result
                .getEffortEstimate(), effortEstimate);
        assertNull("stored task had nonzero performed effort", result
                .getPerformedEffort());

        assertEquals("stored task had some events", task.getEvents().size(), 0);

        return result;
    }

    private User createTestUser(String login, String name) {
        User user = new User();
        user.setLoginName(login);
        user.setFullName(name);

        userDAO.store(user);

        assertTrue("stored user had an invalid id", user.getId() > 0);

        User result = userDAO.get(user.getId());

        assertEquals("stored task had an invalid login name", result
                .getLoginName(), login);
        assertEquals("stored task had an invalid login name", result
                .getFullName(), name);

        return result;
    }

    /**
     * Run the action method execute in performWorkAction, with testing for
     * success.
     */
    private void execute() {
        String result = performWorkAction.execute();
        assertEquals("execute() was unsuccessful", result, Action.SUCCESS);
    }

    /** Check that given collection of TaskEvents contains an event with given id */
    private boolean checkCollectionContainsId(
            Collection<? extends TaskEvent> collection, int id) {
        for (TaskEvent event : collection)
            if (event.getId() == id)
                return true;
        return false;
    }

    /** Find and return event with given id from a taskevent collection */
    private TaskEvent findEventWithId(
            Collection<? extends TaskEvent> collection, int id) {
        for (TaskEvent event : collection)
            if (event.getId() == id)
                return event;
        return null;
    }

    public void testExecute() {

        // create the entity hierarchy around our work event using createTest -
        // methods
        Product product = createTestProduct(PRODUCT_NAME, PRODUCT_DESCRIPTION);
        BacklogItem bli = createTestBacklogItem(product, BACKLOGITEM_NAME,
                BACKLOGITEM_DESCRIPTION);
        Task task = createTestTask(bli, new AFTime(AFTime.parse(TASK_EFFORT)),
                TASK_NAME, TASK_DESCRIPTION);
        ActivityType activityType = createTestActivityType(ACTIVITYTYPE_NAME,
                ACTIVITYTYPE_DESCRIPTION, ACTIVITYTYPE_PERCENTAGE);
        WorkType workType = createTestWorkType(WORKTYPE_NAME,
                WORKTYPE_DESCRIPTION, activityType);
        // User user = createTestUser(USER_LOGIN, USER_FULLNAME);

        PerformedWork performWork = performWorkAction.getEvent();

        Date current = new Date();

        // configure our work event
        performWork.setWorkType(workType);
        performWorkAction.setTaskId(task.getId());
        performWorkAction.setTask(task);
        performWork.setEffort(new AFTime(AFTime.parse(PERFORMWORK_EFFORT)));
        performWork.setComment(PERFORMWORK_COMMENT);
        performWork.setCreated(current);
        // performWork.setActor(user);

        execute();

        taskDAO.refresh(task);

        // see if task effort increased
        assertEquals("task effort did not increase after new work performed",
                task.getPerformedEffort(), new AFTime(AFTime
                        .parse(PERFORMWORK_EFFORT)));

        int workEventId = performWorkAction.getEvent().getId();

        // test that our event appeared in all the collections that can be
        // requested from the DAO
        assertTrue(
                "performed work for the task did not contain added work event",
                checkCollectionContainsId(performedWorkDAO
                        .getPerformedWork(task), workEventId));
        assertTrue(
                "performed work for the backlogitem did not contain added work event",
                checkCollectionContainsId(performedWorkDAO
                        .getPerformedWork(bli), workEventId));
        assertTrue("events for the task did not contain added work event",
                checkCollectionContainsId(task.getEvents(), workEventId));

        // since we don't have a getter in the DAO for single events by id, get
        // a fresh copy of our event this way
        PerformedWork ourEvent = (PerformedWork) findEventWithId(task
                .getEvents(), workEventId);

        // see if the event has proper contents
        assertEquals("work event had invalid task", ourEvent.getTask().getId(),
                task.getId());
        assertEquals("work event had invalid effort", ourEvent.getEffort(),
                new AFTime(AFTime.parse(PERFORMWORK_EFFORT)));
        assertEquals("work event had invalid comment", PERFORMWORK_COMMENT,
                ourEvent.getComment());
        // eh, test that the difference is no more than 1h :) Where does this
        // value actually get set for an event?
        assertTrue("work event had invalid creation date", Math.abs(current
                .getTime()
                - ourEvent.getCreated().getTime()) < 1 * 60 * 60 * 1000);
        assertEquals("work event had invalid work type", workType.getId(),
                ourEvent.getWorkType().getId());
        // assertEquals("work event had invalid work type", user.getId(),
        // ourEvent.getActor().getId());
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
        this.activityTypeDAO = activityTypeDAO;
    }

    public void setPerformedWorkDAO(PerformedWorkDAO performWorkDAO) {
        this.performedWorkDAO = performWorkDAO;
    }

    public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
        this.workTypeDAO = workTypeDAO;
    }

    public void setPerformWorkAction(PerformWorkAction performWorkAction) {
        this.performWorkAction = performWorkAction;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }
}
