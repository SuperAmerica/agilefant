package fi.hut.soberit.agilefant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinorUnitsParser {

    private final String minorUnit;
    private final String majorUnit;

    private final Pattern PARSER_PATTERN;

    private int minorsPerMajor;

    public MinorUnitsParser(String majorUnit, String minorUnit,
            int minorsPerMajor) {
        this.minorUnit = minorUnit;
        this.majorUnit = majorUnit;
        this.minorsPerMajor = minorsPerMajor;
        this.PARSER_PATTERN = Pattern.compile("\\s*((\\d+)([.,]\\d+)?"
                + majorUnit + ")?\\s*((\\d+)" + minorUnit + ")?\\s*");
    }

    public String convertToString(long minorsUnits) {
        long majors = minorsUnits / minorsPerMajor;
        long minors = minorsUnits % minorsPerMajor;
        StringBuilder builder = new StringBuilder();
        if (majors > 0) {
            builder.append(majors);
            builder.append(majorUnit);
            builder.append(' ');
        }
        builder.append(minors);
        builder.append(minorUnit);
        return builder.toString();
    }

    public long convertFromString(String string) {
        int parsedMajors;
        double parsedMajorsDecimals;
        int parsedMinors;

        Matcher matcher = PARSER_PATTERN.matcher(string);

        try {
            matcher.matches();
            parsedMajors = parseIntSafely(matcher.group(2));
            parsedMajorsDecimals = parseDoubleSafely(matcher.group(3));
            parsedMinors = parseIntSafely(matcher.group(5));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid input", e);
        }

        long minors = parsedMinors + parsedMajors * minorsPerMajor;
        minors = minors + (long) (parsedMajorsDecimals * minorsPerMajor);

        return minors;
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
