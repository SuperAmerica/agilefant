package fi.hut.soberit.agilefant.util;

public final class JSONUtils {
    /**
     * Replace all carriage returns, newlines and quote-marks.
     * @param str
     * @return
     */
    public static String stringToJSON(String str) {
       str = str.replaceAll("'", "\\\\'");
       str = str.replaceAll("\n", "\\\\n");
       str = str.replaceAll("\r", "\\\\r");
       return str;
    }
}
