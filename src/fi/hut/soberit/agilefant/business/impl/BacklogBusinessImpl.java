package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.Iterator;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogBusinessImpl implements BacklogBusiness {
    private BacklogItemDAO backlogItemDAO;

    private HistoryBusiness historyBusiness;

    private BacklogDAO backlogDAO;

    // @Override
    public void deleteMultipleItems(int backlogId, int[] backlogItemIds) {
        Backlog backlog = backlogDAO.get(backlogId);

        for (int id : backlogItemIds) {
            Collection<BacklogItem> items = backlog.getBacklogItems();
            Iterator<BacklogItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                BacklogItem item = iterator.next();
                if (item.getId() == id) {
                    iterator.remove();
                    backlogItemDAO.remove(id);
                }
            }
        }
        historyBusiness.updateBacklogHistory(backlog.getId());
    }

    public BacklogItem createBacklogItemToBacklog(int backlogId) {
        BacklogItem backlogItem = new BacklogItem();
        backlogItem = new BacklogItem();
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null)
            return null;
        backlogItem.setBacklog(backlog);
        backlog.getBacklogItems().add(backlogItem);
        return backlogItem;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws ObjectNotFoundException
     */
    public void changePriorityOfMultipleItems(int[] backlogItemIds,
            Priority priority) throws ObjectNotFoundException {

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);
            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change priority. Object with id " + id
                                + " was not found.");
            }
            bli.setPriority(priority);
        }
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }
}
