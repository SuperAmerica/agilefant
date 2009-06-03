package fi.hut.soberit.agilefant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class ExactEstimateUtils {

    /*
     * These should be constants, but we want to be able to change them runtime
     */
    private static String minorUnit = "min";
    private static String majorUnit = "h";
    private static int minorsPerMajor = 60;

    private static final Pattern ESTIMATE_PATTERN = Pattern
            .compile("\\s*((\\d+)([.,]\\d+)?" + majorUnit + ")?\\s*((\\d+)" + minorUnit
                    + ")?\\s*");

    public static void setMajorUnit(String majorUnit) {
        ExactEstimateUtils.majorUnit = majorUnit;
    }
    
    public static void setMinorsPerMajor(int minorsPerMajor) {
        ExactEstimateUtils.minorsPerMajor = minorsPerMajor;
    }
    
    public static void setMinorUnit(String minorUnit) {
        ExactEstimateUtils.minorUnit = minorUnit;
    }
    
    public static int getMinorsPerMajor() {
        return minorsPerMajor;
    }
    
    public static String convertToString(ExactEstimate estimate) {
        long majors = estimate.getMinorUnits() / minorsPerMajor;
        long minors = estimate.getMinorUnits() % minorsPerMajor;
        StringBuilder builder = new StringBuilder();
        builder.append(majors);
        builder.append(majorUnit);
        builder.append(' ');
        builder.append(minors);
        builder.append(minorUnit);
        return builder.toString();
    }

    public static ExactEstimate convertFromString(String string) {
        int parsedMajors;
        double parsedMajorsDecimals;
        int parsedMinors;

        Matcher matcher = ESTIMATE_PATTERN.matcher(string);

        try {
            matcher.matches();
            parsedMajors = parseIntSafely(matcher.group(2));
            parsedMajorsDecimals = parseDoubleSafely(matcher.group(3));
            parsedMinors = parseIntSafely(matcher.group(5));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid estimate input", e);
        }
        ExactEstimate estimate = new ExactEstimate();

        long minors = parsedMinors + parsedMajors * minorsPerMajor;
        minors = minors + (long) (parsedMajorsDecimals * minorsPerMajor);
        estimate.setMinorUnits(minors);

        return estimate;
    }

    private static double parseDoubleSafely(String string) {
        if (string == null || string.length() == 0)
            return 0.0;
        return Double.parseDouble(string.replace(',', '.'));
    }

    private static int parseIntSafely(String string) {
        if (string == null || string.length() == 0)
            return 0;
        return Integer.parseInt(string);
    }

}
