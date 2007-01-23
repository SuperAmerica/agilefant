package fi.hut.soberit.agilefant.util;

import org.springframework.test.AbstractTransactionalSpringContextTests;

public abstract class SpringTestCase extends AbstractTransactionalSpringContextTests {
	@Override
	protected String[] getConfigLocations() {
		return new String[]{"file:conf/applicationContext.xml", "file:conf/applicationContext-*.xml"};
		//return new String[]{"file:conf/applicationContext*.xml"}; // ylempi ainakin tuntuu toimivan tutki, josko jotain erroria antin kautta? 
	}
	
	protected void endOldAndStartNewTransaction() {
		super.endTransaction();
		super.startNewTransaction();
	}
	
}
