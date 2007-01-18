package fi.hut.soberit.agilefant.util;

import junit.framework.TestCase;

/**
 * Use for &quot;TestCase&quot;s with spring support. 
 * <p>
 * This uses ExplicitSpringSupport to plug the inherited test case 
 * automatically to spring.
 * <p>
 * If you want to use other JUnit testing classes than &quot;TestCase&quot;, 
 * consult ExplicitSpringSupport. 
 * 
 * @see ExplicitSpringSupport
 * @author Turkka Äijälä
 */
public class SpringEnabledTestCase extends TestCase {	
	
	/**
	 * Constructor which forces a call to loadSpringSupport.  
	 */
	public SpringEnabledTestCase() {
		// call loadSpringSupport
		ExplicitSpringSupport.loadSpringSupport(this);
	}
}
