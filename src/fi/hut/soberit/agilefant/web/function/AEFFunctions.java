package fi.hut.soberit.agilefant.web.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.support.PropertyComparator;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.NamedObject;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;
import fi.hut.soberit.agilefant.util.MinorUnitsParser;

/**
 * custom functions for jsp pages
 */
public class AEFFunctions {

    
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

    public static String minutesToString(Long minor) {
        if(minor == null) {
            return "";
        }
        return minutesParser.convertToString(minor);
    }
    
    public static String estimateToHours(ExactEstimate estimate) {
        if(estimate == null) {
            return "";
        }
        double rounded = Math.round(estimate.floatValue() * 10 / 60.0);
        double result = rounded / 10.0;
        return "" + result + "h";
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

    public static Date dateTimeToDate(DateTime dateTime) {
        return dateTime.toDate();
    }
    
    public static DateTime currentDateTime() {
        return new DateTime();
    }
    
    public static String dateTimeToFormattedString(DateTime dateTime) {
        return dateTime.toString("YYYY-MM-dd HH:mm");
    }
    
    public static String joinNamedObjects(Collection<NamedObject> objects) {
        String retval = "";
        for (NamedObject obj : objects) {
            retval += obj.getName() + ", ";
        }
        return retval.substring(0, retval.length() - 2);
    }
    
    public static String scheduleStatus(Schedulable obj) {
        if (obj.getEndDate().isBeforeNow()) {
            return "PAST";
        }
        else if (obj.getStartDate().isAfterNow()) {
            return "FUTURE";
        }
        return "CURRENT";
    }
    
    /**
     * Merge story and task histories and sort them per the timestamp field.
     * This is needed to generate mingled revision history for stories and tasks.
     * 
     * @param first
     * @param second
     * @return merged list: first+second
     * 
     * @author aborici
     */
    @SuppressWarnings("unchecked")
    public static List<AgilefantHistoryEntry> mergeAndSort(List<AgilefantHistoryEntry> first, List<AgilefantHistoryEntry> second) {
        List<AgilefantHistoryEntry> listFirst = new ArrayList<AgilefantHistoryEntry>(first);
        List<AgilefantHistoryEntry> listSecond = new ArrayList<AgilefantHistoryEntry>(second);
        List<AgilefantHistoryEntry> list = new ArrayList<AgilefantHistoryEntry>();
      
        list.addAll(listFirst);
        list.addAll(listSecond);
        
        Collections.sort(list, new PropertyComparator("revision.timestamp",
                true, false));
        
        return list;
    }
    
}
