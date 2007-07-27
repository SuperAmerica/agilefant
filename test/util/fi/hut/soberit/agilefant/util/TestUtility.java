package fi.hut.soberit.agilefant.util;

import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.TaskAction;
import fi.hut.soberit.agilefant.web.UserAction;
import fi.hut.soberit.agilefant.web.UserActionTest;

/**
 * Utility class for testing
 * 
 */
public class TestUtility {
	private static Log logger;
	
	public static enum TestUser {
		USER1, USER2
	}
	
	private static Log getLogger() {
		if(logger == null) {
			logger = LogFactory.getLog(TestUtility.class);
		}
		return logger;
	}
	
	/**
	 * Createss a product for testing.
	 * 
	 * @param number number for identifying product from name
	 * @param productDAO data access object for product
	 */
	public static void createTestProduct(int number, ProductDAO productDAO) {
		Product product = new Product();
		
		product.setDescription("Product backlog for testing");
		product.setName("Product test backlog " + number);
		productDAO.store(product);
	}
	
	/**
	 * Createss a iteration for testing.
	 * 
	 * @param number number for identifying iteration from name
	 * @param iterationDAO data access object for iteration
	 */
	public static int createTestIteration(int number, IterationDAO iterationDAO) {
		Iteration iteration = new Iteration();
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.add(GregorianCalendar.MONTH, 1);
		
		iteration.setDescription("Iteration backlog for testing");
		iteration.setName("Iteration test backlog " + number);
		iteration.setStartDate(new GregorianCalendar().getTime());
		iteration.setEndDate(endDate.getTime());
		return (Integer)iterationDAO.create(iteration);
	}
	
	/**
	 * Create test backlog item without using action.
	 * 
	 * @param number identifier for backlog item name
	 * @param backlog backlog used 
	 * @param backlogItemDAO data access object for backlogItem
	 */
	public static void createBareTestItem(int number, Backlog backlog, 
			BacklogItemDAO backlogItemDAO) {
		BacklogItem backlogItem = new BacklogItem();
		
		backlogItem.setBacklog(backlog);
		backlogItem.setDescription("Backlog item for testing");
		backlogItem.setName("Test backlog " + number);
		backlogItemDAO.store(backlogItem);
	}
	
	/**
	 * Creates a test backlog item using action
	 * @param number identifier for backlog item name
	 * @param backlog backlog used 
	 * @param backlogItemDAO data access object for backlogItem
	 */
	public static String createTestItem(Backlog backlog, 
			BacklogItemAction backlogItemAction) {
		return createTestItem(backlog, backlogItemAction, 0);
	}
	
	/**
	 * Creates a test backlog item using action
	 * @param number identifier for backlog item name
	 * @param backlog backlog used 
	 * @param backlogItemAction action used in creation
	 * @return result of the storing action
	 */
	public static String createTestItem(Backlog backlog, 
			BacklogItemAction backlogItemAction, long originalEstimate) {
		backlogItemAction.create();
		backlogItemAction.setBacklog(backlog);
		backlogItemAction.setBacklogId(backlog.getId());
		backlogItemAction.setBacklogItemName("Test item");
		backlogItemAction.getBacklogItem().setAllocatedEffort(
				new AFTime(originalEstimate));
		return backlogItemAction.store();
	}
	
	/**
	 * Creates a test task using action
	 * @param backlogItem the backlog item this task belongs to
	 * @param backlogItemAction the action used in creation
	 * @param estimate the estimate for the task
	 * @return the ID of the task created
	 */
	public static int createTestTask(BacklogItem backlogItem,
			TaskAction taskAction, long estimate) {
		taskAction.create();
		taskAction.setBacklogItemId(backlogItem.getId());
		taskAction.getTask().setEffortEstimate(new AFTime(estimate));
		taskAction.getTask().setName("Test task");
		return taskAction.storeNew();
	}
	
	/**
	 * Create and log in a user. Do not call this twice in same test case.
	 *
	 * @param userAction action for user creation
	 * @param userDAO data access object for user
	 * @return initialized user
	 */
	public static User initUser(UserAction userAction, UserDAO userDAO) {
		return initUser(userAction, userDAO, TestUser.USER1);
	}
	
	/**
	 * Create and log in a use to use in testing. Two users are available.
	 *
	 * @param userAction action for user creation
	 * @param userDAO data access object for user
	 * @return initialized user
	 */
	public static User initUser(UserAction userAction, UserDAO userDAO, 
			TestUser userNumber) {
		User user = 
			UserActionTest.GenerateAndStoreTestUser(userAction, userDAO, 
					userNumber);
		SecurityUtil.setLoggedUser(user);
		return user;
	}
	

	
	/**
	 * Adds an estimate event to the estimate history of the given item. 
	 * Doesn't alter the effor left value of the task.
	 * 
	 * @param taskId the Id of the task to add effor history to
	 * @param taskDAO the task DAO to use
	 * @param estimate the new effort estimate in milliseconds
	 * @param taskEventDAO the task event DAO to use
	 * @param createdDate the created date to give the new event
	 */
	public static void addEstimate(int taskId, TaskDAO taskDAO,
			long estimate, TaskEventDAO taskEventDAO, Date createdDate) {
		Task task = taskDAO.get(taskId);
		EstimateHistoryEvent event = new EstimateHistoryEvent();
		TaskEvent taskEvent;
		event.setActor(SecurityUtil.getLoggedUser());
		event.setNewEstimate(new AFTime(estimate));
		event.setTask(task);
		task.getEvents().add(event);
		int eventId = (Integer)taskEventDAO.create(event);
		taskDAO.store(task);
		taskEvent = taskEventDAO.get(eventId);
		taskEvent.setCreated(createdDate);
		taskEventDAO.store(taskEvent);
		if(getLogger().isDebugEnabled()) {
			getLogger().debug("Task " + taskId + " Event " + eventId + 
					" created " + 
					taskEventDAO.get(eventId).getCreated() + 
					" estimate: " + new AFTime(estimate));
		}
	}
	
	/**
	 * Clears the database from test data.
	 * We must clear database by hand because of we need transactions to
	 * complete.
	 * 
	 * @param userDAO data access object for user
	 * @param backlogItem DAO data access object for backlog item 
	 * @param productDAO data access object for product
	 */
	public static void clearData(UserDAO userDAO, BacklogItemDAO backlogItemDAO,
			ProductDAO productDAO) throws Exception {
		for(User i: userDAO.getAll()) {
			userDAO.remove(i.getId());
		}
		
		for(BacklogItem i: backlogItemDAO.getAll()) {
			backlogItemDAO.remove(i.getId());
		}
		
		clearData(productDAO);
	}
	
	public static void clearData(ProductDAO productDAO,
			EffortHistoryDAO effortHistoryDAO) {
		clearData(effortHistoryDAO);
		clearData(productDAO);
	}
	
	public static void clearData(ProductDAO productDAO) {
		for(Product i: productDAO.getAll()) {
			productDAO.remove(i.getId());
		}
	}
	
	public static void clearData(EffortHistoryDAO effortHistoryDAO) {
		for(EffortHistory i: effortHistoryDAO.getAll()) {
			effortHistoryDAO.remove(i.getId());
		}	
	}
	
	public static void clearData(IterationDAO iterationDAO) {
		for(Iteration i: iterationDAO.getAll()) {
			iterationDAO.remove(i.getId());
		}	
	}
}
