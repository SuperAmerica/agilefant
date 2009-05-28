package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;

/**
 * Interface for a DAO of an Iteration.
 * 
 * @see GenericDAO
 */
public interface IterationDAO extends GenericDAO<Iteration> {

    /**
     * Get all currently ongoing iterations.
     */
    public Collection<Iteration> getOngoingIterations();
    
    public Collection<BacklogItem> getBacklogItemsWihoutIterationGoal(Iteration iter);
}
