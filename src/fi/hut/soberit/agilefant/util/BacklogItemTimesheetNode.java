package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.BacklogItem;

public class BacklogItemTimesheetNode extends TimesheetNode {
    private BacklogItem backlogItem;
    
    public BacklogItemTimesheetNode(BacklogItem backlogItem){
        this.backlogItem = backlogItem;
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
        System.out.println(this.backlogItem.getName());
    }
}
