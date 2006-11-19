package fi.hut.soberit.agilefant.model;

import fi.hut.soberit.agilefant.db.UserDAO;

/*
 * Example spring-enabled unit test.
 */
public class UserTest extends SpringEnabledTestCase {
	
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

		// get old size
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
		
		// asserts the fields are equal to what we saved 
		assertEquals(user.getId(), user2.getId());
		assertEquals(user.getFullName(), user2.getFullName());
		assertEquals(user.getLoginName(), user2.getLoginName());
		assertEquals(user.getPassword(), user2.getPassword());
		
		// remove
		userDAO.remove(user.getId());

		// get new size
		int size2 = userDAO.getAll().size();
		
		// test that amount of users stayed the same
		assertEquals(size1, size2);				
	}	
}
