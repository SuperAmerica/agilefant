package fi.hut.soberit.agilefant.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

public class CalendarUtilsTest extends TestCase {

    private CalendarUtils cUtils = new CalendarUtils();

    @SuppressWarnings("deprecation")
    public void testNextMonday() {

        GregorianCalendar cal = new GregorianCalendar();

        // Monday to next Monday
        cal.setTime(new Date(2006, 1, 5));
        assertEquals(GregorianCalendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK));
        Date date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Tuesday to next Monday
        cal.setTime(new Date(2006, 1, 6));
        assertEquals(GregorianCalendar.TUESDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Wednesday to next Monday
        cal.setTime(new Date(2006, 1, 7));
        assertEquals(GregorianCalendar.WEDNESDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Thursday to next Monday
        cal.setTime(new Date(2006, 1, 8));
        assertEquals(GregorianCalendar.THURSDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Friday to next Monday
        cal.setTime(new Date(2006, 1, 9));
        assertEquals(GregorianCalendar.FRIDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Saturday to next Monday
        cal.setTime(new Date(2006, 1, 10));
        assertEquals(GregorianCalendar.SATURDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());

        // Sunday to next Monday
        cal.setTime(new Date(2006, 1, 11));
        assertEquals(GregorianCalendar.SUNDAY, cal.get(Calendar.DAY_OF_WEEK));
        date = cUtils.nextMonday(cal.getTime());
        cal.setTime(date);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(new Date(2006, 1, 12), cal.getTime());
    }

    @SuppressWarnings("deprecation")
    public void testgetLengthInDays() {
        Date start = new Date(96, 5, 1); // note months start from 0
        Date end = new Date(96, 5, 1); // note months start from 0
        assertEquals(1, this.cUtils.getLengthInDays(start, end));

        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 2); // note months start from 0
        assertEquals(2, this.cUtils.getLengthInDays(start, end));

        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 3); // note months start from 0
        assertEquals(3, this.cUtils.getLengthInDays(start, end));

        start = new Date(94, 5, 1); // note months start from 0
        end = new Date(95, 5, 1); // note months start from 0
        assertEquals(366, this.cUtils.getLengthInDays(start, end));

        start = new Date(93, 5, 1); // note months start from 0
        end = new Date(95, 5, 1); // note months start from 0
        assertEquals(731, this.cUtils.getLengthInDays(start, end));

        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 6, 1); // note months start from 0
        assertEquals(31, this.cUtils.getLengthInDays(start, end));

        start = new Date(96, 3, 8); // note months start from 0
        end = new Date(96, 3, 16); // note months start from 0
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        assertEquals(GregorianCalendar.MONDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));

