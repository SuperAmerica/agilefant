package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskStatus;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.db.TaskDAO;

/**
 * JUnit integration testing class for testing class TaskAction.
 * 
 * Heavily under construction. Do not copy =)
 * 
 * @author tvainiok
 */
public class TaskActionTest extends SpringTestCase {
	private static final String TEST_NAME1 = "jUnit test -task 1";
	private static final String TEST_NAME2 = "jUnit test -task 1";
	private static final String TEST_DESC1 = "Task, missä tehdään vaikka mitä 1";
	private static final String TEST_DESC2 = "Task, missä tehdään vaikka mitä 2";
	private static final AFTime TEST_EST1 = new AFTime("4h");
	private static final AFTime TEST_EST2 = new AFTime("5h");
	private static final Priority TEST_PRI1 = Priority.CRITICAL;
	private static final Priority TEST_PRI2 = Priority.TRIVIAL;
	private static final TaskStatus TEST_STAT1 = TaskStatus.NOT_STARTED;
	private static final TaskStatus TEST_STAT2 = TaskStatus.STARTED;
/*	private static final String TEST_LOGINNAME = "ttestuse";
	private static final String TEST_PASS1 = "foobar";
	private static final String TEST_PASS2 = "asdf56";*/
	private static final int INVALID_TASKID = -1;
	
	private User user1;
	private User user2;
	
	// The field and setter to be used by Spring
	private TaskAction taskAction;
	private UserAction userAction;
	private ProductAction productAction;
	private BacklogItemAction backlogItemAction;
	//private TaskDAO taskDAO;
	
	public void setTaskAction(TaskAction taskAction) {
		this.taskAction = taskAction;
	}

	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}
	
	public void setProductAction(ProductAction productAction) {
		this.productAction = productAction;
	}
	
	public void setBacklogItemACtion(BacklogItemAction backlogItemAction) {
		this.backlogItemAction = backlogItemAction;
	}
	
/*	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	} doesn't work*/

	/*
	 * Checks, if there are any given error countered. 
	 */
	private boolean errorFound(String e) {
		Collection<String> errors = taskAction.getActionErrors();
		boolean found = false;
		for(String s: errors) {
			if(s.equals(e))
				found = true;
		}
		return found;
	}
	
	private void setNameAndDesc(String name, String desc) {
		Task t = taskAction.getTask();
		t.setName(name);
		t.setDescription(desc);
	}
	
	private void setUsers(User creator, User assignee) {
		Task t = taskAction.getTask();
		t.setCreator(creator);
		t.setAssignee(assignee);
	}

	private void setEstimate(AFTime est) {
		taskAction.getTask().setEffortEstimate(est);
	}
	
	private void setPriority(Priority priority) {
		taskAction.getTask().setPriority(priority);
	}
	
	private void setStatus(TaskStatus status) {
		taskAction.getTask().setStatus(status);
	}
	
	private void setBacklogItem(BacklogItem bi) {
		taskAction.setBacklogItemId(bi.getId());
		//taskAction.getTask().setBacklogItem(bi);
	}
	
	private User getTestUser(int n) {
		if(n == 1) {
			if(this.user1 == null)
				user1 = UserActionTest.GenerateAndStoreTestUser(this.userAction, 1);
			return user1;
		}
		else {
			if(this.user2 == null)
				user2 = UserActionTest.GenerateAndStoreTestUser(this.userAction, 2);
			return user2;
		} 
	}
	
	private Backlog getTestBacklog() {
		this.productAction.create();
		Product p = this.productAction.getProduct();
		p.setAssignee(this.getTestUser(1));
		p.setDescription("FOOBAR");
		p.setName("Testituote 1");
		String result = this.productAction.store();
		assertSame("Product creation failed", Action.SUCCESS, result);
		Collection<Product> cp = this.productAction.getProductDAO().getAll();
		for(Product pp: cp) {
			return pp;
		}
		fail("Product not created as supposed");
		return null;
	}
	
	private BacklogItem getTestBacklogItem(Backlog b) {
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
		Collection<BacklogItem> cb = this.backlogItemAction.getBacklogItemDAO().getAll();
		for(BacklogItem bb : cb) {
			return bb;
		}
		fail("Backlog item not created as supposed");
		return null;
		
	}
	
	private Collection<Task> getAllTasks() {
		return this.taskAction.getTaskDAO().getAll();
	}
	
/*	private void setPasswords(String password1, String password2) {
		this.userAction.setPassword1(password1);
		this.userAction.setPassword2(password2);
	}*/

	/*
	 * Method for calling taskAction.create that is supposed to work (and 
	 * is not a target for testing) Actual testing for method create
	 * is done in testCreate_XXX -methods
	 */
	private void create() {
		String result = taskAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
	}

	/*
	 * Method for calling taskAction.store that is supposed to work (and 
	 * is not a target for testing) Actual testing for method store
	 * is done in testStore_XXX -methods
	 */
	private void store() {
		String result = taskAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
	}

	/*
	 * Get all stored Users.
	 * @return all users stored
	 */
/*	private Collection<User> getAllUsers() {
		return this.userAction.getUserDAO().getAll();
	}*/
	
	/*
	 * Get user based on loginname.
	 */
