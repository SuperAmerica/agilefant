package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;

import fi.hut.soberit.agilefant.web.ProductAction;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.UserAction;

import fi.hut.soberit.agilefant.web.UserActionTest;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.User;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;

public class BacklogTest extends SpringTestCase {
	
	private ProductAction productAction;
	private BacklogAction backlogAction;
	private BacklogItemAction backlogItemAction;
	private UserDAO userDAO;
	private UserAction userAction;
	private ProductDAO productDAO;
	private BacklogItemDAO backlogItemDAO;
	
	/* Dependency injection setters */
	
	/**
	 * Setter for Spring IoC.
	 * Injected product action is used in product creation.
	 * @param productAction product action to be set
	 */
	public void setProductAction(ProductAction productAction) {
		this.productAction = productAction;
	}
	
	/**
	 * Setter for Spring IoC
	 * Injected backlog item action is used in backlog item creation.
	 * @param backlogItemAction backlog item to be set
	 */
	public void setBacklogItemAction(BacklogItemAction backlogItemAction) {
		this.backlogItemAction = backlogItemAction;
	}
	
	/**
	 * Setter for Spring IoC
	 * Injected user DAO is used in backlog item creation.
	 * @param userDAO user DAO to be set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	/**
	 * Setter for Spring IoC
	 * Injected product DAO is used to test that backlog item creation
	 * was succesfull.
	 * @param productDAO product DAO to be set
	 */
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}
	
	/**
	 * Setter for Spring IoC <br/>
	 * Injected backlog item DAO is used to test that backlog items are
	 * saved into the database.
	 * @param backlogItemDAO backlog item DAO to be set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
	
	/**
	 * Setter for Spring IoC
	 * Injected user action is used in backlog item creation.
	 * @param userAction user action to be set
	 */
	public void setUserAction(UserAction userAction) {
		this.userAction = userAction;
	}
	
	/**
	 * Setter for Spring IoC
	 * Injected backlog action is used in testing that moving backlog items
	 * works.
	 * @param backlogAction
	 */
	public void setBacklogAction(BacklogAction backlogAction) {
		this.backlogAction = backlogAction;
	}
	
	/* Utility methods */
	
	/**
	 * Returns a product for testing.
	 * 
	 * @param number number for identifying product from name
	 * @return Product backlog for testing
	 */
	private void createTestProduct(int number) {
		String result = this.productAction.create();
		assertSame("Product creation failed", Action.SUCCESS, result);
		
		Product product = this.productAction.getProduct();

		assertNotNull("Could not retrive product", product);
		
		product.setDescription("Product backlog for testing");
		product.setName("Product test backlog " + number);
		
		result = this.productAction.store();
		assertSame("Product storing failed", Action.SUCCESS, result);
	}
	
	private void createTestItem(int number, Backlog backlog) {
		backlogItemAction.setBacklog(backlog);
		backlogItemAction.setBacklogId(backlog.getId());
		String result = this.backlogItemAction.create();
		assertSame("Backlog item creation failed", Action.SUCCESS, result);
		
		BacklogItem backlogItem = this.backlogItemAction.getBacklogItem();

		assertNotNull("Could not retrive backlog item", backlogItem);	

		backlogItem.setDescription("Backlog item for testing");
		backlogItem.setName("Test backlog " + number);

		backlogItemAction.setBacklogId(backlog.getId());
		
		assertFalse("Product DB is empty", this.productDAO.getAll().isEmpty());		
		
		result = this.backlogItemAction.store();
		
		assertSame("Backlog item storing failed: " + 
				backlogItemAction.getActionErrors(), 
				Action.SUCCESS, result);
	}
	
	/**
	 * Create and store a user for backlog item creation.
	 *
	 */
	private void createAndStoreUser() {
		User user = 
			UserActionTest.GenerateAndStoreTestUser(userAction, userDAO, 1);
		SecurityUtil.setLoggedUser(user);
	}
	
	/**
	 * Clears the database from test data.
	 * We must clear database by hand because of we need transactions to
	 * complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
		super.setComplete();
		
		for(User i: this.userDAO.getAll()) {
			this.userDAO.remove(i.getId());
		}
		
		for(BacklogItem i: this.backlogItemDAO.getAll()) {
			this.backlogItemDAO.remove(i.getId());
		}
		
		for(Product i: this.productDAO.getAll()) {
			this.productDAO.remove(i.getId());
		}	
	}
	
	/* Test methods */
	
	/**
	 * Test moving group of backlog items between to backlogs.
	 */
	public void testMove() {
		Product testBacklog0;	
		Product testBacklog1;
		Product[] testBacklogs;	
		Collection<BacklogItem> backlogItems;
		BacklogItem[] backlogItemArray;
		String result;
		int[] backlogItemIds;
		
		super.setComplete(); //Do not rollback transaction
		
		/* Set up database */
		
		this.createAndStoreUser();
		this.createTestProduct(1);
		this.createTestProduct(2);

		testBacklogs = productDAO.getAll().toArray(new Product[0]);
		
		assertEquals("Number of created products is wrong", 
				testBacklogs.length, 2);
		
		this.createTestItem(1, testBacklogs[0]);
		this.createTestItem(2, testBacklogs[0]);
		
		super.endTransaction();
		
		/* Set up objects for testing */
		
		super.startNewTransaction();
		super.setComplete();
		
		backlogItems = backlogItemDAO.getAll();
		backlogItemArray = backlogItems.toArray(new BacklogItem[0]);
		
		testBacklog0 = productDAO.get(testBacklogs[0].getId());
		
		testBacklog1 = productDAO.get(testBacklogs[1].getId());
		
		assertNotNull("Retrivin backlog failed", testBacklog0);
		
		assertTrue("Adding items to backlog failed ",
				backlogItems.size() > 0);
		
		/* The actual test code */
		
		backlogItemIds = new int[backlogItems.size()];
		
		for(int i = 0; i < backlogItemArray.length; i++) {
			backlogItemIds[i] = backlogItemArray[i].getId();
		}
		assertEquals("Backlog item ID array generation failed",
				backlogItemIds.length, backlogItems.size());
		
		backlogAction.setBacklogId(testBacklogs[0].getId());
		backlogAction.setTargetBacklog(testBacklogs[1].getId());
		backlogAction.setSelected(backlogItemIds);
		
		result = backlogAction.moveSelectedItems();		
		assertFalse("Moving items failed " + 
				backlogAction.getActionErrors(), 
				result.equals(Action.ERROR));
		
		assertEquals("Target backlog has invalid number of items",
				2, testBacklog1.getBacklogItems().size());
		assertEquals("Original backlog has invalid number of items",
				0, testBacklog0.getBacklogItems().size());
		
		/* Test for moving empty set of backlog items */
		
		backlogItemIds = null;
		backlogAction.setSelected(backlogItemIds);
		result = backlogAction.moveSelectedItems();
		assertTrue("Moving empty set of backlog items doesn't give error" + 
				backlogAction.getActionErrors(), 
				result.equals(Action.ERROR));
		
		super.endTransaction();
		super.startNewTransaction();
	}
	
}
