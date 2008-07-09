package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
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
    private boolean expanded = false;
    
    /**
     * 
     * @param backlog The backlog to be stored in this node
     * @param expandChildren Whether or not to include this node's children in the tree. When building the tree
     *                       upwards from the selected node to the root, do not expand.
     * @param tsb The timesheetBusiness instance to enable getting the hour entries by backlog (item) id
     */
    public BacklogTimesheetNode(Backlog backlog, boolean expandChildren, TimesheetBusiness tsb){
        this.childBacklogs = new ArrayList<BacklogTimesheetNode>();
        this.childBacklogItems = new ArrayList<BacklogItemTimesheetNode>();
        
        this.backlog = backlog;
        
        if(expandChildren){
            expandChildren(tsb, true);
        }
    }
    
    /**
     * This constructor is only used with unit tests
     * @param backlog The backlog to be stored in this node
     * @param expandChildren Whether or not to include this node's children in the tree. When building the tree
     *                       upwards from the selected node to the root, do not expand.
     * @param hourEntries The hour entries for this backlog
     */
    public BacklogTimesheetNode(Backlog backlog, boolean expandChildren, List<? extends HourEntry> hourEntries){
        this.childBacklogs = new ArrayList<BacklogTimesheetNode>();
        this.childBacklogItems = new ArrayList<BacklogItemTimesheetNode>();
        
        this.backlog = backlog;
        this.hourEntries = hourEntries;
        
        if(expandChildren)
            expandChildren(null, false);
    }
    
    /**
     * Create nodes for the child backlogs and backlog items of this backlog. 
     * @param tsb The timesheetBusiness instance is needed to check for duplicates in the forest
     * @param checkDuplicates Check if the forest already contains the given node. Should probably only be
     *                        switched off with unit tests. 
     */
    public void expandChildren(TimesheetBusiness tsb, boolean checkDuplicates){
        BacklogTimesheetNode node;
        Collection<PageItem> childBacklogs = backlog.getChildren();

        if(childBacklogs != null){
            for(PageItem childBacklog : childBacklogs){
                if(checkDuplicates && (node = tsb.getNodes().get(childBacklog.getId())) != null){
                    if(!this.childBacklogs.contains(node))
                        this.addChildBacklog(node);
                }else{
                    node = new BacklogTimesheetNode((Backlog) childBacklog, true, tsb);
                    this.addChildBacklog(node);
                    if(checkDuplicates)
                        tsb.getNodes().put(childBacklog.getId(), node);
                }
            }
        }
        
        Collection<BacklogItem> backlogItems = backlog.getBacklogItems();
        if(backlogItems != null){
            for(BacklogItem backlogItem : backlogItems){
                this.addChildBacklogItem(new BacklogItemTimesheetNode(backlogItem, tsb));
            }
        }
        
        this.hourEntries = tsb.getFilteredHourEntries(backlog);
        
        expanded = true;
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
    public void addChildBacklogItem(BacklogItemTimesheetNode node){
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

    public boolean isExpanded() {
        return expanded;
    }
}