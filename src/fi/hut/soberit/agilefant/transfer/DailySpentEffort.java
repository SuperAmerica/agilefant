package fi.hut.soberit.agilefant.transfer;

import org.joda.time.DateTime;

public class DailySpentEffort {
    private Long spentEffort = null;
    private DateTime day;
    public Long getSpentEffort() {
        return spentEffort;
    }
    public void setSpentEffort(Long spentEffort) {
        this.spentEffort = spentEffort;
    }
    public DateTime getDay() {
        return day;
    }
    public void setDay(DateTime day) {
        this.day = day;
    }
    public int getDate() {
        return day.getDayOfMonth();
    }
    public int getMonth() {
        return day.getMonthOfYear();
    }
    
    public int getDayOfYear() {
        return day.getDayOfYear();
    }
}
