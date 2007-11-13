package fi.hut.soberit.agilefant.model;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

/**
 * Class for testing backlog and backlog item metrics data.
 * 
 * @author vtheikki
 * 
 */
public class MetricsTest extends SpringTestCase {

    private TestUtility testUtility;

    private UserDAO userDAO;

    private BacklogItemDAO backlogItemDAO;

    private IterationDAO iterationDAO;

    private TaskDAO taskDAO;

    private TaskEventDAO taskEventDAO;

    private BacklogDAO backlogDAO;

    private Log logger = LogFactory.getLog(this.getClass());

    public void testBacklogMetrics() {
        super.setComplete();

        testUtility.setCleanup(true);
        User user = userDAO.get(testUtility.createUser("test0", "test"));
        SecurityUtil.setLoggedUser(user);

        /* Test with bli with one event after iteration start */
        Integer iterationId = testUtility.createIteration("TestIteration");
        Iteration iteration = iterationDAO.get(iterationId);

        BacklogItem backlogItem = backlogItemDAO.get(testUtility
                .createBacklogItem("test", iteration));

        Task task = taskDAO.get(testUtility.createTask(user, "test",
                backlogItem));

        task.setEffortEstimate(new AFTime(TestUtility.HOUR));
        testUtility.createEstimateHistory(user, task, new Date(System
                .currentTimeMillis()), new AFTime(TestUtility.HOUR));
        backlogItem.setPlaceHolder(task);

        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        /*
         * After completing the transaction, you need to refetch the iteration
         * to connection from iteration to backlog items to work!
         */
        iteration = iterationDAO.get(iterationId);

        BacklogValueInjector.injectMetrics(iteration, new Date(0),
                taskEventDAO, backlogItemDAO);

        assertEquals(1, iteration.getBacklogItems().size());
        assertEquals("Efffort left sum incorrect", new AFTime(TestUtility.HOUR)
                .getTime(), iteration.getBliEffortLeftSum().getTime());
        assertEquals("Original estimate sum incorrect", new AFTime(
                TestUtility.HOUR).getTime(), iteration.getBliOrigEstSum()
                .getTime());

        BacklogValueInjector.injectMetrics(iteration, new Date(System
                .currentTimeMillis()), taskEventDAO, backlogItemDAO);
    }

    /**
     * Clears the database from test data. We must clear database manually if we
     * need to transactions to complete.
     */
    protected void onTearDownInTransaction() throws Exception {
        testUtility.clearDBStack();
    }

    /**
     * @return the testUtility
     */
    public TestUtility getTestUtility() {
        return testUtility;
    }

    /**
     * @param testUtility
     *                the testUtility to set
     */
    public void setTestUtility(TestUtility testUtility) {
        this.testUtility = testUtility;
    }

    /**
     * @return the userDAO
     */
    public UserDAO getUserDAO() {
        return userDAO;
    }

    /**
     * @param userDAO
     *                the userDAO to set
     */
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * @return the backlogItemDAO
     */
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    /**
     * @param backlogItemDAO
     *                the backlogItemDAO to set
     */
    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    /**
     * @return the iterationDAO
     */
    public IterationDAO getIterationDAO() {
        return iterationDAO;
    }

    /**
     * @param iterationDAO
     *                the iterationDAO to set
     */
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    /**
     * @return the taskDAO
     */
    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    /**
     * @param taskDAO
     *                the taskDAO to set
     */
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /**
     * @return the taskEventDAO
     */
    public TaskEventDAO getTaskEventDAO() {
        return taskEventDAO;
    }

    /**
     * @param taskEventDAO
     *                the taskEventDAO to set
     */
    public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
        this.taskEventDAO = taskEventDAO;
    }

    /**
     * @return the backlogDAO
     */
    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    /**
     * @param backlogDAO
     *                the backlogDAO to set
     */
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
}
