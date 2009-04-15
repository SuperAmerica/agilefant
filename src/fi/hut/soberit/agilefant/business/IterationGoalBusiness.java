package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.IterationGoal;

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

}
