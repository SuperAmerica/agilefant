package fi.hut.soberit.agilefant.util;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.AbstractTransactionalSpringContextTests;

/**
 * A helper class for enabling Spring for fitnesse.
 * To use, use ownSetup() with target or ownSetup() and enableSpring(target)
 * and ownTearDown() on the end.
 * 
 * @author tiaijala, tvainiok
 */
public class FitnesseSpringHelper extends SpringTestCase {
	/**
	 * Public access to setUp()
	 * Remember to call enableSpring!
	 * @see AbstractTransactionalSpringContextTests
	 */
	public void ownSetUp() throws Exception {
		super.setUp();
	}

	/**
	 * Public access to setUp()
	 * @see AbstractTransactionalSpringContextTests
	 * @param target object
	 */
	public void ownSetUp(Object target) throws Exception {
		super.setUp();
		enableSpring(target);
	}
	
	/**
	 * Public access to tearDown()
	 * Ends also current transaction.
	 * @see AbstractTransactionalSpringContextTests
	 */
	public void ownTearDown() throws Exception{
		super.endTransaction();
		super.tearDown();
	}
	
	/**
	 * Public access to setComplete()
	 * @see AbstractTransactionalSpringContextTests
	 */
	public void setComplete() {
		super.setComplete();
	}

	/**
	 * Public access to endTransaction()
	 * 
	 * @see AbstractTransactionalSpringContextTests
	 */
	public void endTransaction() {
		super.endTransaction();
	}

	/**
	 * Autowire-by-name given target.
	 * Must be called for something sensible to be done with this class. 
	 * @param target to be wired
	 */
	public void enableSpring(Object target) {
		applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(target, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, true);
	}
}
