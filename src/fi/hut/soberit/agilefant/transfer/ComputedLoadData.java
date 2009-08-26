package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Holiday;
import flexjson.JSON;

public class ComputedLoadData {
    private List<IntervalLoadContainer> loadContainers = new ArrayList<IntervalLoadContainer>();
    private List<Holiday> holidays = new ArrayList<Holiday>();
    private DateTime startDate;
    private DateTime endDate;
    
    
    @JSON
    public List<IntervalLoadContainer> getLoadContainers() {
        return loadContainers;
    }
    public void setLoadContainers(List<IntervalLoadContainer> loadContainers) {
        this.loadContainers = loadContainers;
    }
    @JSON
    public List<Holiday> getHolidays() {
        return holidays;
    }
    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }
    @JSON
    public DateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }
    @JSON
    public DateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }
}
