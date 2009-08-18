package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.hut.soberit.agilefant.model.Holiday;
import flexjson.JSON;

public class ComputedLoadData {
    private List<IntervalLoadContainer> loadContainers = new ArrayList<IntervalLoadContainer>();
    private List<Holiday> holidays = new ArrayList<Holiday>();
    private Date startDate;
    private Date endDate;
    
    
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
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    @JSON
    public Date getEndDate() {
        return endDate;
    }
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
