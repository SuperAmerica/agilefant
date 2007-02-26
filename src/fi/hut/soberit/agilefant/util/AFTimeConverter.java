package fi.hut.soberit.agilefant.util;

import java.util.Map;

import com.opensymphony.webwork.util.WebWorkTypeConverter;

import fi.hut.soberit.agilefant.model.AFTime;

/**
 * WebWork converter for converting AFTime objects to and from
 * ui layer.
 */
public class AFTimeConverter extends WebWorkTypeConverter {

	@Override
	public Object convertFromString(Map context, String[] values, Class toClass) {
		String value = values[0];
		return new AFTime(value);
	}

	@Override
	public String convertToString(Map context, Object o) {
		return (o == null) ? null : o.toString();
	}
}
