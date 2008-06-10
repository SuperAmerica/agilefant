package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.*;

public class TimesheetBusinessImpl implements TimesheetBusiness {
    private BacklogDAO backlogDAO;
    private List<BacklogTimesheetNode> roots = new ArrayList<BacklogTimesheetNode>();
    private Map<Integer, BacklogTimesheetNode> nodes = new HashMap<Integer, BacklogTimesheetNode>();
    
    public void generateTree(int[] backlogIds){
        Backlog backlog, parent;
        BacklogTimesheetNode backlogNode, parentNode, childNode;
        
        roots.clear();
        nodes.clear();

        for(int id : backlogIds){
            backlog = backlogDAO.get(id);
            
            if((parent = (Backlog) backlog.getParent()) != null){
                backlogNode = new BacklogTimesheetNode(backlog, true); 
                
                if((parentNode = nodes.get(parent.getId())) != null){
                    parentNode.addChild(backlogNode);
                }else{
                    parentNode = new BacklogTimesheetNode(parent, false);
                    parentNode.addChild(backlogNode);
                    nodes.put(parentNode.getBacklog().getId(), parentNode);
                    childNode = parentNode;
                
                    while((parent = (Backlog) childNode.getBacklog().getParent()) != null){
                        if((parentNode = nodes.get(parent.getId())) != null){
                            parentNode.addChild(childNode);
                            break;
                        }else{
                            parentNode = new BacklogTimesheetNode(parent, false);
                            parentNode.addChild(childNode);
                            nodes.put(parentNode.getBacklog().getId(), parentNode);
                            childNode = parentNode;
                        }
                    }
                    
                    if(!roots.contains(parentNode))
                        roots.add(parentNode);
                    
                }
            }else{
                roots.add(new BacklogTimesheetNode(backlog, true));
            }
        }

        // DEBUG
        for(BacklogTimesheetNode root : roots){
            root.print();
        }
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
}
