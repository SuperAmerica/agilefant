package fi.hut.soberit.agilefant.transfer;

import java.util.Calendar;
import java.util.Date;

public class DailySpentEffort {
    private Long spentEffort = null;
    private Date day;
    public Long getSpentEffort() {
        return spentEffort;
    }
    public void setSpentEffort(Long spentEffort) {
        this.spentEffort = spentEffort;
    }
    public Date getDay() {
        return day;
    }
    public void setDay(Date day) {
        this.day = day;
    }
    public int getDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.day);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    public int getMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.day);
        return cal.get(Calendar.MONTH) +1;  
    }
    
    public int getDayOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.day);
        return cal.get(Calendar.DAY_OF_YEAR);
    }
}
