package fi.hut.soberit.agilefant.util;

import org.joda.time.DateTime;

public class DateTimeUtils {

    public static DateTime roundToNearestMidnight(DateTime date) {
        if (date == null) return null;
        return date.plusHours(12).toDateMidnight().toDateTime();
    }
}
