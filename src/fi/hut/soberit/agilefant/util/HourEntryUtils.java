package fi.hut.soberit.agilefant.util;


public class HourEntryUtils {

    private static MinorUnitsParser parser = new MinorUnitsParser("h", "min", 60);
    
    public static String convertToString(long minutesSpent) {
        return parser.convertToString(minutesSpent);
    }

    public static long convertFromString(String string) {
        if (string == null) return 0;
        return parser.convertFromString(string);
    }

}
