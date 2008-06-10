package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.web.page.PageItem;

public class BacklogTimesheetNode extends TimesheetNode {
    private Backlog backlog;
    
    public BacklogTimesheetNode(Backlog backlog, boolean expandChildren, TimesheetBusiness tsb){
        BacklogTimesheetNode node;
        this.children = new ArrayList<TimesheetNode>();
        this.backlog = backlog;
        
        if(expandChildren){
            Collection<PageItem> childBacklogs = backlog.getChildren();

            if(childBacklogs != null){
                for(PageItem childBacklog : childBacklogs){
                    if((node = tsb.getNodes().get(childBacklog.getId())) != null){
                        this.children.add(node);
                    }else{
                        node = new BacklogTimesheetNode((Backlog) childBacklog, true, tsb);
                        this.children.add(node);
                        tsb.getNodes().put(childBacklog.getId(), node);
                    }
                }
            }
            
            Collection<BacklogItem> backlogItems = backlog.getBacklogItems();
            if(backlogItems != null){
                for(BacklogItem backlogItem : backlogItems){
                    this.children.add(new BacklogItemTimesheetNode(backlogItem, tsb));
                }
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
        System.out.println(this.backlog.getName() + " " + getHourTotal());
        if(children != null){
            for(TimesheetNode child : children)
                child.print();
        }
    }
    
    /*
    public void setBacklog(Backlog backlog){
        this.backlog = backlog;
    }
    */
}
