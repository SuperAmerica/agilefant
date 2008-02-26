package fi.hut.soberit.agilefant.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyWorkLoadData {
    private Map<Integer, String> EffortsLeftMap = new HashMap<Integer, String>();
    private Map<Integer, String> OverheadsMap = new HashMap<Integer, String>();
    private Map<Integer, String> totalsMap = new HashMap<Integer, String>();
    private String[] overallTotals;
    private List<Integer> weekNumbers;
    
    public Map<Integer, String> getEffortsLeftMap() {
        return EffortsLeftMap;
    }
    public void setEffortsLeftMap(Map<Integer, String> effortsLeftMap) {
        EffortsLeftMap = effortsLeftMap;
    }
    public Map<Integer, String> getOverheadsMap() {
        return OverheadsMap;
    }
    public void setOverheadsMap(Map<Integer, String> overheadsMap) {
        OverheadsMap = overheadsMap;
    }
    public Map<Integer, String> getTotalsMap() {
        return totalsMap;
    }
    public void setTotalsMap(Map<Integer, String> totalsMap) {
        this.totalsMap = totalsMap;
    }
    public List<Integer> getWeekNumbers() {
        return weekNumbers;
    }
    public void setWeekNumbers(List<Integer> weekNumbers) {
        this.weekNumbers = weekNumbers;
    }
    public String[] getOverallTotals() {
        return overallTotals;
    }
    public void setOverallTotals(String[] overallTotals) {
        this.overallTotals = overallTotals;
    }

    
}
