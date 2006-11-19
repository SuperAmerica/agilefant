package fi.hut.soberit.agilefant.model;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class SpringEnabledTestCase extends AbstractDependencyInjectionSpringContextTests {

	public SpringEnabledTestCase() {
		setAutowireMode(AUTOWIRE_BY_NAME); 
	}
		
	/**
	 * List of spring configs that should be loaded.
	 * Notice that this is according to project directory, not
	 * installation directory. 
	 */ 
	private static final String[] configLocations = {
		"file:conf/testApplicationContext.xml",
		"file:conf/testApplicationContext-daos.xml",
		"file:conf/applicationContext-actions.xml"}; 
	                        
	/**
	 * Tell configuration locations to spring.
	 */
	protected final String[] getConfigLocations() {
		return configLocations;
	}
}
