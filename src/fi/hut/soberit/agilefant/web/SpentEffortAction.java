package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.util.DailySpentEffort;

public class SpentEffortAction extends ActionSupport {

    private static final long serialVersionUID = -8867256217181600965L;
    private int week = -1;
    private int prevWeek;
    private int nextWeek;
    private int prevYear;
    private int nextYear;
    private int year = 0;
    private int day = 1;
    private int currentWeek = 0;
    private int currentYear = 0;
    private HourEntryBusiness hourEntryBusiness;
    private int userId;
    
    private List<DailySpentEffort> dailyEffort;
    private List<Object[]> weeks;
    private List<HourEntry> effortEntries;
    
    
    public String getDaySumsByWeek() {
        Calendar cal = Calendar.getInstance();
        this.currentWeek = cal.get(Calendar.WEEK_OF_YEAR);
        this.currentYear = cal.get(Calendar.YEAR);
        if(week == -1) {
            week = cal.get(Calendar.WEEK_OF_YEAR);
            year = cal.get(Calendar.YEAR);
        } else {
            cal.set(Calendar.YEAR, this.year);
            cal.set(Calendar.WEEK_OF_YEAR, this.week);
        }
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        nextWeek = cal.get(Calendar.WEEK_OF_YEAR);
        nextYear = cal.get(Calendar.YEAR);
        cal.add(Calendar.WEEK_OF_YEAR, -2);
        prevWeek = cal.get(Calendar.WEEK_OF_YEAR);
        prevYear = cal.get(Calendar.YEAR);
        this.dailyEffort = this.hourEntryBusiness.getDailySpentEffortByWeekAndUser(this.week, this.year, this.userId);
        cal.add(Calendar.WEEK_OF_YEAR, -9);
        this.weeks = new ArrayList<Object[]>();
        for(int i = 0; i < 20; i++) {
            Object[] cur = new Object[] {cal.get(Calendar.YEAR),cal.get(Calendar.WEEK_OF_YEAR), 
                    cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH)+1};
            this.weeks.add(cur);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
        }
        return SUCCESS;
    }
    
    public String getHourEntriesByUserAndDay() {
        this.effortEntries = this.hourEntryBusiness.getEntriesByUserAndDay(day, year, userId);
        return SUCCESS;
    }
    
    public List<DailySpentEffort> getDailyEffort() {
        return dailyEffort;
    }

    public void setDailyEffort(List<DailySpentEffort> dailyEffort) {
        this.dailyEffort = dailyEffort;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public int getWeek() {
        return this.week;
    }
    
    public int getPrevWeek() {
        return prevWeek;
    }

    public int getNextWeek() {
        return nextWeek;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public int getYear() {
        return this.year;
    }

    public int getPrevYear() {
        return prevYear;
    }

    public int getNextYear() {
        return nextYear;
    }

    public List<HourEntry> getEffortEntries() {
        return effortEntries;
    }

    public void setEffortEntries(List<HourEntry> effortEntries) {
        this.effortEntries = effortEntries;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<Object[]> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<Object[]> weeks) {
        this.weeks = weeks;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCurrentWeek() {
        return currentWeek;
    }

    public int getCurrentYear() {
        return currentYear;
    }
}
