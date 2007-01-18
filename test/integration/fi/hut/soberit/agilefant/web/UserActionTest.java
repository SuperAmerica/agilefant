package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.SpringTestCase;

import java.util.Collection;

public class UserActionTest extends SpringTestCase {
	
	private UserAction userAction;
	
	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}
	
	public void testCreate(){
		String result = userAction.create();
		assertEquals(result, Action.SUCCESS);
		super.assertEquals(0, userAction.getUser().getId());
		
		// enpä tiedä, onko nämä seuraavat fiksuja..
		
		User u = new User();
		u.setFullName("Unit TestiKäyttäjä");
		u.setId(userAction.getUser().getId());
		u.setPassword("foobar");
		userAction.setPassword1("foobar");
		userAction.setPassword2("foobar");
		userAction.setUser(u);
		userAction.store();
		//this.setComplete(); // data is left in the database
		this.endTransaction();
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
	
	private User createTestUser(String fullName, String loginName) {
		User u = new User();
		u.setFullName(fullName);
		u.setLoginName(loginName);
		return u;
	}
	
	
//	public void testStoreAnd
	public void testStore_withEmptyPassword() {
//		User u = createTestUser("Unit TestiKäyttäjä", "unit_tk");
//		userAction.setUser(u);
		userAction.setPassword1("");
		userAction.setPassword2("");
		String result = userAction.store();
		assertEquals("Empty password accepted", Action.ERROR, result);
		assertTrue("user.missingPassword -error not found", 
				errorFound(userAction.getText("user.missingPassword")));
	}
	
	public void testStore_withDifferentPasswords() {
//		User u = createTestUser("Unit TestiKäyttäjä", "unit_tk");
//		userAction.setUser(u);
		userAction.setPassword1("foobar");
		userAction.setPassword2("bar");
		String result = userAction.store();
		assertEquals("Different passwords accepted", Action.ERROR, result);
		assertTrue("user.missingPassword -error not found", 
				errorFound(userAction.getText("user.passwordsNotEqual")));	
	}
	
	
	/*public void testEdit_withInvalidId() {
		
	}*/
}
