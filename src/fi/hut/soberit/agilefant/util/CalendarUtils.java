package fi.hut.soberit.agilefant.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

public class CalendarUtils {

    private Logger log = Logger.getLogger(this.getClass());
    
    public Date nextMonday(Date date) {
        DateTime jodaDate = new DateTime(date);
        DateTime nextMonday = jodaDate.plusWeeks(1).withDayOfWeek(DateTimeConstants.MONDAY);
        return nextMonday.toDateMidnight().toDate();
    }
    
    /**
     * Parse date from string. Sets the time to 12 p.m., if no time is supplied.
     * Accepted time formats are as follows:
     *  "2008-11-02" YYYY-MM-DD
     *  "2008-11-02 18:03" YYYY-MM-DD HH:MM 
     * @param date the date as string
     * @return
     * @throws ParseException 
     */
    public static Date parseDateFromString(String date) throws ParseException {
        Calendar cal = Calendar.getInstance();       
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            df.setLenient(true);
            cal.setTime(df.parse(date));
        }
        catch (ParseException pe) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(true);
            cal.setTime(df.parse(date));
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
        }
        return cal.getTime();
    }

    /**
     * Get the length of a timeframe in days rounded up. 
     */
    public static int getLengthInDays(Date start, Date end) {
        DateMidnight jodaStart = new DateTime(start).toDateMidnight();
        DateMidnight jodaEnd = new DateTime(end).toDateMidnight();
        // If end < start, we don't want a negative day amount
        int absDays = Math.abs(Days.daysBetween(jodaStart, jodaEnd).getDays());
        // Because we always round up, we can simply add 1 to the result
        return absDays + 1;
    }

    public int getProjectDaysDaysInTimeFrame(Date projectStart, Date projectEnd, Date start, Date end){
        
        // 1. Project not in timeframe
        if((projectStart.before(start) & projectEnd.before(start) ||
                projectStart.after(end) & projectEnd.after(end) )&&
                (projectEnd.compareTo(end) != 0)){
            return 0;
        }
                
        // 2. Project is fully on the week -> 7 days
        if(projectStart.before(start) && projectEnd.after(end)){
            return 7;
        }

        // Adjust search range with 1 day in both directions as before() and after()
        // dont include the start & end days
        start = new Date(start.getTime() - 86400000L);
        end = new Date(end.getTime() + 86400000L);
        
        // 3. Project fully inside week, +1 day because we want to count the start date as well 
        if(projectStart.after(start) && projectEnd.before(end)){
            return (int)((projectEnd.getTime() - projectStart.getTime()) / (86400000L)+1);
        }
        // 4. Project not yet fully on the week 
        if(projectStart.after(start) && projectEnd.after(end)){
            return (int)((end.getTime() - projectStart.getTime()) / (86400000L));
        }
        
        // 5. Project is ending, still partly on the week
        if(projectStart.before(start) && projectEnd.after(start)){
            return (int)((projectEnd.getTime() - start.getTime()) / (86400000L));
        }     
        
        // Something when wrong, didnt find any timeframe for project
        return -1;        
    }
    
    public int getProjectWeekDays(Date projectStart, Date projectEnd, Date start, Date end){
        
        // 1. Project not in timeframe
        if((projectStart.before(start) & projectEnd.before(start) ||
                projectStart.after(end) & projectEnd.after(end) )&&
                (projectEnd.compareTo(end) != 0)){
            return 0;
        }
                
        // 2. Project is fully on the week -> 7 days
        if(projectStart.before(start) && projectEnd.after(end)){
            return 2;
        }

        // Adjust search range with 1 day in both directions as before() and after()
        // dont include the start & end days
        start = new Date(start.getTime() - 86400000L);
        end = new Date(end.getTime() + 86400000L);
        
        // 3. Project fully inside week, +1 day because we want to count the start date as well 
        if(projectStart.after(start) && projectEnd.before(end)){
            return this.getWeekEndDays(projectStart, projectEnd);
        }
        // 4. Project not yet fully on the week 
        if(projectStart.after(start) && projectEnd.after(end)){
            return this.getWeekEndDays(projectStart, end);
        }
        
        // 5. Project is ending, still partly on the week
        if(projectStart.before(start) && projectEnd.after(start)){
            return this.getWeekEndDays(start, projectEnd);
        }     
        
        // Something when wrong, didnt find any timeframe for project
        return -1;        
    }
    
    public int getWeekEndDays(Date start, Date end){
        DateTime currentDate = new DateTime(start);
        DateTime jodaEnd = new DateTime(end).plusDays(1);

        int days = 0;
        while (jodaEnd.isAfter(currentDate)) {
            int dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek == DateTimeConstants.SUNDAY || dayOfWeek == DateTimeConstants.SATURDAY) {
                days++;
            }
            currentDate = currentDate.plusDays(1);
        }
        return days;
    }
    
    public static void setHoursMinutesAndSeconds(Calendar cal, int hours, int minutes, int seconds) {
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        cal.set(Calendar.MILLISECOND, 0);
    }
    
    public List<Date> getProjectDaysList(Date projectStart, Date projectEnd, Date start, 
            Date end, boolean includeWeekEnds){
        
        // 1. Project not in timeframe
        if((projectStart.before(start) & projectEnd.before(start) ||
                projectStart.after(end) & projectEnd.after(end) )&&
                (projectEnd.compareTo(end) != 0)){
            return null;
        }
            
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        List<Date> days = new ArrayList<Date>();
        // 2. Project is fully on the week -> 7 days
        if(projectStart.before(start) && projectEnd.after(end)){
            Date tmpend = new Date(end.getTime() + 86400000L);
            cal.setTime(start);
            log.debug("Project fully on week");
            while(cal.getTime().before(tmpend)){
                if(includeWeekEnds){
                    log.debug("Adding date(true):"+cal.getTime());
                    days.add(cal.getTime());
                }else{
                    if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && 
                            cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                        days.add(cal.getTime());
                        log.debug("Adding date(false):"+cal.getTime());
                    }else{
                        log.debug("Excluding"+cal.get(Calendar.DAY_OF_WEEK)+" date:"+cal.getTime());
                    }
                }
                cal.add(Calendar.DATE, 1);
            }
            return days;
        }

        // Adjust search range with 1 day in both directions as before() and after()
        // dont include the start & end days
        start = new Date(start.getTime() - 86400000L);
        end = new Date(end.getTime() + 86400000L);
        
        // 3. Project fully inside week, +1 day because we want to count the start date as well 
        if(projectStart.after(start) && projectEnd.before(end)){
            log.debug("Project fully inside week");
            Date tmpend = new Date(projectEnd.getTime() + 86400000L);
            cal.setTime(start);
            cal.setTime(projectStart);
            while(cal.getTime().before(tmpend)){
                if(includeWeekEnds){
                    days.add(cal.getTime());
                    log.debug("Adding date(true):"+cal.getTime());
                }else{
                    if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && 
                            cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                        days.add(cal.getTime());
                        log.debug("Adding date(false):"+cal.getTime());
                    }else{
                        log.debug("Excluding"+cal.get(Calendar.DAY_OF_WEEK)+" date:"+cal.getTime());
                    }
                }
                cal.add(Calendar.DATE, 1);
            }
            return days;
        }
        // 4. Project not yet fully on the week 
        if(projectStart.after(start) && projectEnd.after(end)){
            cal.setTime(projectStart);
            log.debug("Project not yeat fully on the week");
            while(cal.getTime().before(end)){
                if(includeWeekEnds){
                    days.add(cal.getTime());
                    log.debug("Adding date(true):"+cal.getTime());
                }else{
                    if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && 
                            cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                        days.add(cal.getTime());
                        log.debug("Adding date(false):"+cal.getTime());
                    }else{
                        log.debug("Excluding"+cal.get(Calendar.DAY_OF_WEEK)+" date:"+cal.getTime());
                    }
                }
                cal.add(Calendar.DATE, 1);
            }
            return days;
        }
        
        // 5. Project is ending, still partly on the week
        if(projectStart.before(start) && projectEnd.after(start)){
            log.debug("Project ending, still partly on the week");
            start = new Date(start.getTime() + 86400000L);
            projectEnd = new Date(projectEnd.getTime() + 86400000L);
            cal.setTime(start);
            while(cal.getTime().before(projectEnd)){
                if(includeWeekEnds){
                    days.add(cal.getTime());
                    log.debug("Adding date(true):"+cal.getTime());
                }else{
                    if(cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && 
                            cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY){
                        days.add(cal.getTime());
                        log.debug("Adding date(false):"+cal.getTime());
                    }else{
                        log.debug("Excluding"+cal.get(Calendar.DAY_OF_WEEK)+" date:"+cal.getTime());
                    }
                }
                cal.add(Calendar.DATE, 1);
            }
            return days;
        }     
        
        // Something when wrong, didnt find any timeframe for project
        return null;        
    }
}
