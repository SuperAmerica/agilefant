package fi.hut.soberit.agilefant.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.opensymphony.webwork.util.WebWorkTypeConverter;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.LocaleProvider;
import com.opensymphony.xwork.TextProvider;
import com.opensymphony.xwork.TextProviderSupport;
import com.opensymphony.xwork.util.TypeConversionException;

/**
 * Converts dates. Uses WebWorks i18n and key webwork.date.format 
 * for finding pattern. This allows different patterns to be used
 * for different locales.
 */
public class OnlyDateConverter extends WebWorkTypeConverter implements LocaleProvider{
	
	private TextProvider textProvider = new TextProviderSupport(this.getClass(), this);
	
	public Locale getLocale() {
		return ActionContext.getContext().getLocale();
	}

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		String value = values[0];
		
		DateFormat df = this.getDateFormat();
		
		try{
			return df.parse(value);
		} catch (ParseException e){
			throw new TypeConversionException(e.getMessage());
		}
	}

	@Override
	public String convertToString(Map context, Object o) {
		if (o == null){
			return null;
		}

		Date date = (Date)o;
		
		DateFormat df = this.getDateFormat();
		
		return df.format(date);
	}
	
	protected DateFormat getDateFormat(){
		String pattern = textProvider.getText("webwork.date.format");
		return new SimpleDateFormat(pattern);
//		return DateFormat.getDateInstance(DateFormat.SHORT, ActionContext.getContext().getLocale());
	}
}
