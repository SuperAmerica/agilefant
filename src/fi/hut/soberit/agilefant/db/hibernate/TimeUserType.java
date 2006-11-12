package fi.hut.soberit.agilefant.db.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import fi.hut.soberit.agilefant.model.AFTime;

/**
 * Hibernate user type object, which enables saving our model.Time-classes
 * in JDBC DB, as fields of type "time".  
 * 
 * @author Turkka Äijälä
 */
public class TimeUserType implements UserType {
	
	private static final int[] SQL_TYPES = {Types.TIME};

	/** 
	 * Get the sql types to use to save our "Time" object. 
	 */
	public int[] sqlTypes() { return SQL_TYPES; }

	/** 
	 * Class of the type handled, the hibernate implementation 
	 * uses this at least for nullSafeGet and set.   
	 */	
	public Class returnedClass() { return AFTime.class; }
	
	/** 
	 * Is mutable. 
	 */	
	public boolean isMutable() { return true; }
	
	public Object deepCopy(Object value) {
		AFTime time = (AFTime)value;
		return new AFTime(time.getTime());
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
		AFTime t = (AFTime)original;		
		return t.clone();
	}	

	/**
	 * Construct an object of our type from JDBC resultSet.
	 * This is the db "deserialization" method.
	 */
	public Object nullSafeGet(ResultSet resultSet,
							  String[] names,
							  Object owner)
			throws HibernateException, SQLException {
		
		java.sql.Time time = resultSet.getTime(names[0]);
		if (resultSet.wasNull()) return null;
		
		return new AFTime(time.getTime());
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
			statement.setNull(index, Types.TIME);
			return;
		}

		AFTime time = (AFTime)value;
		statement.setTime(index, time);
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
