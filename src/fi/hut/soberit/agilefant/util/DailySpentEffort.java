package fi.hut.soberit.agilefant.util;

import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;

public class DailySpentEffort {
    private AFTime spentEffort = new AFTime(0);
    private Date day;
    public AFTime getSpentEffort() {
        return spentEffort;
    }
    public void setSpentEffort(AFTime spentEffort) {
        this.spentEffort = spentEffort;
    }
    public Date getDay() {
        return day;
    }
    public void setDay(Date day) {
        this.day = day;
    }
}
