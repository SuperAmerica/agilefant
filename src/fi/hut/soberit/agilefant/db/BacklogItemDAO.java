package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * Interface for a DAO of a BacklogItem.
 * 
 * @see GenericDAO
 */
public interface BacklogItemDAO extends GenericDAO<BacklogItem> {

    /**
     * Returns the products or it's sub-backlog's backlogitems which are not
     * done or have no parent BLI.
     * 
     * @param productId
     * @return
     */
    public List<BacklogItem> nonDoneTopLevelBacklogItems(List<Backlog> backlogs);
    
    public List<BacklogItem> doneTopLevelBacklogItems(List<Backlog> backlogs);
    
    public List<BacklogItem> backlogItemChildren(int fatherId);
}
