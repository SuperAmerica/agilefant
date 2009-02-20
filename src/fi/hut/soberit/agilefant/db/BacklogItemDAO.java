package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.util.TodoMetrics;

/**
 * Interface for a DAO of a BacklogItem.
 * 
 * @see GenericDAO
 */
public interface BacklogItemDAO extends GenericDAO<BacklogItem> {
    
    public Map<BacklogItem, TodoMetrics> getTasksByBacklog(Backlog backlog);
    
    public List<Object[]> getResponsiblesByBacklog(Backlog backlog);
    
    public List<BacklogItem> getBacklogItemsByBacklog(Backlog backlog);

}
