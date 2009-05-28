package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.util.BacklogMetrics;

/**
 * Interface for a DAO of a Backlog.
 * 
 * @see GenericDAO
 */
public interface BacklogDAO extends GenericDAO<Backlog> {
    
    /**
     * Get the number of backlog items in done state.
     * @return
     */
    public int getNumberOfDoneBacklogItems(int backlogId);
    
    /**
     * Get the number of backlog items in done state.
     * @return
     */
    public int getNumberOfDoneBacklogItems(Backlog backlog);
    
    /**
     * Get project metrics by backlog.
     * 
     * @param backlog
     *                parent backlog for backlog items
     * @return BacklogMetrics metrics object filled with original estimate,
     *         effort left and total number of items.
     */
    public BacklogMetrics getBacklogMetrics(Backlog backlog);
    
    
    public Collection<BacklogItem> getBlisWithSpentEffortByBacklog(Backlog bl, Date start, Date end, Set<Integer> users);

}
