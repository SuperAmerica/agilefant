package fi.hut.soberit.agilefant.web;

import org.springframework.test.AbstractTransactionalSpringContextTests;

public class UserActionTest extends AbstractTransactionalSpringContextTests {
	
	private UserAction userAction;

	@Override
	protected String[] getConfigLocations() {
		return new String[]{"WEB-INF/applicationContext*.xml"};
	}
	
	public void setUserAction(UserAction userAction){
		this.userAction = userAction;
	}
	
	public void testCreate(){
		userAction.create();
		super.assertEquals(0, userAction.getUser().getId());
	}
}
