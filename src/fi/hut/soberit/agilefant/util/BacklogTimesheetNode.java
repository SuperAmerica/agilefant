package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * A class representing a backlog node in the timesheet tree.
 * Holds other backlogs and backlog items (separately) as its children.
 * With its superclass it calculates the effort spent for this backlog and its children.
 *  
 * @author Vesa Pirila
 *
 */
public class BacklogTimesheetNode extends TimesheetNode {
    private Backlog backlog;
    
    /**
     * 
     * @param backlog The backlog to be stored in this node
     * @param expandChildren Whether or not to include this node's children in the tree. When building the tree
     *                       upwards from the selected node to the root, do not expand.
     * @param tsb The timesheetBusiness instance to enable getting the hour entries by backlog (item) id
     */
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
   
    /**
     * Get the Backlog of the node. Used at least to get the node's name.
     */
    public Backlog getBacklog(){
        return this.backlog;
    }
    
    /**
     * Add a backlog under this backlog (for instance a Project under a Product)
     * @param node The child backlog node
     */
    public void addChildBacklog(BacklogTimesheetNode node){
        this.childBacklogs.add(node);
    }
    
    /**
     * Add a backlog item under this backlog
     * @param node The child backlog item node
     */
    public void addChildBacklogItems(BacklogItemTimesheetNode node){
        this.childBacklogItems.add(node);
    }
    
    /**
     * {@inheritDoc}
     */
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