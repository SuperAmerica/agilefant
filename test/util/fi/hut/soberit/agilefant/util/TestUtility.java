package fi.hut.soberit.agilefant.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.model.TaskStatus;
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
public class TestUtility extends SpringTestCase {
	private static Log logger = LogFactory.getLog(TestUtility.class);
	private UserDAO userDAO;
	private ProductDAO productDAO;
	private BacklogItemDAO backlogItemDAO;
	private TaskDAO taskDAO;
	private TaskEventDAO taskEventDAO;
	private DeliverableDAO deliverableDAO;
	private IterationDAO iterationDAO;
	private boolean cleanup = false;
	
	public static final long MINUTE = 1000 * 60;
	public static final long HOUR = MINUTE * 60;
	
	public static enum TestUser {
		USER1, USER2
	}
	
	private Stack<GenericDAO> cleanupStack = new Stack<GenericDAO>();
	
	/**
	 * Create a new user for testing.
	 * 
	 * @param loginName The login name for the user
	 * @param passwd The password of the user
	 * @return The id of the generated user
	 */
	public Integer createUser(String loginName, String passwd) {
		Integer id;
		User user = new User();
		user.setLoginName(loginName);
		user.setPassword(SecurityUtil.MD5(passwd));
		id = (Integer)userDAO.create(user);
		pushToCleanupstack(userDAO);
		return id;
	}
	
	/**
	 * Create a new product for testing.
	 * 
	 * @param productName The name for the test product
	 * @return The id of the generated product
	 */
	public Integer createProduct(String name) {
		Integer id;
		Product product = new Product();
		product.setName(name);
		id = (Integer)productDAO.create(product);
		pushToCleanupstack(productDAO);
		return id;
	}
	
	/**
	 * Create a new project for testing.
	 * 
	 * @param name The name for the test project
	 * @param startDate The start date of the test project
	 * @param endDate The end date of the test project
	 * @param product The product of the test project
	 * @return The id of the generated project
	 */
	public Integer createProject(String name, Date startDate,
			Date endDate, Product product) {
		Integer id;
		Deliverable project = new Deliverable();
		project.setName(name);
		project.setProduct(product);
		project.setStartDate(startDate);
		project.setEndDate(endDate);
		id = (Integer)deliverableDAO.create(project);
		pushToCleanupstack(deliverableDAO);
		return id;
	}
	
	/**
	 * Create a new project for testing. Start date is epoch, end date
	 * is now * 2. Generic product is also created.
	 * @param name The name for the test project
	 * @return The id of the generated project
	 */
	public Integer createProject(String name) {
		Product product = productDAO.get(createProduct(name));
		return createProject(name, new Date(0), 
				new Date(System.currentTimeMillis() * 2), product);
	}
	
	/**
	 * Crate a new iteration for testing.
	 * 
	 * @param name the name for the test iteration
	 * @param startDate the start date for the iteration
	 * @param endDate the end date for the iteration
	 * @param project the project the iteration belongs to
	 * @return the id of the generated iteration
	 */
	public Integer createIteration(String name, Date startDate,
			Date endDate, Deliverable project) {
		Integer id;
		Iteration iteration = new Iteration();
		iteration.setName(name);
		iteration.setDeliverable(project);
		iteration.setStartDate(startDate);
		iteration.setEndDate(endDate);
		id = (Integer)iterationDAO.create(iteration);
		pushToCleanupstack(iterationDAO);
		return id;
	}
	
	/**
	 * Create a new iteration for testing. Start date is epoch, end date
	 * is 2 * now. Generic project is also created.
	 * 
	 * @param name the name for the test iteration
	 * @return the id of the generated iteration
	 */
	public Integer createIteration(String name) {
		Deliverable project = deliverableDAO.get(createProject(name));
		return createIteration(name, new Date(0),
				new Date(System.currentTimeMillis() * 2), project);
	}
	
	/**
	 * Create a backlog item. Doesn't create placeholder task.
	 * 
	 * @param name the name for the backlog item
	 * @param backlog the backlog for the backlog item
	 * @return the id of the generated backlog item
	 */
	public Integer createBacklogItem(String name, Backlog backlog) {
		Integer id;
		BacklogItem backlogItem = new BacklogItem();
		backlogItem.setName(name);
		backlogItem.setBacklog(backlog);
		id = (Integer)backlogItemDAO.create(backlogItem);
		pushToCleanupstack(backlogItemDAO);
		return id;
	}
	
