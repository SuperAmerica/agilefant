package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class ExactEstimateUtils {

    private static MinorUnitsParser parser = new MinorUnitsParser("h", "min", 60);
    
    public static String convertToString(ExactEstimate estimate) {
        return parser.convertToString(estimate);
    }

    public static ExactEstimate convertFromString(String string) {
        return parser.convertFromString(string);
    }

}
