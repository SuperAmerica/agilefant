package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemStatus;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.TaskStatus;
//import fi.hut.soberit.agilefant.model.Task;
//import fi.hut.soberit.agilefant.model.TaskStatus;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;

/**
 * JUnit integration testing class for testing class BacklogItemAction.
 * 
 * Heavily under construction. Do not copy =)
 * 
 * @author tvainiok
 */
public class BacklogItemActionTest extends SpringTestCase {
	private static final String TEST_NAME1 = "jUnit test -backlog item 1";
	private static final String TEST_NAME2 = "jUnit test -backlog item 2";
	private static final String TEST_DESC1 = "Backlog item, missä tehdään vaikka mitä 1";
	private static final String TEST_DESC2 = "Backlog item, missä tehdään vaikka mitä 2";
	private static final AFTime TEST_EST1 = new AFTime("4h");
	private static final AFTime TEST_EST2 = new AFTime("5h");
	private static final Priority TEST_PRI1 = Priority.CRITICAL;
	private static final Priority TEST_PRI2 = Priority.TRIVIAL;
	private static final TaskStatus TEST_STAT1 = TaskStatus.NOT_STARTED;
	private static final TaskStatus TEST_STAT2 = TaskStatus.STARTED;
	private static final boolean TEST_WATCH1 = false;
	private static final boolean TEST_WATCH2 = true;
	private static final int INVALID_BACKLOGITEMID = -1;
	
	private User user1;
	private User user2;
	
	
	// TODO watch!
	// TODO assignee nullilla.
	// TODO taskien lisäys, jonka jälkeen effort estimate sums (from tasks) et cetera..
	
	// The field and setter to be used by Spring
	private BacklogItemAction backlogItemAction;
//	private TaskAction taskAction;
	private UserAction userAction;
	private ProductAction productAction;
	private TaskDAO taskDAO;
	private ProductDAO productDAO;
	private BacklogItemDAO backlogItemDAO;
	private UserDAO userDAO;
	
/*	public void setTaskAction(TaskAction taskAction) {
		this.taskAction = taskAction;
	}*/

	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}
	
	public void setProductAction(ProductAction productAction) {
		this.productAction = productAction;
	}
	
	public void setBacklogItemAction(BacklogItemAction backlogItemAction) {
		this.backlogItemAction = backlogItemAction;
	}
	
	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}
	
	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}
	
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
/*	public void setSecurityUtil(SecurityUtil securityUtil) {
		this.securityUtil = securityUtil;
	}*/

	/**
	 * Checks, if there are any given error countered. 
	 */
	private boolean errorFound(String e) {
		Collection<String> errors = backlogItemAction.getActionErrors();
		boolean found = false;
		System.out.println("checking for errors.");
		for(String s: errors) {
			System.out.println("error " + s);
			if(s.equals(e))
				found = true;
		}
		return found;
	}
	
	private void setNameAndDesc(String name, String description) {
		BacklogItem bi = backlogItemAction.getBacklogItem();
		bi.setName(name);
		bi.setDescription(description);
	}
	
	private void setLoggedUser(User user) {
		SecurityUtil.setLoggedUser(user);
	}
	
	private void setAssignee(User assignee) {
		if(assignee == null)
			backlogItemAction.setAssigneeId(0);
		else
			backlogItemAction.setAssigneeId(assignee.getId());
	}

	private void setAllocatedEffort(AFTime allocatedEffort) {
		backlogItemAction.getBacklogItem().setAllocatedEffort(allocatedEffort);
	}
	
	private void setPriority(Priority priority) {
		backlogItemAction.getBacklogItem().setPriority(priority);
	}
	
	private void setStatus(TaskStatus status) {
		
		backlogItemAction.getBacklogItem().setStatus(status);
	}
	
	private void setBacklog(Backlog backlog) {
		backlogItemAction.getBacklogItem().setBacklog(backlog);
	}
	
	private void setContents(String name, String desc, User creator, boolean watch, 
			User assignee, AFTime allocatedEffort, Priority priority, 
			TaskStatus status, Backlog backlog) {
		this.setNameAndDesc(name, desc);
		this.setLoggedUser(creator);
		this.backlogItemAction.setWatch(watch);
		this.setAssignee(assignee);
		this.setAllocatedEffort(allocatedEffort);
		this.setPriority(priority);
		this.setStatus(status);
		this.setBacklog(backlog);
	}
	
	private void checkContents(String entity, BacklogItem bi, String name, String desc, User creator, 
			boolean watch, User assignee, AFTime estimate, Priority priority, 
			TaskStatus status, Backlog backlog) {
		super.assertEquals("The name of the " + entity + " was wrong", name, bi.getName());
		super.assertEquals("The description of the " + entity + " was wrong", desc, bi.getDescription());
		if(watch) {
			super.assertFalse("Number of watchers set was 0 even though watcher was set", 
						bi.getWatchers().size() == 0);
			super.assertTrue("Watching wasn't set as supposed", bi.getWatchers().containsValue(creator));
			userDAO.refresh(creator);
			super.assertFalse("Number of backlog items watched by user was 0 even though shouldn't be", 
					creator.getWatchedBacklogItems().size() == 0);
		}
		else 
			super.assertFalse("Watching was set unlike as supposed", bi.getWatchers().containsValue(creator));
		// TODO watcher
//		super.assertEquals("The creator of the " + entity + " was wrong", creator, task.getCreator());
		super.assertEquals("The assignee of the " + entity + " was wrong", assignee.getId(), 
					bi.getAssignee().getId());
		super.assertEquals("The allocated effort of the " + entity + " was wrong", estimate, 
				bi.getAllocatedEffort());
		super.assertEquals("The priority of the " + entity + " was wrong", priority, bi.getPriority());
		super.assertEquals("The status of the " + entity + " was wrong", status, bi.getPlaceHolder().getStatus());
		super.assertEquals("The backlog of the " + entity + " was wrong", backlog, bi.getBacklog());

	}
			
	private User getTestUser(int n) {
		if(n == 1) {
			if(this.user1 == null)
				user1 = UserActionTest.GenerateAndStoreTestUser(this.userAction, this.userDAO, 1);
			assertNotNull(user1);
			return user1;
		}
		else {
			if(this.user2 == null)
				user2 = UserActionTest.GenerateAndStoreTestUser(this.userAction, this.userDAO, 2);
			assertNotNull(user2);
			return user2;
		} 
	}
	
	/**
	 * Returns a product backlog for testing.
	 * 
	 * @param number Not in use
	 * @return Product backlog for testing
	 */
	private Backlog getTestBacklog(int number) {
		this.productAction.create();
		Product p = this.productAction.getProduct();
		p.setAssignee(this.getTestUser(1));
		p.setDescription("FOOBAR");
		p.setName("Testituote 1");
		String result = this.productAction.store();
		assertSame("Product creation failed", Action.SUCCESS, result);
		Collection<Product> cp = this.productDAO.getAll();
		for(Product pp: cp) {
			return pp;
		}
		fail("Product not created as supposed");
		return null;
	}
	
