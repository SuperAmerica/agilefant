package fi.hut.soberit.agilefant.util;

import java.util.List;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.HourEntry;

public abstract class TimesheetNode {
    protected List<TimesheetNode> children;
    protected List<? extends HourEntry> hourEntries;
    private AFTime spentHours = null; 
    private AFTime hourTotal = null;

    public AFTime getSpentHours(){
        if(spentHours == null){
            spentHours = new AFTime(0);
            
            if(hourEntries != null){
                for(HourEntry hourEntry : hourEntries)
                    spentHours.add(hourEntry.getTimeSpent());
            }
        }
        
        return spentHours;
    }
    
    public AFTime getHourTotal(){
        if(hourTotal == null){
            hourTotal = new AFTime(0);
            
            if(children != null) {
                for(TimesheetNode child : children)
                    hourTotal.add(child.getHourTotal());
            }
            
            hourTotal.add(getSpentHours());

        }
        
        return hourTotal;
    }
    
    public abstract void print();
}


