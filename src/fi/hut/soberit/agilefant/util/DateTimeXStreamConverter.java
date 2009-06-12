package fi.hut.soberit.agilefant.util;

import org.joda.time.DateTime;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.SingleValueConverter;

public class DateTimeXStreamConverter implements SingleValueConverter {

    @SuppressWarnings("unchecked")
    public boolean canConvert(Class clazz) {
        return clazz.isInstance(DateTime.class);
    }

    public Object fromString(String value) {
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        long millis = Long.parseLong(value);
        return new DateTime(millis);
    }

    public String toString(Object object) {
        if (object instanceof DateTime) {
            throw new ConversionException("Not a DateTime!");
        }
        DateTime dateTime = (DateTime) object;
        return Long.toString(dateTime.getMillis());
    }

}
