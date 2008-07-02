package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Logger;

public class CalendarUtils {

    private Logger log = Logger.getLogger(this.getClass());
    
    public Date nextMonday(Date date){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);

        switch(cal.get(GregorianCalendar.DAY_OF_WEEK)){
        
            case GregorianCalendar.MONDAY :
            {
                cal.add(GregorianCalendar.DATE, 7);
                break;
            }
            case GregorianCalendar.TUESDAY :
            {
                cal.add(GregorianCalendar.DATE, 6);
                break;
            }
            case GregorianCalendar.WEDNESDAY :
            {
                cal.add(GregorianCalendar.DATE, 5);
                break;
            }
            case GregorianCalendar.THURSDAY :
            {
                cal.add(GregorianCalendar.DATE, 4);
                break;
            }
            case GregorianCalendar.FRIDAY :
            {
                cal.add(GregorianCalendar.DATE, 3);
                break;
            }
            case GregorianCalendar.SATURDAY :
            {
                cal.add(GregorianCalendar.DATE, 2);
                break;
            }
            case GregorianCalendar.SUNDAY :
            {
                cal.add(GregorianCalendar.DATE, 1);
                break;
            }
            default:{
                return null; 
            }
        }
        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 0);
        cal.set(GregorianCalendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    public int getLengthInDays(Date start, Date end){
        Calendar calStart = GregorianCalendar.getInstance();
        Calendar calEnd = GregorianCalendar.getInstance();
        calStart.setTime(start);
        calEnd.setTime(end);
        calStart.set(Calendar.HOUR, 0);
        calStart.set(Calendar.MINUTE, 0);
        calStart.set(Calendar.SECOND, 0);
        calStart.set(Calendar.MILLISECOND, 0);
        calEnd.set(Calendar.HOUR, 0);
        calEnd.set(Calendar.MINUTE, 0);
        calEnd.set(Calendar.SECOND, 0);
        calEnd.set(Calendar.MILLISECOND, 0);
        return (int)((calEnd.getTime().getTime() - calStart.getTime().getTime()) / (86400000L))+1;
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

     // invalid range
        if(start.after(end)){
            return 0;
        } 
        
        int days = 0;
        GregorianCalendar cal = new GregorianCalendar();
        
        // Add 1 day to end, as we want the actual end date included
        cal.setTime(end);
        cal.add(GregorianCalendar.DATE, 1);
        end = cal.getTime();
        cal.setTime(start);
        
        while(cal.getTime().before(end)){
            if(cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SATURDAY ||
                    cal.get(GregorianCalendar.DAY_OF_WEEK) == GregorianCalendar.SUNDAY){
                days++;
            }
            cal.add(GregorianCalendar.DATE, 1);
        }
        
        return days;
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
