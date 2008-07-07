package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public class EnumUserType implements UserType, ParameterizedType {

    public static final String USE_ORDINAL_PARAM = "useOrdinal";

    public static final String ENUM_CLASS_NAME = "enumClassName";

    @SuppressWarnings("unchecked")
    private Class<Enum> clazz = null;

    private boolean useOrdinal;

    private static final int[] VARCHAR_SQL_TYPES = { Types.VARCHAR };

    private static final int[] INT_SQL_TYPES = { Types.INTEGER };

    @SuppressWarnings("unchecked")
    public Class returnedClass() {
        return clazz;
    }

    @SuppressWarnings("unchecked")
    public void setParameterValues(Properties params) {
        String enumClassName = params.getProperty(ENUM_CLASS_NAME);
        try {
            clazz = (Class<Enum>) Class.forName(enumClassName);
        } catch (Exception e) {
            throw new MappingException("Failed to create class "
                    + enumClassName + ". Check param " + ENUM_CLASS_NAME);
        }
        useOrdinal = "true".equalsIgnoreCase(params
                .getProperty(USE_ORDINAL_PARAM));
    }

    public int[] sqlTypes() {
        return useOrdinal ? INT_SQL_TYPES : VARCHAR_SQL_TYPES;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object obj)
            throws HibernateException, SQLException {
        if (useOrdinal) {
            int ordinal = rs.getInt(names[0]);
            if (rs.wasNull()) {
                return null;
            } else {
                return clazz.getEnumConstants()[ordinal];
            }
        } else {
            String value = rs.getString(names[0]);
            if (rs.wasNull()) {
                return null;
            } else {
                return Enum.valueOf(clazz, value);
            }
        }
    }

    public void nullSafeSet(PreparedStatement stmt, Object obj, int index)
            throws HibernateException, SQLException {
        if (obj == null) {
            stmt.setNull(index, this.sqlTypes()[0]);
        } else if (useOrdinal) {
            stmt.setInt(index, ((Enum) obj).ordinal());
        } else {
            stmt.setString(index, ((Enum) obj).name());
        }
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

    public Object replace(Object obj, Object obj1, Object obj2)
            throws HibernateException {
        return obj;
    }
}