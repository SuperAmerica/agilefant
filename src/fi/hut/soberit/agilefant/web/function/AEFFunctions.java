package fi.hut.soberit.agilefant.web.function;

import java.util.Date;
import java.util.GregorianCalendar;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;


/**
 * custom functions for jsp pages
 */
public class AEFFunctions {

    private static int maxStrLength = 30;

    public static boolean isProduct(Object obj) {
        return obj instanceof Product;
    }

    public static boolean isProject(Object obj) {
        return obj instanceof Project;
    }

    public static boolean isIteration(Object obj) {
        return obj instanceof Iteration;
    }

    public static boolean isIterationGoal(Object obj) {
        return obj instanceof IterationGoal;
    }

    public static boolean isBacklogItem(Object obj) {
        return obj instanceof BacklogItem;
    }

    public static boolean isTask(Object obj) {
        return obj instanceof Task;
    }

    public static boolean isUser(Object obj) {
        return obj instanceof User;
    }

    /**
     * Chop strings to MAX_STR_LENGTH
     * 
     * @param s
     *                string to shorten
     * @return shorter string, or original string if length < MAX_STR_LENGTH
     */
    public static String out(String s) {
        return out(s, maxStrLength, false);
    }

    /**
     * Shorten strings to specified length
     * 
     * @param s
     *                string to shorten
     * @param newLength
     *                length
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

    private static String out(String s, int newLength, boolean asHtml) {
        return asHtml ? "<span title=\"" + s + "\">" + s + "</span>" : s;
    }

    public void setMaxStrLength(int maxStrLength) {
        AEFFunctions.maxStrLength = maxStrLength;
    }

    public static boolean isBeforeThisDay(Date date) {
        return date.before(GregorianCalendar.getInstance().getTime());
    }
}
