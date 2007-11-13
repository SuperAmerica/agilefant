package fi.hut.soberit.agilefant.util;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.test.AbstractTransactionalSpringContextTests;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class SpringTestCase extends
		AbstractTransactionalSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		return new String[] { "file:conf/applicationContext.xml",
				"file:conf/applicationContext-*.xml",
				"file:conf/test/testUtilApplicationContext.xml" };
		// return new String[]{"file:conf/applicationContext*.xml"}; // ylempi
		// ainakin tuntuu toimivan tutki, josko jotain erroria antin kautta?
	}

	protected void endOldAndStartNewTransaction() {
		super.endTransaction();
		super.startNewTransaction();
	}

	/**
	 * Autowire-by-name given target so that one can autowire also another
	 * objects
	 * 
	 * @param target
	 *            to be wired
	 */
	public void enableSpring(Object target) {
		applicationContext.getAutowireCapableBeanFactory()
				.autowireBeanProperties(target,
						AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
	}

	public SpringTestCase() {
		// autowire by name, defaults to type
		setAutowireMode(AUTOWIRE_BY_NAME);

		// Since we don't use autowiring by type,
		// AbstractTransactionalSpringContextTests in unable to get the
		// transaction manager by itself.
		// (it expects a bean of name transactionManager, we have
		// hibernateTransactionManager)
		// We need to disable the dependy checking here, so that it wont go
		// crazy for the missing
		// transaction manager.
		setDependencyCheck(false);

		setDefaultRollback(true);
	}

	public void setHibernateTransactionManager(
			HibernateTransactionManager manager) {
		// We'll set the transaction manager here, since
		// AbstractTransactionalSpringContextTests wont
		// find it alone, since we use autowiring by name.
		setTransactionManager((PlatformTransactionManager) manager);
	}

}
