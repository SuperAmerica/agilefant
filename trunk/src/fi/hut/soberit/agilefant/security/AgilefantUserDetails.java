package fi.hut.soberit.agilefant.security;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.GrantedAuthorityImpl;

import fi.hut.soberit.agilefant.model.User;

/**
 * Acegi UserDetails-implementation.
 * <p>
 * This is the "glue" between our system and Acegi authentication. Basically,
 * we provide these on request trough AgilefantUserDetailsService.
 * <p>
 * TODO: Should the user object be re-requested every time when "getUser" 
 * is called due to Hibernate session issues? If so, how do we obtain UserDAO here? 
 *  
 * @see AgilefantUserDetailsService
 * @author Turkka Äijälä
 */
public class AgilefantUserDetails implements UserDetails {

	private static final long serialVersionUID = 1262586472763367026L;
	
	private String username;
	private String password;
	private int userId;
	
	AgilefantUserDetails(User user) {	
		username = user.getLoginName();
		password = user.getPassword();
		userId = user.getId();
	}
	
	public GrantedAuthority[] getAuthorities() {
		// I have no idea what's the proper thing to put here
		return new GrantedAuthority[] {new GrantedAuthorityImpl("USER")};
	}

	/**
	 * Provide password for acegi.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Provide username for acegi.
	 */
	public String getUsername() {
		return username;
	}

	public boolean isAccountNonExpired() {		
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
	
	/**
	 * Extra functionality to provide userId for callers.
	 */
	public int getUserId() {
		return userId;
	}
}
