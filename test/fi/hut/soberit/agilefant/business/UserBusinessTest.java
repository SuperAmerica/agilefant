package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

/**
 * JUnit integration test case for testing userBusiness.
 * 
 * @author rjokelai
 * 
 */
public class UserBusinessTest extends SpringTestCase {

    private TestUtility testUtility;

    private UserDAO userDAO;

    private ProductDAO productDAO;

    private BacklogItemDAO backlogItemDAO;

    private TaskDAO taskDAO;

    private UserBusiness userBusiness;

    public TestUtility getTestUtility() {
        return testUtility;
    }

    public void setTestUtility(TestUtility testUtility) {
        this.testUtility = testUtility;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    /**
     * A method for testing the getBacklogItemsInProgress method of class
     * UserBusiness.
     */
    public void testGetBacklogItemsInProgressForUser() {
        super.setComplete();

        testUtility.setCleanup(true);
        User user = userDAO.get(testUtility.createUser("test0", "test"));
        SecurityUtil.setLoggedUser(user);

        /* Create context for backlog items */
        int productId = testUtility.createProduct("Test product");
        Product product = productDAO.get(productId);

        /* Create the backlog items */
        int bli1Id = testUtility.createBacklogItem(
                "Backlog item with not started state", product);
        int bli2Id = testUtility.createBacklogItem(
                "Backlog item with started state", product);
        int bli3Id = testUtility.createBacklogItem(
                "Backlog item with blocked state and assignee", product);
        int bli4Id = testUtility.createBacklogItem(
                "Backlog item with implemented state and assignee", product);
        int bli5Id = testUtility.createBacklogItem(
                "Backlog item with done state and assignee", product);

        BacklogItem bli1 = backlogItemDAO.get(bli1Id);
        BacklogItem bli2 = backlogItemDAO.get(bli2Id);
        BacklogItem bli3 = backlogItemDAO.get(bli3Id);
        BacklogItem bli4 = backlogItemDAO.get(bli4Id);
        BacklogItem bli5 = backlogItemDAO.get(bli5Id);

        /* Create placeholder tasks */
        Task ph1 = taskDAO.get(testUtility.createTask(user, "Placeholder 1",
                bli1));
        Task ph2 = taskDAO.get(testUtility.createTask(user, "Placeholder 2",
                bli2));
        Task ph3 = taskDAO.get(testUtility.createTask(user, "Placeholder 3",
                bli3));
        Task ph4 = taskDAO.get(testUtility.createTask(user, "Placeholder 4",
                bli4));
        Task ph5 = taskDAO.get(testUtility.createTask(user, "Placeholder 5",
                bli5));

        bli1.setState(State.NOT_STARTED);
        bli2.setState(State.STARTED);
        bli3.setState(State.BLOCKED);
        bli4.setState(State.IMPLEMENTED);
        bli5.setState(State.DONE);
        
        bli1.setPriority(Priority.BLOCKER);
        bli2.setPriority(Priority.BLOCKER);
        bli3.setPriority(Priority.BLOCKER);
        bli4.setPriority(Priority.BLOCKER);
        bli5.setPriority(Priority.BLOCKER);

        
        /* Set the assignees */
        bli3.setAssignee(user);
        bli4.setAssignee(user);
        bli5.setAssignee(user);

        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        /* Refetch the test backlog items from the mysql database */
        bli1 = backlogItemDAO.get(bli1Id);
        bli2 = backlogItemDAO.get(bli2Id);
        bli3 = backlogItemDAO.get(bli3Id);
        bli4 = backlogItemDAO.get(bli4Id);
        bli5 = backlogItemDAO.get(bli5Id);

        List<BacklogItem> list = userBusiness.getBacklogItemsInProgress(user);

        assertFalse("Failed: list is empty", list.isEmpty());

        assertFalse(
                "Failed: list should not contain backlog item with state not started",
                list.contains(bli1));
        assertFalse(
                "Failed: list should not contain this backlog item with no assigned user",
                list.contains(bli2));
        assertTrue(
                "Failed: list should contain this backlog item with state blocked and an assigned user",
                list.contains(bli3));
        assertTrue(
                "Failed: list should contain this backlog item with state implemented and an assigned user",
                list.contains(bli4));
        assertFalse(
                "Failed: list should not contain backlog item with state done and assigned user",
                list.contains(bli5));

        for (BacklogItem bli : list) {
            assertFalse(
                    "Failed: the list should not contain backlog items with not started state",
                    bli.getState() == State.NOT_STARTED);
            assertFalse(
                    "Failed: the list should not contain backlog items with done state",
                    bli.getState() == State.DONE);
        }
    }

    /**
     * Clears the database from test data. We must clear database manually if we
     * need to transactions to complete.
     */
    protected void onTearDownInTransaction() throws Exception {
        testUtility.clearDBStack();
    }
}