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
    
    /**
     * Find the iteration goal, that is just one step higher in rank in
     * the same iteration.
     * @param ig the original iteration goal
     * @return higher ranked iteration goal; null, if there is none
     */
    public IterationGoal findFirstHigherRankedIterationGoal(IterationGoal iterGoal);
    
    /**
     * Find the iteration goal, that is just one step lower in rank in
     * the same iteration.
     * @param ig the original iteration goal
     * @return lower ranked iteration goal; null, if there is none
     */
    public IterationGoal findFirstLowerRankedIterationGoal(IterationGoal iterGoal);
    
    /**
     * Get the iteration goal with the highest priority number.
     * @param iterGoal
     * @return
     */
    public IterationGoal getLowestRankedIterationGoalInIteration(Iteration iteration);
    
    /**
     * Raise priority number by 1 for every iteration goal between the
     * selected priority numbers. 
     * @param lowLimitRank
     * @param upperLimitRank
     * @param iteration
     */
    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            Iteration iteration);

    public List<IterationGoal> getGoalsByIteration(Iteration iter);
    
    public IterationGoalMetrics loadIterationGoalMetrics(IterationGoal iterationGoal, Iteration iteration);
}
