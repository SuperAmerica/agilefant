package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.util.DailySpentEffort;

public class SpentEffortAction extends ActionSupport {

    private static final long serialVersionUID = -8867256217181600965L;
    public static final int WEEKS_IN_WEEK_SELECTION = 21;
    private int week = -1;
    private int year = 0;
    private int day = 1;
    private int currentWeek = 0;
    private int currentYear = 0;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    private int userId;
    private long weekEffort = 0;
    private LocalDate prevWeek;
    private LocalDate nextWeek;
    
    private List<DailySpentEffort> dailyEffort;
    private List<LocalDate> weeks = new ArrayList<LocalDate>();
    private List<HourEntry> effortEntries;
    
    
    public void initializeWeekSelection(DateTime middle) {
        this.weeks.clear();
        MutableDateTime iteratorDate = new MutableDateTime(middle.minusWeeks(WEEKS_IN_WEEK_SELECTION/2));
        for(int i = 0; i < WEEKS_IN_WEEK_SELECTION; i++) {
            this.weeks.add(iteratorDate.toDateTime().toLocalDate());
            iteratorDate.addWeeks(1);
        }
    }
    
    public DateTime getSelectedDate() {
        MutableDateTime selectedTime = new MutableDateTime();
        if(this.week == 0 || this.year == 0) {
            this.week = selectedTime.getWeekOfWeekyear();
            this.year = selectedTime.getYear();
        } else {
            selectedTime.setYear(this.year);
            selectedTime.setWeekOfWeekyear(this.week);
        }
        selectedTime.setDayOfWeek(DateTimeConstants.MONDAY);
        DateTime selectedDate = selectedTime.toDateTime();
        this.prevWeek = selectedDate.minusWeeks(1).toLocalDate();
        this.nextWeek = selectedDate.plusWeeks(1).toLocalDate();
        this.currentWeek = selectedDate.getWeekOfWeekyear();
        this.currentYear = selectedDate.getYear(); 
        return selectedDate;
    }
    
    public String getDaySumsByWeek() {
        //get current day and initialize next and previous week properties
        DateTime currentDay = this.getSelectedDate();
        this.initializeWeekSelection(currentDay);
        this.dailyEffort = this.hourEntryBusiness.getDailySpentEffortByWeek(currentDay.toLocalDate(), userId);
        this.weekEffort = this.hourEntryBusiness.calculateWeekSum(currentDay.toLocalDate(), userId);
        return Action.SUCCESS;
    }

    
    public String getHourEntriesByUserAndDay() {
        MutableDateTime tmpDate = new MutableDateTime();
        tmpDate.setYear(this.year);
        tmpDate.setDayOfYear(this.day);
        this.effortEntries = this.hourEntryBusiness.getEntriesByUserAndDay(tmpDate.toDateTime().toLocalDate(), userId);
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
    
    public void setYear(int year) {
        this.year = year;
    }
    public int getYear() {
        return this.year;
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

    public List<LocalDate> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<LocalDate> weeks) {
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

    public long getWeekEffort() {
        return weekEffort;
    }

    public LocalDate getPrevWeek() {
        return prevWeek;
    }

    public LocalDate getNextWeek() {
        return nextWeek;
    }
}
