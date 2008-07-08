package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.Backlog;

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
}