        assertEquals(9, this.cUtils.getLengthInDays(start, end));
    }

    /**
     * 
     */
    @SuppressWarnings("deprecation")
    public void testGetProjectDaysDaysInTimeFrame() {

        // 1. Project fully on the timeframe
        // Note: Months start from 0 and year is set 1900 + year
        Date start = new Date(96, 5, 1); // note months start from 0
        Date end = new Date(96, 5, 7); // note months start from 0
        Date projectStart = new Date(96, 4, 1); // note months start from 0
        Date projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(7, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 1); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(7, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 4); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(4, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 3. Project fully inside timeframe, 4 days miniproject 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 2); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(5, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 3. Project ending, only partly in timeframe 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(6, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 4. Project ending today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 1); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 5. Project starting on 'sunday' (last day of the timeframe) today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 7); // note months start from 0
        projectEnd = new Date(96, 6, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 6. Project not in time frame, after
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(97, 5, 7); // note months start from 0
        projectEnd = new Date(97, 6, 6); // note months start from 0          
        assertEquals(0, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 7. Project not in time frame, before 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(94, 5, 7); // note months start from 0
        projectEnd = new Date(94, 6, 6); // note months start from 0          
        assertEquals(0, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));

        // 8. Single day project 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 6); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysDaysInTimeFrame(projectStart,
                projectEnd, start, end));
    }

    @SuppressWarnings("deprecation")
    public void testGetWeekEndDays() {
        // Note: Months start from 0 and year is set 1900 + year
        Date start = new Date(96, 5, 1); // note months start from 0
        Date end = new Date(96, 5, 7); // note months start from 0
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        assertEquals(GregorianCalendar.SATURDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(2, cUtils.getWeekEndDays(start, end));

        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 14); // note months start from 0            
        assertEquals(4, cUtils.getWeekEndDays(start, end));

        start = new Date(96, 5, 4); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0            
        assertEquals(0, cUtils.getWeekEndDays(start, end));

        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 2); // note months start from 0
        cal.setTime(start);
        assertEquals(GregorianCalendar.SATURDAY, cal
                .get(GregorianCalendar.DAY_OF_WEEK));
        assertEquals(2, cUtils.getWeekEndDays(start, end));
    }

    /**
     *
     */
    @SuppressWarnings("deprecation")
    public void testGetProjectDaysDaysList() {

        // 1. Project fully on the timeframe
        // Note: Months start from 0 and year is set 1900 + year
        Date start = new Date(96, 5, 1); // note months start from 0
        Date end = new Date(96, 5, 7); // note months start from 0
        Date projectStart = new Date(96, 4, 1); // note months start from 0
        Date projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(7, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 1); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(7, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 4); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(4, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 3. Project fully inside timeframe, 4 days miniproject 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 2); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(5, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 3. Project ending, only partly in timeframe 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(6, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 4. Project ending today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 1); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 5. Project starting on 'sunday' (last day of the timeframe) today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 7); // note months start from 0
        projectEnd = new Date(96, 6, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());

        // 6. Project not in time frame, after
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(97, 5, 7); // note months start from 0
        projectEnd = new Date(97, 6, 6); // note months start from 0          
        assertEquals(null, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true));

        // 7. Project not in time frame, before 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(94, 5, 7); // note months start from 0
        projectEnd = new Date(94, 6, 6); // note months start from 0          
        assertEquals(null, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true));

        // 8. Single day project 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 6); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, true).size());
    }

    /**
     *
     */
    @SuppressWarnings("deprecation")
    public void testGetProjectDaysDaysList_NoWeekEndDays() {

        // 1. Project fully on the timeframe
        // Note: Months start from 0 and year is set 1900 + year
        Date start = new Date(96, 5, 1); // note months start from 0
        Date end = new Date(96, 5, 7); // note months start from 0
        Date projectStart = new Date(96, 4, 1); // note months start from 0
        Date projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(5, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 1); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(5, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 2. Project not yet fully in timeframe
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 4); // note months start from 0
        projectEnd = new Date(96, 6, 2); // note months start from 0          
        assertEquals(4, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 3. Project fully inside timeframe, 4 days miniproject 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 2); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(4, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 3. Project ending, only partly in timeframe 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(4, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 4. Project ending today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 4, 1); // note months start from 0
        projectEnd = new Date(96, 5, 1); // note months start from 0          
        assertEquals(0, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 5. Project starting on 'sunday' (last day of the timeframe) today 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 7); // note months start from 0
        projectEnd = new Date(96, 6, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());

        // 6. Project not in time frame, after
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(97, 5, 7); // note months start from 0
        projectEnd = new Date(97, 6, 6); // note months start from 0          
        assertEquals(null, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false));

        // 7. Project not in time frame, before 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(94, 5, 7); // note months start from 0
        projectEnd = new Date(94, 6, 6); // note months start from 0          
        assertEquals(null, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false));

        // 8. Single day project 
        start = new Date(96, 5, 1); // note months start from 0
        end = new Date(96, 5, 7); // note months start from 0
        projectStart = new Date(96, 5, 6); // note months start from 0
        projectEnd = new Date(96, 5, 6); // note months start from 0          
        assertEquals(1, this.cUtils.getProjectDaysList(projectStart,
                projectEnd, start, end, false).size());
    }

}
