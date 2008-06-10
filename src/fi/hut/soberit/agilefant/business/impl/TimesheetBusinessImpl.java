package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.*;

public class TimesheetBusinessImpl implements TimesheetBusiness {
    private BacklogDAO backlogDAO;
    private HourEntryBusiness hourEntryBusiness;
    private List<BacklogTimesheetNode> roots = new ArrayList<BacklogTimesheetNode>();
    
    /** The map contains all nodes that are already in the tree, mapped by backlog id.
     *  It is used to avoid creating duplicate instances of the same node, and to group
     *  backlogs properly.   
     */
    private Map<Integer, BacklogTimesheetNode> nodes = new HashMap<Integer, BacklogTimesheetNode>();
    
    /**
     * {@inheritDoc}
     */
    public List<BacklogTimesheetNode> generateTree(int[] backlogIds){
        Backlog backlog, parent;
        BacklogTimesheetNode backlogNode, parentNode, childNode;
        
        roots.clear();
        nodes.clear();

        for(int id : backlogIds){
            backlog = backlogDAO.get(id);
            if(!nodes.containsKey(backlog.getId())){
                if((parent = (Backlog) backlog.getParent()) != null){
                    backlogNode = new BacklogTimesheetNode(backlog, true, this); 
                    
                    if((parentNode = nodes.get(parent.getId())) != null){
                        parentNode.addChildBacklog(backlogNode);
                    }else{
                        parentNode = new BacklogTimesheetNode(parent, false, this);
                        parentNode.addChildBacklog(backlogNode);
                        nodes.put(parentNode.getBacklog().getId(), parentNode);
                        childNode = parentNode;
                    
                        while((parent = (Backlog) childNode.getBacklog().getParent()) != null){
                            if((parentNode = nodes.get(parent.getId())) != null){
                                parentNode.addChildBacklog(childNode);
                                break;
                            }else{
                                parentNode = new BacklogTimesheetNode(parent, false, this);
                                parentNode.addChildBacklog(childNode);
                                nodes.put(parentNode.getBacklog().getId(), parentNode);
                                childNode = parentNode;
                            }
                        }
                        
                        if(!roots.contains(parentNode))
                            roots.add(parentNode);
                        
                    }
                }else{
                    roots.add(new BacklogTimesheetNode(backlog, true, this));
                }
            }
        }
        // DEBUG
        if(roots != null){
            for(BacklogTimesheetNode root : roots){
                root.print();
            }
        }
        
        return roots;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    /**
     * {@inheritDoc}
     */
    public List<BacklogTimesheetNode> getRoots() {
        return roots;
    }

    public void setRoots(List<BacklogTimesheetNode> roots) {
        // Not available
    }

    /**
     * {@inheritDoc}
     */
    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    /**
     * {@inheritDoc}
     */
    public Map<Integer, BacklogTimesheetNode> getNodes() {
        return nodes;
    }
}
