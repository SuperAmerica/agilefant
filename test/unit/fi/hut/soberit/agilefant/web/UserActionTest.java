package fi.hut.soberit.agilefant.web;

//import org.springframework.test.AbstractTransactionalSpringContextTests;
import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.SpringTestCase;
import fi.hut.soberit.agilefant.model.User;

public class UserActionTest extends SpringTestCase {
	
	private UserAction userAction;
	
	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}
	
	public void testCreate(){
		userAction.create();
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
		// System.out.println("foobar");
		this.setComplete();
		this.endTransaction();
//		assertEquals(Action.SUCCESS, userAction.store());
	}
}
