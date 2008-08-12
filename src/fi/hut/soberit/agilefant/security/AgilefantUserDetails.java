package fi.hut.soberit.agilefant.security;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.GrantedAuthorityImpl;
import org.springframework.security.userdetails.UserDetails;

import fi.hut.soberit.agilefant.model.User;

/**
 * Spring Security UserDetails-implementation.
 * <p>
 * This is the "glue" between our system and spring security authentication. Basically, we
 * provide these on request trough AgilefantUserDetailsService.
 * <p>
 * TODO: Should the user object be re-requested every time when "getUser" is
 * called due to Hibernate session issues? If so, how do we obtain UserDAO here?
 * 
 * @see AgilefantUserDetailsService
 * @author Turkka Äijälä
 */
public class AgilefantUserDetails implements UserDetails {

    private static final long serialVersionUID = 1262586472763367026L;

    private String username;

    private String password;
    
    private boolean enabled;

    private int userId;

    AgilefantUserDetails(User user) {
        username = user.getLoginName();
        password = user.getPassword();
        userId = user.getId();
        enabled = user.isEnabled();
    }

    public GrantedAuthority[] getAuthorities() {
        // I have no idea what's the proper thing to put here
        return new GrantedAuthority[] { new GrantedAuthorityImpl("USER") };
    }

    /**
     * Provide password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Provide username.
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
        return enabled;
    }

    /**
     * Extra functionality to provide userId for callers.
     */
    public int getUserId() {
        return userId;
    }
}
