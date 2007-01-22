package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.security.SecurityUtil;

import java.util.Collection;

public class UserActionTest extends SpringTestCase {
	private static final String TEST_NAME = "Timo Testuser";
	private static final String TEST_NAME2 = "Timo Testuser2";
	private static final String TEST_LOGINNAME = "ttestuse";
	
	private UserAction userAction;

	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}

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
	 * Get user based on loginname
	 *
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
		super.assertEquals("New user had an invalid id", 0, userAction.getUser().getId());
		
		this.endTransaction();
	}
	
	public void testStore() {
		String pass = "foobar";

		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(pass, pass);
		int n = getAllUsers().size();
		String result = userAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		super.assertEquals("The total number of stored users didn't grow up with store().", 
				n+1, getAllUsers().size());
		User storedUser = this.getUser(TEST_LOGINNAME);
		super.assertNotNull("User wasn't stored properly (wasn't found)", storedUser);
		super.assertTrue("Stored user had invalid name", storedUser.getFullName().equals(TEST_NAME)); 
		super.assertEquals("Stored user had invalid hashed password.",
				SecurityUtil.MD5(pass), storedUser.getPassword());
		this.endTransaction();
	}
	
	/*
	 * Change the name of previously stored user and update the user.
	 */
	public void testStore_withUpdate() {
		String pass = "foobar";
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords(pass, pass);
		User storedUser = this.getUser(TEST_LOGINNAME);
		String result = userAction.store();
		super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		storedUser.setFullName(TEST_NAME2);
		userAction.setUserId(storedUser.getId());
		userAction.setUser(storedUser);
		
		
		
	}

	public void testStore_withDuplicateLogins() {
		this.create(); // 1st user
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords("foobar", "foobar");
		this.store();

		this.create(); // 2nd user with same login name
		this.setNames(TEST_NAME2, TEST_LOGINNAME);
		String result = userAction.store();
		assertNotSame("User with duplicate login name was accepted.", Action.SUCCESS, result);	
		this.endTransaction();
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
		this.setPasswords("foobar", "bar");
		String result = userAction.store();
		assertEquals("Different passwords accepted", Action.ERROR, result);
		assertTrue("user.missingPassword -error not found", 
				errorFound(userAction.getText("user.passwordsNotEqual")));	
	}
	
	public void testDelete() {
		this.create();
		this.setNames(TEST_NAME, TEST_LOGINNAME);
		this.setPasswords("foobar", "foobar");
		String result = userAction.store();
		assertEquals("store() was unsuccessful", result, Action.SUCCESS);
		
		int n = getAllUsers().size();
		User u = getUser(TEST_LOGINNAME);
		userAction.setUserId(u.getId());
		userAction.delete();
		super.assertEquals("The number of users didn't decrease with delete().", n-1, getAllUsers().size());
		
		User testU = getUser(TEST_LOGINNAME);
		super.assertNull("The deleted user wasn't properly deleted", testU);
		this.endTransaction();		
	}
	
	public void testDelete_withInvalidId() {
		userAction.setUserId(-1);
		try {
			userAction.delete();
			fail("delete() with id -1 was accepted.");
		}
		catch(IllegalArgumentException iae) {
		}
	}
}
