package fi.hut.soberit.agilefant.web.function;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.MinorUnitsParser;

/**
 * custom functions for jsp pages
 */
public class AEFFunctions {

    private static int maxStrLength = 30;
    
    private static MinorUnitsParser minutesParser = new MinorUnitsParser("h", "min", 60);

    public static boolean isProduct(Object obj) {
        return obj instanceof Product;
    }

    public static boolean isProject(Object obj) {
        return obj instanceof Project;
    }

    public static boolean isIteration(Object obj) {
        return obj instanceof Iteration;
    }

    public static boolean isUser(Object obj) {
        return obj instanceof User;
    }

    /**
     * Chop strings to MAX_STR_LENGTH
     * 
     * @param s
     *            string to shorten
     * @return shorter string, or original string if length < MAX_STR_LENGTH
     */
    public static String out(String s) {
        return out(s, maxStrLength, false);
    }

    /**
     * Shorten strings to specified length
     * 
     * @param s
     *            string to shorten
     * @param newLength
     *            length
     * @return shorter string, or original string if length < newLength
     */
    public static String out(String s, int newLength) {
        return out(s, newLength, false);
    }

    public static String htmlOut(String s) {
        return out(s, maxStrLength, true);
    }

    public static String htmlOut(String s, int newLength) {
        return out(s, newLength, true);
    }

    public static String minutesToString(long minor) {
        return minutesParser.convertToString(minor);
    }
    
    public static String nl2br(String s) {
        if (s == null)
            return s;
        if (!s.contains("<br")) { // hack to avoid doubling new lines
            s = s.replaceAll("\r\n", "<br>");
            s = s.replaceAll("\n", "<br>");
            s = s.replaceAll("\r", "<br>");
        }
        return s;
    }

    public static String escapeHTML(String s) {
        s = s.replaceAll("\"", "\\\\\"");
        s = s.replaceAll("\r\n", "\\\\n");
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\r", "\\\\r");
        return s;
    }

    private static String out(String s, int newLength, boolean asHtml) {
        String title = s.replaceAll("\\<.*?>", "");
        title = title.replaceAll("/\"/", "&quote;");
        return asHtml ? "<span title=\"" + title + "\">" + s + "</span>" : s;
    }

    public void setMaxStrLength(int maxStrLength) {
        AEFFunctions.maxStrLength = maxStrLength;
    }

    public static boolean isBeforeThisDay(DateTime date) {
        return date.isBeforeNow();
    }

    public static boolean listContains(Collection<Object> coll, Object object) {
        if (coll == null)
            return false;
        return coll.contains(object);
    }

    public static List<?> substract(Collection<?> first, Collection<?> second) {
        List<?> list = new ArrayList<Object>(first);
        list.removeAll(second);
        return list;
    }

    public static String currentTime() {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return fmt.format(GregorianCalendar.getInstance().getTime());
    }

    public static String timestampToString(Timestamp ts) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return fmt.format(ts);
    }

    public static String stripHTML(String htmlString) {
        if (htmlString == null) {
            return null;
        }
        return htmlString.replaceAll("\\<.*?>", "");
    }

    public static Date dateTimeToDate(DateTime dateTime) {
        return dateTime.toDate();
    }
    
    public static DateTime currentDateTime() {
        return new DateTime();
    }
    
    public static String dateTimeToFormattedString(DateTime dateTime) {
        return dateTime.toString("YYYY-MM-dd HH:mm");
    }
}
