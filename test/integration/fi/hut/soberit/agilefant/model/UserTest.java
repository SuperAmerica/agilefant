package fi.hut.soberit.agilefant.model;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.util.SpringEnabledTestCase;

/*
 * Example spring-enabled unit test.
 * 
 * @author Turkka Äijälä
 */
public class UserTest extends SpringEnabledTestCase {
	
	private final static String validEmail = "abc@def.com";
	private final static String invalidEmail = "foo bar";
	
	private UserDAO userDAO;
	
	/*
	 * Setter for the DAO.
	 * Spring automagically calls this to fill the field.
	 */ 
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	/**
	 * Test saving, loading and deleting users. 
	 */
	public void testUser() {
		
		// if the DAO was null, spring propably did not set it
		assertNotNull("spring support not working?", userDAO);
				
		// get amount of users before any operations
		int size1 = userDAO.getAll().size();
		
		// create a test user object
		User user = new User();
		
		user.setFullName("a b");
		user.setLoginName("c");
		user.setPassword("d");
		
		// store the object
		userDAO.store(user);
				
		// request the user object we just created
		User user2 = userDAO.get(user.getId());
		
		assertNotNull("could not get user", user2);
		
		// asserts the fields are equal to what we saved 
		assertEquals(user.getId(), user2.getId());
		assertEquals(user.getFullName(), user2.getFullName());
		assertEquals(user.getLoginName(), user2.getLoginName());
		assertEquals(user.getPassword(), user2.getPassword());
		
		// remove the user we added
		userDAO.remove(user.getId());

		// get amount of users after all operations
		int size2 = userDAO.getAll().size();

		// test that amount of users was the same before and after operations
		assertEquals("amount of users differ, possibly a deletion failure?", size1, size2);				
		
		User emailuser = new User();
		emailuser.setFullName("c d");
		emailuser.setLoginName("e");
		emailuser.setPassword("f");		
		emailuser.setEmail(validEmail);
		
		userDAO.store(emailuser);

		userDAO.remove(emailuser.getId());

		User invalidEmailuser = new User();
		invalidEmailuser.setFullName("d e");
		invalidEmailuser.setLoginName("f");
		invalidEmailuser.setPassword("g");		
		invalidEmailuser.setEmail(invalidEmail);
		
		//try {
			userDAO.store(invalidEmailuser);
			//fail();
		//} catch(Exception e) {
		//}
			
		User loadedInvalidEmailuser = userDAO.get(invalidEmailuser.getId());
		
		System.out.println(loadedInvalidEmailuser.getEmail());
		
		userDAO.remove(invalidEmailuser.getId());
	}	
}
