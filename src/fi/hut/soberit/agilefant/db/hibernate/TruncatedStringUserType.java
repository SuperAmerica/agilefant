package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import fi.hut.soberit.agilefant.model.AFTime;

/**
 * Truncated string - user type.
 * 
 * Behaves like a normal string, other than it 
 * truncates everything going in the database
 * to 255 characters. Uses the VARCHAR - type.
 * 
 * @author Turkka Äijälä
 */
public class TruncatedStringUserType implements UserType {

	private static final int[] SQL_TYPES = {Types.VARCHAR};
	// could this be some sort of a parameter?
	private static final int   TRUNCATE_LENGTH = 255;

	/** 
	 * Get the sql types to use to save our truncated string object. 
	 */
	public int[] sqlTypes() { return SQL_TYPES; }

	/** 
	 * Class of the type handled, the hibernate implementation 
	 * uses this at least for nullSafeGet and set.   
	 */	
	public Class returnedClass() { return String.class; }
	
	/** 
	 * Is mutable. 
	 */	
	public boolean isMutable() { return true; }
	
	public Object deepCopy(Object value) {
		if(value == null)
			return null;
		String str = (String)value;		
		return new String(str);
	}	

	public boolean equals(Object x, Object y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}	
	
	/**
	 * Called during merge, should replace existing value (target) with 
	 * a new value (original). 
	 */
	public Object replace(Object original, Object target, Object owner)
	throws HibernateException {
		String t = (String)original;		
		return new String(t);
	}	

	/**
	 * Construct an object of our type from JDBC resultSet.
	 * This is the db "deserialization" method.
	 */
	public Object nullSafeGet(ResultSet resultSet,
							  String[] names,
							  Object owner)
			throws HibernateException, SQLException {
		
		String s = resultSet.getString(names[0]);
		
		if(resultSet.wasNull())
			return null;
		
		return new String( s );
		
	}

	/**
	 * Insert an object of our type into JDBC statement.
	 * This is the db "serialization" method.
	 */
	public void nullSafeSet(PreparedStatement statement,
							Object value,
							int index)
			throws HibernateException, SQLException {
				
		if (value == null) {
			statement.setNull(index, Types.VARCHAR);
			return;
		}

		String str = (String)value;				
		
		if(str.length() > TRUNCATE_LENGTH)
			str = str.substring(0, TRUNCATE_LENGTH);		
		
		statement.setString(index, str);		
	}	
	
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}	
	
	/**
	 * Make a cacheable serialization presentation of our class. 
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	/**
	 * Create an object from a cached representation.
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}	
}
