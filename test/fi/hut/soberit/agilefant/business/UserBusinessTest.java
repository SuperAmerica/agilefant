package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Date;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskStatus;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.UserAction;


/**
 * JUnit integration test case for testing userBusiness.
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
		User user =
			userDAO.get(testUtility.createUser("test0", "test"));
		SecurityUtil.setLoggedUser(user);
		
		/* Create context for backlog items */
		int productId = testUtility.createProduct("Test product");
		Product product = productDAO.get(productId);
		
		/* Create the backlog items */
		int bli1Id = testUtility.createBacklogItem(
				"Backlog item with not started status", product);
		int bli2Id = testUtility.createBacklogItem(
				"Backlog item with started status", product);
		int bli3Id = testUtility.createBacklogItem(
				"Backlog item with blocked status and assignee", product);
		int bli4Id = testUtility.createBacklogItem(
				"Backlog item with implemented status and assignee", product);
		int bli5Id = testUtility.createBacklogItem(
				"Backlog item with done status and assignee", product);
		
		BacklogItem bli1 = backlogItemDAO.get(bli1Id);
		BacklogItem bli2 = backlogItemDAO.get(bli2Id);
		BacklogItem bli3 = backlogItemDAO.get(bli3Id);
		BacklogItem bli4 = backlogItemDAO.get(bli4Id);
		BacklogItem bli5 = backlogItemDAO.get(bli5Id);
		
		/* Create placeholder tasks */
		Task ph1 = taskDAO.get(testUtility.createTask(
				user, "Placeholder 1", bli1));
		Task ph2 = taskDAO.get(testUtility.createTask(
				user, "Placeholder 2", bli2));
		Task ph3 = taskDAO.get(testUtility.createTask(
				user, "Placeholder 3", bli3));
		Task ph4 = taskDAO.get(testUtility.createTask(
				user, "Placeholder 4", bli4));
		Task ph5 = taskDAO.get(testUtility.createTask(
				user, "Placeholder 5", bli5));
		
		ph1.setStatus(TaskStatus.NOT_STARTED);
		ph2.setStatus(TaskStatus.STARTED);
		ph3.setStatus(TaskStatus.BLOCKED);
		ph4.setStatus(TaskStatus.IMPLEMENTED);
		ph5.setStatus(TaskStatus.DONE);
		
		ph3.setAssignee(user);
		ph4.setAssignee(user);
		ph5.setAssignee(user);
		
		/* Give backlog items their placeholders */
		bli1.setPlaceHolder(ph1);
		bli2.setPlaceHolder(ph2);
		bli3.setPlaceHolder(ph3);
		bli4.setPlaceHolder(ph4);
		bli5.setPlaceHolder(ph5);
		
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
		
		assertFalse("Failed: list should not contain backlog item with status not started",
				list.contains(bli1));
		assertFalse("Failed: list should not contain this backlog item with no assigned user",
				list.contains(bli2));
		assertTrue("Failed: list should contain this backlog item with status blocked and an assigned user",
				list.contains(bli3));
		assertTrue("Failed: list should contain this backlog item with status implemented and an assigned user",
				list.contains(bli4));
		assertFalse("Failed: list should not contain backlog item with status done and assigned user",
				list.contains(bli5));
		
		for (BacklogItem bli : list) {
			assertFalse("Failed: the list should not contain backlog items with not started status",
					bli.getPlaceHolder().getStatus() == TaskStatus.NOT_STARTED);
			assertFalse("Failed: the list should not contain backlog items with done status",
					bli.getPlaceHolder().getStatus() == TaskStatus.DONE);
		}
	}
	
	/**
	 * Clears the database from test data.
	 * We must clear database manually if we need to transactions to complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
		testUtility.clearDBStack();
	}
}