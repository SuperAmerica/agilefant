package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.model.BacklogItem;

public class BacklogItemTimesheetNode extends TimesheetNode {
    private BacklogItem backlogItem;
    
    public BacklogItemTimesheetNode(BacklogItem backlogItem, TimesheetBusiness tsb){
        this.backlogItem = backlogItem;
        hourEntries = tsb.getHourEntryBusiness().getEntriesByBacklogItem(backlogItem);
    }
    
    public BacklogItem getBacklogItem(){
        return this.backlogItem;
    }
    
    public String toString(){
        return this.backlogItem.getName();
    }
    
    /*
    public void setBacklogItem(BacklogItem backlogItem){
        this.backlogItem = backlogItem;
    }
    */
    
    public void print(){
        System.out.println("  "+this.backlogItem.getName() + " " + this.getHourTotal());
    }
}
