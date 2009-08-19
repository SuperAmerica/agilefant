package fi.hut.soberit.agilefant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinorUnitsParser {

    private final String minorUnit;
    private final String majorUnit;

    private final Pattern PARSER_PATTERN;
    private final Pattern NUMERIC_PATTERN;

    private int minorsPerMajor;

    public MinorUnitsParser(String majorUnit, String minorUnit,
            int minorsPerMajor) {
        this.minorUnit = minorUnit;
        this.majorUnit = majorUnit;
        this.minorsPerMajor = minorsPerMajor;
        this.PARSER_PATTERN = Pattern.compile("\\s*((-?\\d+)([.,]\\d+)?"
                + majorUnit + ")?\\s*((-?\\d+)" + minorUnit + ")?\\s*");
        
        this.NUMERIC_PATTERN =
            Pattern.compile("^\\s*(-?\\d+)([.,]\\d+)?(" + majorUnit + ")?\\s*$");
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
        if (minors > 0) {
            builder.append(minors);
            builder.append(minorUnit);
        }
        return builder.toString();
    }

    public long convertFromString(String string) {
        long minors = this.convert(string);
        if(minors < 0L) {
            throw new IllegalArgumentException("Value can not be negative.");
        }
        return minors;
    }
    
    public long convertSignedFromString(String string) {
        return this.convert(string);
    }
    private long convert(String string) {
        int parsedMajors;
        double parsedMajorsDecimals;
        int parsedMinors;
        boolean isNegative = false;
        
        Matcher numericMatcher = NUMERIC_PATTERN.matcher(string);
        if (numericMatcher.matches()) {
            parsedMajors = parseIntSafely(numericMatcher.group(1));
            parsedMajorsDecimals = parseDoubleSafely(numericMatcher.group(2));
            parsedMinors = 0;
        }
        else {
            Matcher matcher = PARSER_PATTERN.matcher(string);
            try {
                matcher.matches();
                parsedMajors = parseIntSafely(matcher.group(2));
                parsedMajorsDecimals = parseDoubleSafely(matcher.group(3));
                parsedMinors = parseIntSafely(matcher.group(5));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid input", e);
            }
        }

        if(parsedMinors < 0 || parsedMajors < 0 || parsedMajorsDecimals < 0) {
            isNegative = true;
            parsedMajors = Math.abs(parsedMajors);
            parsedMajorsDecimals = Math.abs(parsedMajorsDecimals);
            parsedMinors = Math.abs(parsedMinors);
        }
        
        long minors = parsedMinors + parsedMajors * minorsPerMajor;
        minors = minors + (long) (parsedMajorsDecimals * minorsPerMajor);
        if(isNegative) {
            minors *= -1L;
        }
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
    
    public double toMajorUnits(long minorUnits) {
        return minorUnits / minorsPerMajor;
    }

}
