package fi.hut.soberit.agilefant.util;

import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.util.WebWorkTypeConverter;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class ExactEstimateConverter extends WebWorkTypeConverter {

    private final Logger log = Logger.getLogger(this.getClass());

    @SuppressWarnings("unchecked")
    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0].length() == 0) {
            return null;
        }
        ExactEstimate result = new ExactEstimate();
        double value;
        try {
            value = Double.parseDouble(values[0]);
        } catch (NumberFormatException e) {
            return null;
        }
        result.setMinorUnits((long) (value * ExactEstimateUtils
                .getMinorsPerMajor()));
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map context, Object o) {
        log.info("ToString");
        if (o == null)
            return null;
        if (o instanceof ExactEstimate)
            return ExactEstimateUtils.convertToString((ExactEstimate) o);
        return o.toString();
    }

}