/*	private BacklogItem getTestBacklogItem(Backlog b) {
		this.backlogItemAction.setBacklogId(b.getId());
		this.backlogItemAction.create();
		BacklogItem bi = this.backlogItemAction.getBacklogItem();
		bi.setAssignee(this.getTestUser(1));
		bi.setBacklog(b);
		bi.setDescription("FOOBAR");
		bi.setName("FOOBAR");
		bi.setPriority(TEST_PRI1);
		bi.setAllocatedEffort(TEST_EST1);
		bi.setPriority(TEST_PRI1);
		String result = this.backlogItemAction.store();
		assertSame("Backlog item creation failed", Action.SUCCESS, result);
		Collection<BacklogItem> cb = this.backlogItemDAO.getAll();
		for(BacklogItem bb : cb) {
			return bb;
		}
		fail("Backlog item not created as supposed");
		return null;	
	}*/
	
	private Collection<BacklogItem> getAllBacklogItems() {
		return this.backlogItemDAO.getAll();
	}
	
	/**
	 * Method for calling taskAction.create that is supposed to work (and 
	 * is not a target for testing) Actual testing for method create
	 * is done in testCreate_XXX -methods
	 */
	private void create(int backlogId) {
		backlogItemAction.setBacklogId(backlogId);
		String result = backlogItemAction.create();
		assertEquals("create() was unsuccessful", Action.SUCCESS, result);
	}

	/**
	 * Method for calling taskAction.store that is supposed to work (and 
	 * is not a target for testing) Actual testing for method store
	 * is done in testStore_XXX -methods
	 */
	private void store() {
		String result = backlogItemAction.store();
		assertEquals("store() was unsuccessful", Action.SUCCESS, result);
	}

	private void edit() {
		String result = backlogItemAction.edit();
		assertEquals("edit() was unsuccessful", Action.SUCCESS, result);
	}
	
	private void fetchForEditing(String name, String desc) {
		backlogItemAction.setBacklogItem(null);
		backlogItemAction.setBacklogItemId(this.getBacklogItem(name, desc).getId());
		String result = backlogItemAction.edit();
		super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
		super.assertNotNull("Edit() was unsuccesfull (fetched backlog item was null)", 
				this.backlogItemAction.getBacklogItem());
	}
	
	/**
	 * Get task based on name and description.
	 */
	private BacklogItem getBacklogItem(String name, String desc) {
		for(BacklogItem bi: getAllBacklogItems()) {
			if(bi.getName().equals(name) && 
					bi.getDescription().equals(desc)) 
			return bi;	
		}
		return null;
	}
	
	/*** Actual test methods **/
	
	public void testCreate(){
		Backlog b = this.getTestBacklog(1);
		super.assertNotNull("getTestBacklog doesn't work", b);
		backlogItemAction.setBacklogId(b.getId());
		String result = backlogItemAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("New user had an invalid id", 0, 
				backlogItemAction.getBacklogItem().getId());
		super.assertEquals("New user had an invalid id", 0, 
				backlogItemAction.getBacklogItemId());
		super.assertNotNull("Created backlogitemaction had null BacklogItem", 
				backlogItemAction.getBacklogItem());
	}
	
	public void testStore() {
		BacklogItem storedBI;
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		int n = this.getAllBacklogItems().size();
		String result = backlogItemAction.store();
		
		
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("The total number of stored backlog items didn't " +
				"grow up with store().", n+1, getAllBacklogItems().size());
		storedBI = this.getBacklogItem(TEST_NAME1, TEST_DESC1);
		super.assertNotNull("Stored backlog item wasn't found", storedBI);
		this.checkContents("stored backlog item", storedBI, 
				TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		assertNotNull("Placeholder task was not created", 
				storedBI.getPlaceHolder());
	}
	
	/**
	 * Test storing backlog item when original estimate was null and
	 * new estimate has value. Placeholder task must get the new value.
	 */
	public void testStoreWithOrigNullEstimate() {
		BacklogItem storedBI;
		BacklogItem updatedBI = new BacklogItem();
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		
		
		this.setContents(TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				null, TEST_PRI1, TEST_STAT1, backlog);
		String result = backlogItemAction.store();
		storedBI = this.getBacklogItem(TEST_NAME1, TEST_DESC1);
		assertNull("Placeholder task doesn't have null estimate", 
				storedBI.getPlaceHolder().getEffortEstimate());
		
		updatedBI.setName(TEST_NAME1);
		updatedBI.setDescription(TEST_DESC1);
		
		backlogItemAction.setBacklogItemId(storedBI.getId());
		updatedBI.setAllocatedEffort(TEST_EST1);
		
		backlogItemAction.setBacklogItem(updatedBI);
		assertTrue("Storing backlog item failed", 
				Action.ERROR != backlogItemAction.store());
		
		storedBI = this.getBacklogItem(TEST_NAME1, TEST_DESC1);
		
		assertEquals("Backlog item effort estimate wasn't updated",
				TEST_EST1, storedBI.getAllocatedEffort());
		
		assertEquals("Placeholder task's estimate wasn't updated", 
				TEST_EST1, storedBI.getPlaceHolder().getEffortEstimate());
	}
	
	public void testStore_withDifferentData() {
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME2, TEST_DESC2, creator, TEST_WATCH2, assignee, 
				TEST_EST2, TEST_PRI2, TEST_STAT2, backlog);
		this.store();
		BacklogItem storedBI = this.getBacklogItem(TEST_NAME2, TEST_DESC2);
		super.assertNotNull("Stored backlog item wasn't found", storedBI);
		this.checkContents("stored backlog item", storedBI, TEST_NAME2, TEST_DESC2, creator, TEST_WATCH2,
				assignee, TEST_EST2, TEST_PRI2, TEST_STAT2, backlog);
	}
	
	public void testStore_withEmptyName() {
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents("", TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		String result = backlogItemAction.store();
		super.assertEquals("storing backlog item with name missing was successful", Action.ERROR, result);
		assertTrue("backlogitem.missingName -error not found", errorFound(backlogItemAction.getText("backlogitem.missingName")));
	}
	
/*	public void testStoreAndEdit_withNullEstimates() {
		this.create();
		BacklogItem bi = this.getTestBacklogItem(this.getTestBacklog());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME1, TEST_DESC1, creator, assignee, 
				null, TEST_PRI1, TEST_STAT1, bi);
		String result = taskAction.store();
		super.assertEquals("storing task with null estimate was unsuccesfull", Action.SUCCESS, result);
		this.fetchForEditing(TEST_NAME1, TEST_DESC1);
		super.assertNull("Null estimate wasn't null after fetching for editing", 
				taskAction.getTask().getEffortEstimate());
		this.setEstimate(TEST_EST1);
		this.store();
		this.fetchForEditing(TEST_NAME1, TEST_DESC1);
		super.assertEquals("Changed estimate wasn't as supposed", TEST_EST1, 
				taskAction.getTask().getEffortEstimate());
		this.setEstimate(null);		
		this.store();
		this.fetchForEditing(TEST_NAME1, TEST_DESC1);
		super.assertNull("Setting estimate to null (for a task create before) was unsuccesfull", 
				taskAction.getTask().getEffortEstimate());
	}*/

	
	public void testStore_withoutCreate() {
		try {
			String result = backlogItemAction.store();
			assertEquals("Store without create didn't result an error.", Action.ERROR, result);
		} catch (NullPointerException e) {	
		}		
	}
	
	public void testEdit() {
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		this.store();
		backlogItemAction.setBacklogItem(null);
		backlogItemAction.setBacklogItemId(this.getBacklogItem(TEST_NAME1, TEST_DESC1).getId());
		String result = backlogItemAction.edit();
		super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
		BacklogItem fetchedBI = backlogItemAction.getBacklogItem();
		this.checkContents("backlog item fetched for editing", fetchedBI, TEST_NAME1, TEST_DESC1, creator, 
				TEST_WATCH1, assignee, TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
	}
	
	public void testEdit_withInvalidId() {
		backlogItemAction.setBacklogId(INVALID_BACKLOGITEMID);
		String result = backlogItemAction.edit();
		assertEquals("Invalid backlog item id didn't result an error.", Action.ERROR, result);
		// TODO check the error?
	}
	
	/**
	 * Change the details of previously stored task and update the task.
	 */
	public void testStore_withUpdate() {
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		this.store();
		
		this.fetchForEditing(TEST_NAME1, TEST_DESC1);

		this.setNameAndDesc(TEST_NAME2, TEST_DESC2);
		this.setAssignee(creator);
		this.setPriority(TEST_PRI2);
		this.setAllocatedEffort(TEST_EST2);
		this.setStatus(TEST_STAT2);
		
		String result = backlogItemAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		BacklogItem updatedBacklogItem = this.getBacklogItem(TEST_NAME2, TEST_DESC2);
		this.checkContents("updated task", updatedBacklogItem, TEST_NAME2, TEST_DESC2, creator, TEST_WATCH1,
				creator, TEST_EST2, TEST_PRI2, TEST_STAT2, backlog);
	}

	public void testDelete() {
		Backlog backlog = this.getTestBacklog(1);
		this.create(backlog.getId());
		User assignee = this.getTestUser(1);
		User creator = this.getTestUser(2);
		this.setContents(TEST_NAME1, TEST_DESC1, creator, TEST_WATCH1, assignee, 
				TEST_EST1, TEST_PRI1, TEST_STAT1, backlog);
		this.store();

		int n = getAllBacklogItems().size();
		backlogItemAction.setBacklogItemId(this.getBacklogItem(TEST_NAME1, TEST_DESC1).getId());
		String result = backlogItemAction.delete();
		super.assertEquals("delete() was unsuccessful", Action.SUCCESS, result);
		super.assertEquals("The number of backlog items didn't decrease with delete().", n-1, 
				getAllBacklogItems().size());
		
		BacklogItem test = this.getBacklogItem(TEST_NAME1, TEST_DESC1);
		super.assertNull("The deleted backlog item wasn't properly deleted", test);
	}
	
	public void testDelete_withInvalidId() {
		backlogItemAction.setBacklogId(INVALID_BACKLOGITEMID);
		String result = backlogItemAction.delete();
		assertEquals("Invalid backlog item id didn't result an error.", Action.ERROR, result);
	}
}
