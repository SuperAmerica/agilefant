package fi.hut.soberit.agilefant.business.impl;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogItemBusinessImpl implements BacklogItemBusiness {
    private BacklogItemDAO backlogItemDAO;
    private HistoryBusiness historyBusiness;

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogItem getBacklogItem(int backlogItemId) {
        return backlogItemDAO.get(backlogItemId);
    }

    public void removeBacklogItem(int backlogItemId) throws ObjectNotFoundException{
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        
        if(backlogItem == null) {
            throw new ObjectNotFoundException("Backlog item with given id was not found."); 
        }
        // Store backlog to be able to update its history
        Backlog backlog = backlogItem.getBacklog();
        backlogItemDAO.remove(backlogItem);
        // Update backlog history for item's backlog
        historyBusiness.updateBacklogHistory(backlog.getId());
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }
    
}
