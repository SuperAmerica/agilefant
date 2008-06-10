package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;

public class BacklogTimesheetNode extends TimesheetNode {
    private Backlog backlog;
    
    public BacklogTimesheetNode(Backlog backlog, boolean expandChildren){
        this.children = new ArrayList<TimesheetNode>();
        this.backlog = backlog;
        
        if(expandChildren){
            Collection<BacklogItem> backlogItems = backlog.getBacklogItems();
            for(BacklogItem backlogItem : backlogItems){
                this.children.add(new BacklogItemTimesheetNode(backlogItem));
            }
        }

        this.hourEntries = null; // TODO vesa: Add another iterator when hour entries are created for backlogs
    }
    
    public Backlog getBacklog(){
        return this.backlog;
    }
    
    public void addChild(TimesheetNode node){
        this.children.add(node);
    }
    
    public void print(){
        System.out.println(this.backlog.getName());
        for(TimesheetNode child : children)
            child.print();
    }
    
    /*
    public void setBacklog(Backlog backlog){
        this.backlog = backlog;
    }
    */
}
