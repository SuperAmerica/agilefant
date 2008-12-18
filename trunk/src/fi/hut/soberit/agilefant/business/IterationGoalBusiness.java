package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

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
    
    /**
     * Convert iteration goal to JSON notation.
     * @param iterGoal the iteration goal
     * @return the JSON as a string
     */
    public String iterationGoalToJSON(IterationGoal iterGoal)
        throws ObjectNotFoundException;
    
    /**
     * Convert iteration goal to JSON notation.
     * @param iterationGoalId the iteration goal
     * @return the JSON as a string
     */
    public String iterationGoalToJSON(int iterationGoalId)
        throws ObjectNotFoundException;
}
