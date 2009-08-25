package fi.hut.soberit.agilefant.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;
import org.joda.time.DateTime;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.conversion.TypeConversionException;

public class DateTimeConverter extends StrutsTypeConverter implements
LocaleProvider {

    private TextProvider textProvider = new TextProviderSupport(
            this.getClass(), this);

    protected DateFormat getDateFormat() {
        String pattern = textProvider.getText("struts.shortDateTime.format");
        return new SimpleDateFormat(pattern);
        // return DateFormat.getDateInstance(DateFormat.SHORT,
        // ActionContext.getContext().getLocale());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        String value = values[0];

        DateFormat df = this.getDateFormat();
        
        try {
            return new DateTime(df.parse(value));
        } catch (ParseException e) {
            throw new TypeConversionException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null) {
            return null;
        }

        Date date = ((DateTime) o).toDate();

        DateFormat df = this.getDateFormat();

        return df.format(date);
    }

    public Locale getLocale() {
        return ActionContext.getContext().getLocale();
    }

}
