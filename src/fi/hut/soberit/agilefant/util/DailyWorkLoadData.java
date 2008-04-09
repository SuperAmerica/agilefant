package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;

public class DailyWorkLoadData {
    private List<Integer> weekNumbers;
    private Map<Backlog, BacklogLoadData> loadDatas = new HashMap<Backlog, BacklogLoadData>();
    private List<Backlog> backlogs = new ArrayList<Backlog>();
    private Map<Integer, AFTime> weeklyTotals = new HashMap<Integer, AFTime>();
    private Map<Integer, AFTime> weeklyEfforts = new HashMap<Integer, AFTime>();
    private Map<Integer, AFTime> weeklyOverheads = new HashMap<Integer, AFTime>();
    private AFTime overallTotal = new AFTime(0);
    private AFTime totalEffort = new AFTime(0);
    private AFTime totalOverhead = new AFTime(0);
    
    public List<Integer> getWeekNumbers() {
        return weekNumbers;
    }
    public void setWeekNumbers(List<Integer> weekNumbers) {
        this.weekNumbers = weekNumbers;
    }
    public Map<Backlog, BacklogLoadData> getLoadDatas() {
        return loadDatas;
    }
    public void setLoadDatas(Map<Backlog, BacklogLoadData> loadDatas) {
        this.loadDatas = loadDatas;
    }
    public List<Backlog> getBacklogs() {
        return backlogs;
    }
    public void setBacklogs(List<Backlog> backlogs) {
        this.backlogs = backlogs;
    }
    public Map<Integer, AFTime> getWeeklyTotals() {
        return weeklyTotals;
    }
    public void setWeeklyTotals(Map<Integer, AFTime> weeklyTotals) {
        this.weeklyTotals = weeklyTotals;
    }
    public AFTime getOverallTotal() {
        return overallTotal;
    }
    public void setOverallTotal(AFTime overallTotal) {
        this.overallTotal = overallTotal;
    }
    public Map<Integer, AFTime> getWeeklyEfforts() {
        return weeklyEfforts;
    }
    public void setWeeklyEfforts(Map<Integer, AFTime> weeklyEfforts) {
        this.weeklyEfforts = weeklyEfforts;
    }
    public Map<Integer, AFTime> getWeeklyOverheads() {
        return weeklyOverheads;
    }
    public void setWeeklyOverheads(Map<Integer, AFTime> weeklyOverheads) {
        this.weeklyOverheads = weeklyOverheads;
    }
    public AFTime getTotalEffort() {
        return totalEffort;
    }
    public void setTotalEffort(AFTime totalEffort) {
        this.totalEffort = totalEffort;
    }
    public AFTime getTotalOverhead() {
        return totalOverhead;
    }
    public void setTotalOverhead(AFTime totalOverhead) {
        this.totalOverhead = totalOverhead;
    }
}
