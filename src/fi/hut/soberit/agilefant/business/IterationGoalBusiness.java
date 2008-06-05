package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogLoadData;
import fi.hut.soberit.agilefant.util.EffortSumData;

public interface IterationGoalBusiness {

    /**
     * Move an iteration goal's rank up by one position related to other
     * goals in the iteration.
     * @param iterGoal the goal to be moved
     */
    public void moveUp(IterationGoal iterGoal);

    /**
     * Move an iteration goal's rank down by one position related to other
     * goals in the iteration.
     * @param iterGoal the goal to be moved
     */
    public void moveDown(IterationGoal iterGoal);

    /**
     * Move an iteration goal's rank to upmost position related to other
     * goals in the iteration.
     * @param iterGoal the goal to be moved
     */
    public void moveToTop(IterationGoal iterGoal);

    /**
     * Move an iteration goal's rank to bottomost position related to other
     * goals in the iteration.
     * @param iterGoal the goal to be moved
     */
    public void moveToBottom(IterationGoal iterGoal);
    
    /**
     * Get the priority number for a new iteration goal. 
     * @param iteration iteration where the iteration goal is created
     */
    public int getNewPriorityNumber(Iteration iteration);
}
