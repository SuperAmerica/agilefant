package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

public class BacklogTest extends SpringTestCase {

	private BacklogAction backlogAction;

	private UserDAO userDAO;

	private UserAction userAction;

	private ProductDAO productDAO;

	private BacklogItemDAO backlogItemDAO;

	/* Dependency injection setters */

	/**
	 * Setter for Spring IoC Injected user DAO is used in backlog item creation.
	 * 
	 * @param userDAO
	 *            user DAO to be set
	 */
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * Setter for Spring IoC Injected product DAO is used to test that backlog
	 * item creation was succesfull.
	 * 
	 * @param productDAO
	 *            product DAO to be set
	 */
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	/**
	 * Setter for Spring IoC <br/> Injected backlog item DAO is used to test
	 * that backlog items are saved into the database.
	 * 
	 * @param backlogItemDAO
	 *            backlog item DAO to be set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	/**
	 * Setter for Spring IoC Injected user action is used in backlog item
	 * creation.
	 * 
	 * @param userAction
	 *            user action to be set
	 */
	public void setUserAction(UserAction userAction) {
		this.userAction = userAction;
	}

	/**
	 * Setter for Spring IoC Injected backlog action is used in testing that
	 * moving backlog items works.
	 * 
	 * @param backlogAction
	 */
	public void setBacklogAction(BacklogAction backlogAction) {
		this.backlogAction = backlogAction;
	}

	/* Teardown */

	/**
	 * Clears the database from test data. We must clear database manually
	 * because of we need transactions to complete.
	 */
	protected void onTearDownInTransaction() throws Exception {
		super.setComplete();
		TestUtility.clearData(userDAO, backlogItemDAO, productDAO);
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

		super.setComplete(); // Do not rollback transaction

		/* Set up database */

		TestUtility.initUser(userAction, userDAO);
		for (int i = 0; i < 2; i++) {
			TestUtility.createTestProduct(i, productDAO);
		}

		testBacklogs = productDAO.getAll().toArray(new Product[0]);
		assertTrue("Product creation failed", testBacklogs.length != 0);

		for (int i = 0; i < 2; i++) {
			TestUtility.createBareTestItem(i, testBacklogs[0], backlogItemDAO);
		}

		super.endTransaction();

		/* Set up objects for testing */

		super.startNewTransaction();
		super.setComplete();

		backlogItems = backlogItemDAO.getAll();
		backlogItemArray = backlogItems.toArray(new BacklogItem[0]);

		testBacklog0 = productDAO.get(testBacklogs[0].getId());

		testBacklog1 = productDAO.get(testBacklogs[1].getId());

		assertNotNull("Retrivin backlog failed", testBacklog0);

		assertTrue("Adding items to backlog failed ", backlogItems.size() > 0);

		/* The actual test code */

		backlogItemIds = new int[backlogItems.size()];

		for (int i = 0; i < backlogItemArray.length; i++) {
			backlogItemIds[i] = backlogItemArray[i].getId();
		}
		assertEquals("Backlog item ID array generation failed",
				backlogItemIds.length, backlogItems.size());

		backlogAction.setBacklogId(testBacklogs[0].getId());
		backlogAction.setTargetBacklog(testBacklogs[1].getId());
		backlogAction.setSelected(backlogItemIds);

		result = backlogAction.moveSelectedItems();
		assertFalse("Moving items failed " + backlogAction.getActionErrors(),
				result.equals(Action.ERROR));

		assertEquals("Target backlog has invalid number of items", 2,
				testBacklog1.getBacklogItems().size());
		assertEquals("Original backlog has invalid number of items", 0,
				testBacklog0.getBacklogItems().size());

		/* Test for moving empty set of backlog items */

		backlogItemIds = null;
		backlogAction.setSelected(backlogItemIds);
		result = backlogAction.moveSelectedItems();
		assertTrue("Moving empty set of backlog items doesn't give error"
				+ backlogAction.getActionErrors(), result.equals(Action.ERROR));

		super.endTransaction();
		super.startNewTransaction();
	}

	/**
	 * Test moving group of backlog items between to backlogs.
	 */
	public void testDelete() {
		Product testBacklog0;
		Product[] testBacklogs;
		Collection<BacklogItem> backlogItems;
		BacklogItem[] backlogItemArray;
		String result;
		int[] backlogItemIds;

		super.setComplete(); // Do not rollback transaction

		// Set up database

		TestUtility.initUser(userAction, userDAO);
		for (int i = 0; i < 2; i++) {
			TestUtility.createTestProduct(i, productDAO);
		}

		testBacklogs = productDAO.getAll().toArray(new Product[0]);
		assertTrue("Product creation failed", testBacklogs.length != 0);

		for (int i = 0; i < 2; i++) {
			TestUtility.createBareTestItem(i, testBacklogs[0], backlogItemDAO);
		}

		super.endTransaction();

		// Set up objects for testing

		super.startNewTransaction();
		super.setComplete();

		backlogItems = backlogItemDAO.getAll();
		backlogItemArray = backlogItems.toArray(new BacklogItem[0]);

		testBacklog0 = productDAO.get(testBacklogs[0].getId());

		assertNotNull("Retrivin backlog failed", testBacklog0);

		assertTrue("Adding items to backlog failed ", backlogItems.size() > 0);

		// The actual test code

		backlogItemIds = new int[backlogItems.size()];

		for (int i = 0; i < backlogItemArray.length; i++) {
			backlogItemIds[i] = backlogItemArray[i].getId();
		}
		assertEquals("Backlog item ID array generation failed",
				backlogItemIds.length, backlogItems.size());

		backlogAction.setBacklogId(testBacklogs[0].getId());
		backlogAction.setSelected(backlogItemIds);

		result = backlogAction.deleteSelectedItems();
		assertFalse("Deleting items failed " + backlogAction.getActionErrors(),
				result.equals(Action.ERROR));

		assertEquals("Backlog has invalid number of items", 0, testBacklog0
				.getBacklogItems().size());

		super.endTransaction();
		super.startNewTransaction();
	}
}
