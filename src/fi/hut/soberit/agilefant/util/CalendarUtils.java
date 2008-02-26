package fi.hut.soberit.agilefant.util;

import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarUtils {

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
        return cal.getTime();
    }
}
