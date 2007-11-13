package fi.hut.soberit.agilefant.model;

import java.sql.Date;
import java.util.GregorianCalendar;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.TaskAction;
import fi.hut.soberit.agilefant.web.UserAction;

public class EffortHistoryTest extends SpringTestCase {
	private EffortHistoryDAO effortHistoryDAO;

	private ProductDAO productDAO;

	private BacklogItemDAO backlogItemDAO;

	private BacklogDAO backlogDAO;

	private BacklogItemAction backlogItemAction;

	private TaskAction taskAction;

	private UserAction userAction;

	private UserDAO userDAO;

	private TaskDAO taskDAO;

	private TaskEventDAO taskEventDAO;

	/**
	 * Setter for Spring IoC <br/> Injected effortHistory item DAO is used for
	 * testing
	 * 
	 * @param effortHistoryDAO
	 *            effortHistory item DAO to be set
	 */
	public void setEffortHistoryDAO(EffortHistoryDAO effortHistoryDAO) {
		this.effortHistoryDAO = effortHistoryDAO;
	}

	/**
	 * Setter for Spring IoC <br/> Injected product item DAO is used for testing
	 * 
	 * @param productDAO
	 *            product item DAO to be set
	 */
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	/**
	 * Clears the database from test data. We must clear database manually
	 * because of we need transactions to complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
	}

	public void testDAO() {
		Integer id = new Integer(0);
		setComplete();

		EffortHistory effortHistory = new EffortHistory();
		Date date = new Date((new GregorianCalendar()).getTimeInMillis());
		Product[] productArray;

		/* Check initial conditions */
		assertNotNull("EffortHistory DAO not injected", effortHistoryDAO);

		/* Test creation */
		TestUtility.createTestProduct(0, productDAO);
		productArray = (Product[]) productDAO.getAll().toArray(new Product[0]);

		effortHistory.setBacklog(productArray[0]);
		effortHistory.setDate(date);
		effortHistory.setEffortLeft(new AFTime(600000));
		effortHistory.setOriginalEstimate(new AFTime(600000));

		id = (Integer) effortHistoryDAO.create(effortHistory);
		assertTrue("Id wasn't returned", id.intValue() != 0);

		effortHistory = effortHistoryDAO.getByDateAndBacklog(date,
				productArray[0]);

		assertTrue("Could not retrieve effortHistory object from database",
				effortHistoryDAO.getAll().size() != 0);

		assertNotNull("EffortHistory retrieval by current date failed",
				effortHistory);

		assertEquals("Mismatch in effort left value", effortHistory
				.getEffortLeft(), new AFTime(600000));

		endTransaction();
		startNewTransaction();
		setComplete();

		/* Test connection from backlog to effortHistory */
		productArray = (Product[]) productDAO.getAll().toArray(new Product[0]);
		assertNotNull("ProductArray is empty", productArray);

		assertFalse("product.effortHistory is empty", productArray[0]
				.getEffortHistory().isEmpty());

		/* Test cascade delete */
		productDAO.remove(productArray[0]);
		assertTrue("Cascade delete failed", effortHistoryDAO.getAll().isEmpty());

		/* Clear database from effort history testdata */
		for (EffortHistory i : effortHistoryDAO.getAll()) {
			effortHistoryDAO.remove(i);
		}

		assertTrue("EffortHistory not removed", effortHistoryDAO.getAll()
				.isEmpty());

		endTransaction();
		startNewTransaction();
		super.setComplete();
		TestUtility.clearData(productDAO, effortHistoryDAO);
	}

	/**
	 * @return the backlogItemAction
	 */
	public BacklogItemAction getBacklogItemAction() {
		return backlogItemAction;
	}

	/**
	 * @param backlogItemAction
	 *            the backlogItemAction to set
	 */
	public void setBacklogItemAction(BacklogItemAction backlogItemAction) {
		this.backlogItemAction = backlogItemAction;
	}

	/**
	 * @return the backlogItemDAO
	 */
	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	/**
	 * @param backlogItemDAO
	 *            the backlogItemDAO to set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	/**
	 * @return the taskAction
	 */
	public TaskAction getTaskAction() {
		return taskAction;
	}

	/**
	 * @param taskAction
	 *            the taskAction to set
	 */
	public void setTaskAction(TaskAction taskAction) {
		this.taskAction = taskAction;
	}

	/**
	 * @return the backlogDAO
	 */
	public BacklogDAO getBacklogDAO() {
		return backlogDAO;
	}

	/**
	 * @param backlogDAO
	 *            the backlogDAO to set
	 */
	public void setBacklogDAO(BacklogDAO backlogDAO) {
		this.backlogDAO = backlogDAO;
	}

	/**
	 * @return the userDAO
	 */
	public UserDAO getUserDAO() {
		return userDAO;
	}

	/**
	 * @param userDAO
	 *            the userDAO to set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @return the userAction
	 */
	public UserAction getUserAction() {
		return userAction;
	}

	/**
	 * @param userAction
	 *            the userAction to set
	 */
	public void setUserAction(UserAction userAction) {
		this.userAction = userAction;
	}

	/**
	 * @return the taskDAO
	 */
	public TaskDAO getTaskDAO() {
		return taskDAO;
	}

	/**
	 * @param taskDAO
	 *            the taskDAO to set
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
	 *            the taskEventDAO to set
	 */
	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}
}
