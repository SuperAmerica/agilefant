package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;

public class ExactEstimateUtils {

    private static MinorUnitsParser parser = new MinorUnitsParser("h", "min", 60);
    
    public static String convertToString(ExactEstimate estimate) {
        if (estimate == null) return "";
        return parser.convertToString(estimate.getMinorUnits());
    }
    
    public static String convertToString(SignedExactEstimate estimate) {
        if (estimate == null) return "";
        return parser.convertToString(estimate.getMinorUnits());
    }

    public static ExactEstimate convertFromString(String string) {
        if (string == null) return new ExactEstimate();
        return new ExactEstimate(parser.convertFromString(string));
    }
    
    public static ExactEstimate convertSignedFromString(String string) {
        if (string == null) return new ExactEstimate();
        return new ExactEstimate(parser.convertSignedFromString(string));
    }
    
    public static double extractMajorUnits(ExactEstimate estimate) {
        if (estimate == null) return 0.0;
        return parser.toMajorUnits(estimate.getMinorUnits());
    }

}
