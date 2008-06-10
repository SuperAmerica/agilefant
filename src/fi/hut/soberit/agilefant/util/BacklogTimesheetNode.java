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
        this.childBacklogs = new ArrayList<BacklogTimesheetNode>();
        this.childBacklogItems = new ArrayList<BacklogItemTimesheetNode>();
        
        this.backlog = backlog;
        
        if(expandChildren){
            Collection<PageItem> childBacklogs = backlog.getChildren();

            if(childBacklogs != null){
                for(PageItem childBacklog : childBacklogs){
                    if((node = tsb.getNodes().get(childBacklog.getId())) != null){
                        this.childBacklogs.add(node);
                    }else{
                        node = new BacklogTimesheetNode((Backlog) childBacklog, true, tsb);
                        this.childBacklogs.add(node);
                        tsb.getNodes().put(childBacklog.getId(), node);
                    }
                }
            }
            
            Collection<BacklogItem> backlogItems = backlog.getBacklogItems();
            if(backlogItems != null){
                for(BacklogItem backlogItem : backlogItems){
                    this.childBacklogItems.add(new BacklogItemTimesheetNode(backlogItem, tsb));
                }
            }
        }

        this.hourEntries = null; // TODO vesa: Add another iterator when hour entries are created for backlogs
    }
    
    public Backlog getBacklog(){
        return this.backlog;
    }
    
    public void addChildBacklog(BacklogTimesheetNode node){
        this.childBacklogs.add(node);
    }
    
    public void addChildBacklogItems(BacklogItemTimesheetNode node){
        this.childBacklogItems.add(node);
    }
    
    public void print(){
        System.out.println(this.backlog.getName() + " " + getHourTotal());
        if(childBacklogs != null){
            for(TimesheetNode child : childBacklogs)
                child.print();
        }
        
        if(childBacklogItems != null){
            for(TimesheetNode child : childBacklogItems)
                child.print();
        }
    }
}
