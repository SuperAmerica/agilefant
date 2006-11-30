package fi.hut.soberit.agilefant.web;

import org.springframework.test.AbstractTransactionalSpringContextTests;
import com.opensymphony.xwork.Action;
import fi.hut.soberit.agilefant.model.User;

public class UserActionTest extends AbstractTransactionalSpringContextTests {
	
	private UserAction userAction;

	@Override
	protected String[] getConfigLocations() {
		// with Eclipse: (doesn't work yet)
//		return new String[]{"file:conf/applicationContext*.xml"}; // for testing with ant
		// works with ant:
		return new String[]{"file:WEB-INF/applicationContext*.xml"}; // for testing with ant
	}
	
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
		this.setComplete();
		this.endTransaction();
//		assertEquals(Action.SUCCESS, userAction.store());
	}
}
