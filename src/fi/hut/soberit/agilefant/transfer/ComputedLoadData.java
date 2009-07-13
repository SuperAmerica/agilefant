package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import flexjson.JSON;

public class ComputedLoadData {
    private List<IntervalLoadContainer> loadContainers = new ArrayList<IntervalLoadContainer>();
    private Set<Iteration> iterations = new HashSet<Iteration>();
    private Set<Project> projects = new HashSet<Project>(); 
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
    public Set<Iteration> getIterations() {
        return iterations;
    }
    public void setIterations(Set<Iteration> iterations) {
        this.iterations = iterations;
    }
    @JSON
    public Set<Project> getProjects() {
        return projects;
    }
    public void setProjects(Set<Project> projects) {
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