	/**
	 * Create a backlog item. Creates also a generic backlog for the item.
	 * 
	 * @param name the name for the backlog item
	 * @param backlog the backlog for the backlog item
	 * @return the id of the generated backlog item
	 */
	public Integer createBacklogItem(String name) {
		Product product = productDAO.get(createProduct(name));
		return createBacklogItem(name, product);
	}
	
	/**
	 * Create a task. Doens't create task event history items.
	 * 
	 * @param name the name for the task
	 * @param backlogItem the backlog item the task belongs to
	 * @return the id of the generated task
	 */
	public Integer createTask(User creator, 
			String name, BacklogItem backlogItem) {
		Integer id;
		Task task = new Task();
		task.setName(name);
		task.setBacklogItem(backlogItem);
		task.setCreator(creator);
		id = (Integer)taskDAO.create(task);
		pushToCleanupstack(taskDAO);
		return id;
	}
	
	/**
	 * Create a task. Create a generic backlog item and backlog for the task.
	 * 
	 * @param name the name for the task
	 * @return the id of the generated task
	 */
	public Integer createTask(User creator, String name) {
		BacklogItem backlogItem = 
			backlogItemDAO.get(createBacklogItem(name));
		return createTask(creator, name, backlogItem);
	}
	
	/**
	 * Create a testimate history event.
	 * 
	 * @param actor The actor in the event
	 * @param task The task the event belongs to
	 * @param created The creation date of the event
	 * @param newEstimate The new estimate of the event
	 * @return the id of the generated event
	 */
	public Integer createEstimateHistory(
			User actor, Task task, Date created, AFTime newEstimate) {
		Integer id;
		EstimateHistoryEvent event = new EstimateHistoryEvent(
				actor, task, created, newEstimate);
		id = (Integer)taskEventDAO.create(event);
		pushToCleanupstack(taskEventDAO);
		return id;
	}
	
	/**
	 * Clean the DB from all content of DAOs in cleanupSet.
	 */
	public void clearDBStack() {
		if(cleanup) {
			while(!cleanupStack.empty()) {
				clearDB(cleanupStack.pop());
			}
		}
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
		if(logger.isDebugEnabled()) {
			logger.debug("Task " + taskId + " Event " + eventId + 
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
	
	public static void clearData(TaskDAO taskDAO) {
		for(Task i: taskDAO.getAll()) {
			taskDAO.remove(i.getId());
		}
	}
	
	public static void clearDB(GenericDAO genericDAO) {
		for(Object i: genericDAO.getAll()) {
			genericDAO.remove(i);
		}
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * @return the cleanup
	 */
	public boolean isCleanup() {
		return cleanup;
	}

	/**
	 * @param cleanup the cleanup to set
	 */
	public void setCleanup(boolean cleanup) {
		this.cleanup = cleanup;
	}

	/**
	 * @return the productDAO
	 */
	public ProductDAO getProductDAO() {
		return productDAO;
	}

	/**
	 * @param productDAO the productDAO to set
	 */
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	/**
	 * @return the backlogItemDAO
	 */
	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	/**
	 * @param backlogItemDAO the backlogItemDAO to set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	/**
	 * @return the taskDAO
	 */
	public TaskDAO getTaskDAO() {
		return taskDAO;
	}

	/**
	 * @param taskDAO the taskDAO to set
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
	 * @param taskEventDAO the taskEventDAO to set
	 */
	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

	/**
	 * @return the deliverableDAO
	 */
	public DeliverableDAO getDeliverableDAO() {
		return deliverableDAO;
	}

	/**
	 * @param deliverableDAO the deliverableDAO to set
	 */
	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}

	/**
	 * @return the iterationDAO
	 */
	public IterationDAO getIterationDAO() {
		return iterationDAO;
	}

	/**
	 * @param iterationDAO the iterationDAO to set
	 */
	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}
	
	/**
	 * Push the DAO to cleanup stack if cleanup is enabled and it is not
	 * yet in the stack.
	 * @param genericDAO the DAO to push to the stack
	 */
	private void pushToCleanupstack(GenericDAO genericDAO) {
		if(cleanup && !cleanupStack.contains(genericDAO)) {
			cleanupStack.push(genericDAO);
		}
	}
}
