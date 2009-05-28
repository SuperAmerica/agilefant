package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.IterationGoalBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.IterationGoalMetrics;

public class IterationGoalBusinessImpl implements IterationGoalBusiness {

    private IterationGoalDAO iterationGoalDAO;
    
    private IterationDAO iterationDAO;
    
    private BacklogItemBusiness backlogItemBusiness;


    
    public IterationGoal store(int id, String name, int iterationId,
            String description, int insertAtPriority) throws ObjectNotFoundException {
        IterationGoal storable;
        if(id > 0) {
            storable = iterationGoalDAO.get(id);
            if(storable == null) {
                throw new ObjectNotFoundException("iterationGoal.notFound");
            }
        } else {
            storable = new IterationGoal();
            storable.setPriority(-1);
        }
        
        //accept empty name
        storable.setName(name);
        storable.setDescription(description);

        attachGoalToIteration(storable, iterationId);

        IterationGoal goal = null;
        if(storable.getId() > 0) {
            iterationGoalDAO.store(storable);
            goal = storable;
        } else {
            Integer iterationGoalId = (Integer)iterationGoalDAO.create(storable);
            goal = iterationGoalDAO.get(iterationGoalId);
        }
        
        updateIterationGoalPriority(goal, insertAtPriority);

        return goal;
    }
    
    public void remove(int iterationGoalId) throws ObjectNotFoundException {
        IterationGoal goal = iterationGoalDAO.get(iterationGoalId);
        if(goal == null) {
            throw new ObjectNotFoundException("iterationGoal.notFound");
        }
        if(goal.getIteration() != null) {
            goal.getIteration().getIterationGoals().remove(goal);
        }
        Collection<BacklogItem> backlogItems = goal.getBacklogItems();
        if(backlogItems != null) {
            for(BacklogItem item : backlogItems) {
                item.setIterationGoal(null); 
            }
        }
        iterationGoalDAO.remove(iterationGoalId);
    }
    public void attachGoalToIteration(IterationGoal goal, int iterationId) throws ObjectNotFoundException {
        this.attachGoalToIteration(goal, iterationId, false);
    }
    public void attachGoalToIteration(int iterationGoalId, int iterationId, boolean moveBacklogItems) throws ObjectNotFoundException {
        IterationGoal goal = iterationGoalDAO.get(iterationGoalId);
        if(goal == null) {
            throw new ObjectNotFoundException("iterationGoal.notFound");
        }
        this.attachGoalToIteration(goal, iterationId, moveBacklogItems);
    }
    public void attachGoalToIteration(IterationGoal goal, int iterationId, boolean moveBacklogItems) throws ObjectNotFoundException {
        Iteration newIteration = null;
        if(iterationId != 0) {
            newIteration = iterationDAO.get(iterationId);
            if(newIteration == null) {
                throw new ObjectNotFoundException("iteration.notFound");
            }
        }
        //iteration goal has to have a parent 
        if(goal.getIteration() == null && iterationId == 0) {
            throw new IllegalArgumentException("iteration.notFound");
        }
        if(goal.getIteration() != null && iterationId != 0) {
            if(goal.getIteration() != newIteration) {
                goal.getIteration().getIterationGoals().remove(goal);
                goal.setIteration(newIteration);
                goal.getIteration().getIterationGoals().add(goal);
                for(BacklogItem bli : goal.getBacklogItems()) {
                    if(moveBacklogItems) {
                        backlogItemBusiness.moveItemToBacklog(bli, newIteration, true);
                    } else {
                        backlogItemBusiness.setBacklogItemIterationGoal(bli, null);
                    }
                }
                if(!moveBacklogItems) {
                    goal.getBacklogItems().clear();
                }
            }
        } else if(iterationId != 0) {
            goal.setIteration(newIteration);
            goal.getIteration().getIterationGoals().add(goal);
        }
        
        if(goal.getIteration() == null) {
            throw new IllegalArgumentException("iterationGoal.noIteration");
        }
    }
    public void updateIterationGoalPriority(IterationGoal goal, int insertAtPriority) {
        if(insertAtPriority == goal.getPriority()) {
            return;
        }
        if(goal.getIteration() == null) {
            throw new IllegalArgumentException("iteration.notFound");
        }
        Iteration iter = goal.getIteration();
        if(iter.getIterationGoals().size() == 0) {
            throw new IllegalArgumentException("iterationGoal.notFound");
        }
        int oldPriority = goal.getPriority();
        
        for(IterationGoal item : iter.getIterationGoals()) {
            //drop new goal to its place
            if(oldPriority == -1) {
                if(item.getPriority() >= insertAtPriority) {
                    item.setPriority(item.getPriority() + 1);
                    iterationGoalDAO.store(item);
                }
            } else {
                //when prioritizing downwards raise all goals by one which are between the old and new priorities 
                if(oldPriority < insertAtPriority && 
                        item.getPriority() > oldPriority && 
                        item.getPriority() <= insertAtPriority) {
                    item.setPriority(item.getPriority() - 1);
                    iterationGoalDAO.store(item);
                }
                //vice versa when prioritizing upwards
                if(oldPriority > insertAtPriority &&
                        item.getPriority() >= insertAtPriority &&
                        item.getPriority() < oldPriority) {
                    item.setPriority(item.getPriority() + 1);
                    iterationGoalDAO.store(item);
                }
            }

        }
        goal.setPriority(insertAtPriority);
        iterationGoalDAO.store(goal);
    }
    
    public IterationGoalMetrics getIterationGoalMetrics(int iterationGoalId, int iterationId) throws ObjectNotFoundException {
        IterationGoal goal = this.iterationGoalDAO.get(iterationGoalId);
        Iteration iter = this.iterationDAO.get(iterationId);
        if(iter == null) {
            throw new ObjectNotFoundException();
        }
        return this.iterationGoalDAO.loadIterationGoalMetrics(goal, iter);
    }
    
    public Collection<BacklogItem> getIterationGoalContents(int iterationGoalId, int iterationId) {
        IterationGoal goal = iterationGoalDAO.get(iterationGoalId);
        Iteration iter = iterationDAO.get(iterationId);
        if(iter == null) {
            return null;
        }
        return getIterationGoalContents(goal, iter);
    }

    public Collection<BacklogItem> getIterationGoalContents(IterationGoal goal,
            Iteration iter) {

        List<BacklogItem> blis = backlogItemBusiness
                .getBacklogItemsByBacklogWithCache(iter);
        Collection<BacklogItem> goalBlis = new ArrayList<BacklogItem>();
        for (BacklogItem bli : blis) {
            if (bli.getIterationGoal() == goal) {
                goalBlis.add(bli);
            }
        }
        return goalBlis;
    }
    
    /*
     * AUTOGENERATED LIST OF SETTERS AND GETTERS
     */

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setBacklogItemBusiness(BacklogItemBusiness backlogItemBusiness) {
        this.backlogItemBusiness = backlogItemBusiness;
    }

 

}
