package fi.hut.soberit.agilefant.util;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

public class CalendarUtilsTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testNextMonday() {
         
            GregorianCalendar cal = new GregorianCalendar();
            CalendarUtils cUtils = new CalendarUtils();
            
            // Monday to next Monday
            cal.setTime(new Date(2006,1,5));
            assertEquals(GregorianCalendar.MONDAY, cal.get(cal.DAY_OF_WEEK));        
            Date date = cUtils.nextMonday(cal.getTime());
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());

            // Tuesday to next Monday
            cal.setTime(new Date(2006,1,6));
            assertEquals(GregorianCalendar.TUESDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());
            
            // Wednesday to next Monday
            cal.setTime(new Date(2006,1,7));
            assertEquals(GregorianCalendar.WEDNESDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());
            
            // Thursday to next Monday
            cal.setTime(new Date(2006,1,8));        
            assertEquals(GregorianCalendar.THURSDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());

            // Friday to next Monday
            cal.setTime(new Date(2006,1,9));
            assertEquals(GregorianCalendar.FRIDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());   
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());
            
            // Saturday to next Monday
            cal.setTime(new Date(2006,1,10));
            assertEquals(GregorianCalendar.SATURDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());   
            cal.setTime(date);
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());
            
            // Sunday to next Monday
            cal.setTime(new Date(2006,1,11));
            assertEquals(GregorianCalendar.SUNDAY, cal.get(cal.DAY_OF_WEEK));        
            date = cUtils.nextMonday(cal.getTime());   
            cal.setTime(date);           
            assertEquals(GregorianCalendar.MONDAY, cal.get(GregorianCalendar.DAY_OF_WEEK));
            assertEquals(new Date(2006,1,12), cal.getTime());
        }
    }
