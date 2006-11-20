package fi.hut.soberit.agilefant.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.acegisecurity.context.SecurityContextHolder;

import fi.hut.soberit.agilefant.model.User;

public class SecurityUtil {
	private SecurityUtil() {}
	
	/**
	 * Get currently logged-in user.
	 * 
	 * @return User object, that has logged in, or null if no user.
	 */
	public static User getLoggedUser() {
		if(SecurityContextHolder.getContext().getAuthentication() == null)
			return null;
		
		AgilefantUserDetails ud = (AgilefantUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if(ud == null) 
			return null;
				
		return ud.getUser();	
	}
	
	/**
	 * Calculate MD5 hash from a string.
	 * @param text string to calculate hash from
	 * @return MD5 hash
	 */
	public static String MD5(String text) {
		
		byte[] bytes = text.getBytes();
		
		try{
		     MessageDigest algorithm = MessageDigest.getInstance("MD5");
		     algorithm.reset();
		     algorithm.update(bytes);
		     byte messageDigest[] = algorithm.digest();
		                    
		     StringBuffer hex = new StringBuffer();
		     
		     for (int i = 0; i < messageDigest.length; i++) {
		    	 
		    	 if((messageDigest[i] & 0xff) < 16)
		    		 hex.append('0');
		    	 
		    	 hex.append( Integer.toHexString(messageDigest[i] & 0xff) );		          
		     }

		     return hex.toString();
		     
		}catch(NoSuchAlgorithmException nsae){
			return null;
		}				
	}
}
