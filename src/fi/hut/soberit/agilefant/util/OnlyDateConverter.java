package fi.hut.soberit.agilefant.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderSupport;
import com.opensymphony.xwork2.conversion.TypeConversionException;

/**
 * Converts dates. Uses Struts' i18n and key struts.date.format for finding
 * pattern. This allows different patterns to be used for different locales.
 */
public class OnlyDateConverter extends StrutsTypeConverter implements
        LocaleProvider {

    private TextProvider textProvider = new TextProviderSupport(
            this.getClass(), this);

    public Locale getLocale() {
        return ActionContext.getContext().getLocale();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        String value = values[0];

        DateFormat df = this.getDateFormat();

        try {
            return df.parse(value);
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

        Date date = (Date) o;

        DateFormat df = this.getDateFormat();

        return df.format(date);
    }

    protected DateFormat getDateFormat() {
        String pattern = textProvider.getText("struts.date.format");
        return new SimpleDateFormat(pattern);
        // return DateFormat.getDateInstance(DateFormat.SHORT,
        // ActionContext.getContext().getLocale());
    }
}
