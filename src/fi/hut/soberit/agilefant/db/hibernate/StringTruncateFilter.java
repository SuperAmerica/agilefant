package fi.hut.soberit.agilefant.db.hibernate;

/**
 * UserTypeFilter to truncate all strings going downwards
 * to the database. Truncates to 255 characters.
 * 
 * @author Turkka Äijälä
 * @see UserTypeFilter
 */
public class StringTruncateFilter extends UserTypeFilter {
	
	private static final int TRUNCATE_LENGTH = 255;
	
	protected Object filterDown(Object ob) {
		if(ob == null) return null;
		if(!(ob instanceof String))
			return ob;
		
		String str = (String)ob;
		
		// if length more than the limit, truncate
		if(str.length() > TRUNCATE_LENGTH)
			str = str.substring(0, TRUNCATE_LENGTH);				
		
		return str;
    }
	
}
