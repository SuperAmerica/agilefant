package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.IterationGoalMetrics;

public interface IterationGoalBusiness {

    public IterationGoal store(int id, String name, int iterationId,
            String description, int insertAtPriority)
            throws ObjectNotFoundException;

    public void remove(int iterationGoalId) throws ObjectNotFoundException;

    /**
     * 
     * @param goal
     * @param insertAtPriority
     */
    public void updateIterationGoalPriority(IterationGoal goal,
            int insertAtPriority);

    public void attachGoalToIteration(IterationGoal goal, int iterationId)
            throws ObjectNotFoundException;
       
    public void attachGoalToIteration(int iterationGoalId, int iterationId, boolean moveBacklogItems) throws ObjectNotFoundException;

    public void attachGoalToIteration(IterationGoal goal, int iterationId, boolean moveBacklogItems) throws ObjectNotFoundException;
    
    public IterationGoalMetrics getIterationGoalMetrics(int iterationGoalId, int iterationId) throws ObjectNotFoundException;

    public Collection<BacklogItem> getIterationGoalContents(IterationGoal goal,
            Iteration iter);
    
    public Collection<BacklogItem> getIterationGoalContents(int iterationGoalId, int iterationId);
}
