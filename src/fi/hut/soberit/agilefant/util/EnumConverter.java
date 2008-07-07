package fi.hut.soberit.agilefant.util;

import java.util.Map;

import com.opensymphony.webwork.util.WebWorkTypeConverter;

/**
 * Generic converter used to convert all enumerations.
 */
public class EnumConverter extends WebWorkTypeConverter {

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        return Enum.valueOf(toClass, values[0]);
    }

    @Override
    public String convertToString(Map context, Object o) {
        return (o == null) ? null : o.toString();
    }
}