/*	private User getUser(String loginName) {
		User result = null;
		for(User u: getAllUsers()) {
			if(u.getLoginName().equals(loginName)) {
				if(result == null)
					result = u;
				else
					fail("Multiple users with same login name : " + loginName);
			}
		}
		return result;
	}*/

	/*** Actual test methods **/
	
	public void testCreate(){
		String result = taskAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("New user had an invalid id", 0, taskAction.getTaskId());
	}
	
	public void testStore() {
		this.create();
		this.setNameAndDesc(TEST_NAME1, TEST_DESC1);
		this.setEstimate(TEST_EST1);
		this.setUsers(this.getTestUser(1), this.getTestUser(2));
		this.setPriority(TEST_PRI1);
		this.setStatus(TEST_STAT1);
		this.setBacklogItem(this.getTestBacklogItem(this.getTestBacklog()));
		//this.taskAction.getTask().setCreated(created) ?
		int n = this.getAllTasks().size();
		String result = taskAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("The total number of stored tasks didn't grow up with store().", 
				n+1, getAllTasks().size());
		
		/*this.setPasswords(TEST_PASS1, TEST_PASS1);
		User storedUser = this.getUser(TEST_LOGINNAME);
		super.assertNotNull("User wasn't stored properly (wasn't found)", storedUser);
		super.assertTrue("User for editing had an invalid name", storedUser.getFullName().equals(TEST_NAME)); 
		super.assertEquals("User for editing had an invalid hashed password.",
				SecurityUtil.MD5(TEST_PASS1), storedUser.getPassword());*/
	}
	
/*	public void testStore_withoutCreate() {
		try {
			String result = userAction.store();
			fail("Store without create didn't cause an exception.");
		} catch (NullPointerException e) {
			
		}		
	}
	
	public void testEdit() {
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(TEST_PASS1, TEST_PASS1);
		this.store();

		userAction.setUser(null);
		User temp = this.getUser(TEST_LOGINNAME);
		userAction.setUserId(temp.getId());
		String result = userAction.edit();
		super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
		User fetchedUser = userAction.getUser();
		super.assertNotNull("User fetched for editing was null", fetchedUser);
		super.assertTrue("Updated user had invalid name", fetchedUser.getFullName().equals(TEST_NAME)); 
		super.assertEquals("Updated user had invalid hashed password.",
				SecurityUtil.MD5(TEST_PASS1), fetchedUser.getPassword());
	}
	
	public void testEdit_withInvalidId() {
		userAction.setUserId(INVALID_USERID);
		String result = userAction.edit();
		assertEquals("Invalid user id didn't result an error.", Action.ERROR, result);
		assertTrue("user.notFound -error not found", 
				errorFound(userAction.getText("user.notFound")));
	}*/
	
	/*
	 * Change the name of previously stored user and update the user.
	 */
/*	public void testStore_withUpdate() {
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(TEST_PASS1, TEST_PASS1);
		this.store(); // 
		
		User storedUser = this.getUser(TEST_LOGINNAME);		
		storedUser.setFullName(TEST_NAME2);
		userAction.setUserId(storedUser.getId());
		userAction.setUser(storedUser);
		this.setPasswords(TEST_PASS2, TEST_PASS2);
		String result = userAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);

		User updatedUser = this.getUser(TEST_LOGINNAME);
		super.assertNotNull("User wasn't stored properly (wasn't found)", updatedUser);
		super.assertTrue("Updated user had invalid name", updatedUser.getFullName().equals(TEST_NAME2)); 
		super.assertEquals("Updated user had invalid hashed password.",
				SecurityUtil.MD5(TEST_PASS2), storedUser.getPassword());
	}

	public void testStore_withDuplicateLogins() {
		// 1st user
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(TEST_PASS1, TEST_PASS1);
		this.store();

		// create 2nd user with same login name
		this.create();
		this.setNames(TEST_NAME2, TEST_LOGINNAME);
		String result = userAction.store();
		assertNotSame("User with duplicate login name was accepted.", Action.SUCCESS, result);	
		assertTrue("user.loginNameInUse -error not found", 
				errorFound(userAction.getText("user.loginNameInUse")));
	}

	public void testStore_withEmptyPassword() {
		this.create();
		this.setPasswords("", "");
		String result = userAction.store();
		assertEquals("Empty password accepted", Action.ERROR, result);
		assertTrue("user.missingPassword -error not found", 
				errorFound(userAction.getText("user.missingPassword")));
	}
	
	public void testStore_withDifferentPasswords() {
		this.create();
		this.setPasswords(TEST_PASS1, TEST_PASS2);
		String result = userAction.store();
		assertEquals("Different passwords accepted", Action.ERROR, result);
		assertTrue("user.missingPassword -error not found", 
				errorFound(userAction.getText("user.passwordsNotEqual")));	
	}
	
	public void testDelete() {
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(TEST_PASS1, TEST_PASS1);
		String result = userAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		int n = getAllUsers().size();
		User u = getUser(TEST_LOGINNAME);
		userAction.setUserId(u.getId());
		userAction.delete();
		super.assertEquals("The number of users didn't decrease with delete().", n-1, getAllUsers().size());
		
		User testU = getUser(TEST_LOGINNAME);
		super.assertNull("The deleted user wasn't properly deleted", testU);
	}
	
	public void testDelete_withInvalidId() {
		userAction.setUserId(INVALID_USERID);
		try {
			userAction.delete();
			fail("delete() with invalid id " + INVALID_USERID + " was accepted.");
		}
		catch(IllegalArgumentException iae) {
		}
	}
*/
}
