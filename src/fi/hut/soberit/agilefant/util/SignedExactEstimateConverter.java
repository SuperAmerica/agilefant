package fi.hut.soberit.agilefant.util;

import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

import fi.hut.soberit.agilefant.model.SignedExactEstimate;

public class SignedExactEstimateConverter extends StrutsTypeConverter {

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0].length() == 0) {
            return null;
        }
        return ExactEstimateUtils.convertSignedFromString(values[0]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map context, Object o) {
        if (o == null)
            return null;
        if (o instanceof SignedExactEstimate)
            return ExactEstimateUtils.convertToString((SignedExactEstimate) o);
        return o.toString();
    }

}
