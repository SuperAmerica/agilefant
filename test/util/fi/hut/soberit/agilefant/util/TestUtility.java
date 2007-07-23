package fi.hut.soberit.agilefant.util;

import java.util.GregorianCalendar;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.EffortHistoryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EffortHistory;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.UserAction;
import fi.hut.soberit.agilefant.web.UserActionTest;

/**
 * Utility class for testing
 * 
 */
public class TestUtility {
	public static enum TestUser {
		USER1, USER2
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
	public static void createTestIteration(int number, IterationDAO iterationDAO) {
		Iteration iteration = new Iteration();
		GregorianCalendar endDate = new GregorianCalendar();
		endDate.add(GregorianCalendar.MONTH, 1);
		
		iteration.setDescription("Iteration backlog for testing");
		iteration.setName("Iteration test backlog " + number);
		iteration.setStartDate(new GregorianCalendar().getTime());
		iteration.setEndDate(endDate.getTime());
		iterationDAO.store(iteration);
	}
	
	/**
	 * 
	 * @param number identifier for backlog item name
	 * @param backlog backlog used 
	 * @param backlogItemDAO data access object for backlogItem
	 */
	public static void createTestItem(int number, Backlog backlog, 
			BacklogItemDAO backlogItemDAO) {
		BacklogItem backlogItem = new BacklogItem();
		
		backlogItem.setBacklog(backlog);
		backlogItem.setDescription("Backlog item for testing");
		backlogItem.setName("Test backlog " + number);
		backlogItemDAO.store(backlogItem);
	}
	
	/**
	 * Creates a test backlog item
	 * @param number identifier for backlog item name
	 * @param backlog backlog used 
	 * @param backlogItemDAO data access object for backlogItem
	 */
	public static void createTestItem(Backlog backlog, 
			BacklogItemAction backlogItemAction) {
		backlogItemAction.create();
		backlogItemAction.setBacklog(backlog);
		backlogItemAction.setBacklogItemName("Test item");
		backlogItemAction.store();
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
}
