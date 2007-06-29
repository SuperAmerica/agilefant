package fi.hut.soberit.agilefant.security;

import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.springframework.dao.DataAccessException;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import org.acegisecurity.userdetails.UserDetailsService;

/**
 * Acegi UserDetailsService-implementation.
 * <p>
 * This is the "glue" between our system and Acegi authentication. Basically,
 * we provide AgilefantUserDetails-objects on request.
 * 
 * @see AgilefantUserDetails
 * @author Turkka Äijälä
 */
public class AgilefantUserDetailsService implements UserDetailsService {

	private static class AgilefantDataAccessException extends DataAccessException {		

		private static final long serialVersionUID = -4433098907957189538L;

		AgilefantDataAccessException(String msg) {
			super(msg);
		}
	}
	
	private UserDAO userDAO;
	
	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
	
	/**
	 * API method to provide UserDetails-object for given username.
	 * Returns AgilefantUserDetails - instances.
	 */
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
		if(userDAO == null)
			throw new AgilefantDataAccessException("DAO was null");

		// try getting user by given username
		User user =  userDAO.getUser(userName);
		
		// no user found, throw exception
		if(user == null)
			throw new UsernameNotFoundException("no such user: " + userName);
		
		// success, return UserDetails-instance
		return new AgilefantUserDetails(user);
	}
}
