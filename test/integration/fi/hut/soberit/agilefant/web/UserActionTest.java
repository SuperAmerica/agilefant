package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.security.SecurityUtil;

import java.util.Collection;

/**
 * JUnit integration testing class for testing class UserAction
 * 
 * @author tvainiok
 */
public class UserActionTest extends SpringTestCase {
	private static final String TEST_NAME = "Timo Testuser";
	private static final String TEST_NAME2 = "Timo Testuser2";
	private static final String TEST_LOGINNAME = "ttestuse";
	private static final String TEST_PASS1 = "foobar";
	private static final String TEST_PASS2 = "asdf56";
	private static final int INVALID_USERID = -1;
	
	// The field and setter to be used by Spring
	private UserAction userAction; 

	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}

	/*
	 * Checks, if there are any given error countered. 
	 */
	private boolean errorFound(String e) {
		Collection<String> errors = userAction.getActionErrors();
		boolean found = false;
		for(String s: errors) {
			if(s.equals(e))
				found = true;
		}
		return found;
	}
	
	private User setNames(String fullName, String loginName) {
		User u = userAction.getUser();
		u.setFullName(fullName);
		u.setLoginName(loginName);
		return u;
	}
	
	private void setPasswords(String password1, String password2) {
		this.userAction.setPassword1(password1);
		this.userAction.setPassword2(password2);
	}

	/*
	 * Method for calling userAction.create that is supposed to work (and 
	 * is not a target for testing) Actual testing for method create
	 * is done in testCreate_XXX -methods
	 */
	private void create() {
		String result = userAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
	}

	/*
	 * Method for calling userAction.store that is supposed to work (and 
	 * is not a target for testing) Actual testing for method store
	 * is done in testStore_XXX -methods
	 */
	private void store() {
		String result = userAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
	}

	/*
	 * Get all stored Users.
	 * @return all users stored
	 */
	private Collection<User> getAllUsers() {
		return this.userAction.getUserDAO().getAll();
	}
	
	/*
	 * Get user based on loginname.
	 */
	private User getUser(String loginName) {
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
	}

	/*** Actual test methods **/
	
	public void testCreate(){
		String result = userAction.create();
		assertEquals("create() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("New user had an invalid id", 0, userAction.getUserId());
	}
	
	public void testStore() {
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(TEST_PASS1, TEST_PASS1);
		int n = getAllUsers().size();
		String result = userAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("The total number of stored users didn't grow up with store().", 
				n+1, getAllUsers().size());
		super.assertNotSame("The Stored user should have a proper id number after store()", 
				0, userAction.getUser().getId());
		User storedUser = this.getUser(TEST_LOGINNAME);
		super.assertNotNull("User wasn't stored properly (wasn't found)", storedUser);
		super.assertTrue("User for editing had an invalid name", storedUser.getFullName().equals(TEST_NAME)); 
		super.assertEquals("User for editing had an invalid hashed password.",
				SecurityUtil.MD5(TEST_PASS1), storedUser.getPassword());
	}
	
	public void testStore_withoutCreate() {
		String result = userAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);		
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
	}
	
	/*
	 * Change the name of previously stored user and update the user.
	 */
	public void testStore_withUpdate() {
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
}
