package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import flexjson.JSON;

public class ComputedLoadData {
    private List<IntervalLoadContainer> loadContainers = new ArrayList<IntervalLoadContainer>();
    private List<Iteration> iterations = new ArrayList<Iteration>();
    private List<Project> projects = new ArrayList<Project>(); 
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
    public List<Iteration> getIterations() {
        return iterations;
    }
    public void setIterations(List<Iteration> iterations) {
        this.iterations = iterations;
    }
    @JSON
    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
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
