package fi.hut.soberit.agilefant.business.impl;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogItemBusinessImpl implements BacklogItemBusiness {
    private BacklogItemDAO backlogItemDAO;

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogItem getBacklogItem(int backlogItemId) {
        return backlogItemDAO.get(backlogItemId);
    }

    public boolean removeBacklogItem(int backlogItemId) {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if(backlogItem != null) {
            backlogItemDAO.remove(backlogItem);
            return true;
        } 
        else {
            return false;
        }
    }
    
}
