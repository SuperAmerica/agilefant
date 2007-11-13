package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class OrdinalEnumUserType<E extends Enum<E>> implements UserType {

    private Class<E> clazz = null;

    protected OrdinalEnumUserType(Class<E> c) {
        this.clazz = c;
    }

    private static final int[] SQL_TYPES = { Types.INTEGER };

    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    public Object assemble(Serializable cached, Object obj)
            throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object obj) throws HibernateException {
        return obj;
    }

    public Serializable disassemble(Object obj) throws HibernateException {
        return (Serializable) obj;
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y)
            return true;
        if (null == x || null == y)
            return false;
        return x.equals(y);
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet resultset, String[] as, Object obj)
            throws HibernateException, SQLException {
        int name = resultset.getInt(as[0]);
        E result = null;
        if (!resultset.wasNull()) {
            result = clazz.getEnumConstants()[name];
        }
        return result;
    }

    public void nullSafeSet(PreparedStatement preparedStatement, Object obj,
            int i) throws HibernateException, SQLException {
        if (null == obj) {
            preparedStatement.setNull(i, Types.VARCHAR);
        } else {
            preparedStatement.setInt(i, ((Enum) obj).ordinal());
        }
    }

    public Object replace(Object obj, Object obj1, Object obj2)
            throws HibernateException {
        return obj;
    }

    public Class returnedClass() {
        return this.clazz;
    }
}
