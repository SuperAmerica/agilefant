package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.Backlog;

/**
 * Interface for a DAO of a Backlog.
 * 
 * @see GenericDAO
 */
public interface BacklogDAO extends GenericDAO<Backlog> {

    /**
     * Gets the number of backlog's child backlogs.
     */
    public Integer getNumberOfChildren(Backlog backlog);
    
    /**
     * Gets the number of backlog's child backlogs.
     */
    public Integer getNumberOfChildren(int backlogId);
}
