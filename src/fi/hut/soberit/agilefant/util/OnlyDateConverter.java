package fi.hut.soberit.agilefant.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.opensymphony.webwork.util.WebWorkTypeConverter;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.util.TypeConversionException;

/**
 * TODO comments kheleniu - What is converted to what, and where used?
 */
public class OnlyDateConverter extends WebWorkTypeConverter{

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
		return DateFormat.getDateInstance(DateFormat.SHORT, ActionContext.getContext().getLocale());
	}
}
