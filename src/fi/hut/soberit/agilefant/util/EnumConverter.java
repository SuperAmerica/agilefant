package fi.hut.soberit.agilefant.util;

import java.util.Map;

import com.opensymphony.webwork.util.WebWorkTypeConverter;

public class EnumConverter extends WebWorkTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		return Enum.valueOf(toClass, values[0]);
	}

	@Override
	public String convertToString(Map context, Object o) {
		return (o == null) ? null : o.toString();
	}
}
