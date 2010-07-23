package fi.hut.soberit.agilefant.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateTimeUtilsTest {

    @Test
    public void testRoundToNearestMidnight_millisecondBeforeNoon() {
        DateTime original = new DateTime(2010, 02, 05, 11, 59, 59, 999);
        assertDateEquals(original, 2010, 2, 5, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_noon() {
        DateTime original = new DateTime(2010, 02, 05, 12, 0, 0, 0);
        assertDateEquals(original, 2010, 2, 6, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_millisecondAfterNoon() {
        DateTime original = new DateTime(2010, 02, 05, 12, 0, 0, 1);
        assertDateEquals(original, 2010, 2, 6, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_millisecondBeforeMidnight() {
        DateTime original = new DateTime(2010, 02, 05, 23, 59, 59, 999);
        assertDateEquals(original, 2010, 2, 6, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_midnight() {
        DateTime original = new DateTime(2010, 02, 06, 0, 0, 0, 0);
        assertDateEquals(original, 2010, 2, 6, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_millisecondAfterMidnight() {
        DateTime original = new DateTime(2010, 2, 6, 0, 0, 0, 1);
        assertDateEquals(original, 2010, 2, 6, 0, 0, 0, 0);
    }
    
    @Test
    public void testRoundToNearestMidnight_null() {
        assertNull(DateTimeUtils.roundToNearestMidnight(null));
    }

    
    private void assertDateEquals(DateTime original, int year, int month,
            int day, int hour, int minutes, int seconds, int millis) {
        DateTime actual = DateTimeUtils.roundToNearestMidnight(original);
        assertEquals(year, actual.getYear());
        assertEquals(month, actual.getMonthOfYear());
        assertEquals(day, actual.getDayOfMonth());
        assertEquals(hour, actual.getHourOfDay());
        assertEquals(minutes, actual.getMinuteOfHour());
        assertEquals(seconds, actual.getSecondOfMinute());
        assertEquals(millis, actual.getMillisOfSecond());
    }
}
