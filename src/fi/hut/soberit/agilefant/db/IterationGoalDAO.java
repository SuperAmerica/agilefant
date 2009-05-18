package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.IterationGoalMetrics;

/**
 * Interface for a DAO of an IterationGoal.
 * 
 * @see GenericDAO
 */
public interface IterationGoalDAO extends GenericDAO<IterationGoal> {

    public List<IterationGoal> getGoalsByIteration(Iteration iter);
    
    public IterationGoalMetrics loadIterationGoalMetrics(IterationGoal iterationGoal, Iteration iteration);
}
