package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * UserType, which saves strings as VARCHARs.
 * <p>
 * Normally you wouldn't write UserTypes that do nothing special, you would use
 * Hibernate built-in types instead. This is for the filtering functionality
 * however: it enables a UserType to be at the bottom of the filtering
 * hierarchy.
 * 
 * @author Turkka Äijälä
 * @see fi.hut.soberit.agilefant.db.hibernate.TextUserType
 * @see fi.hut.soberit.agilefant.db.hibernate.UserTypeFilter
 */
public class VarcharUserType implements UserType {

    private static final int[] SQL_TYPES = { Types.VARCHAR };

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @SuppressWarnings("unchecked")
    public Class returnedClass() {
        return String.class;
    }

    public boolean isMutable() {
        return false;
    }

    public Object deepCopy(Object value) {
        if (value == null)
            return null;
        String str = (String) value;
        return new String(str);
    }

    public boolean equals(Object x, Object y) {
        if (x == y)
            return true;
        if (x == null || y == null)
            return false;
        return x.equals(y);
    }

    public Object replace(Object original, Object target, Object owner)
            throws HibernateException {
        String t = (String) original;
        return new String(t);
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
            throws HibernateException, SQLException {

        String s = resultSet.getString(names[0]);

        if (resultSet.wasNull())
            return null;

        return new String(s);
    }

    public void nullSafeSet(PreparedStatement statement, Object value, int index)
            throws HibernateException, SQLException {

        if (value == null) {
            statement.setNull(index, Types.VARCHAR);
            return;
        }

        String str = (String) value;

        statement.setString(index, str);
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, Object owner)
            throws HibernateException {
        return cached;
    }
}
