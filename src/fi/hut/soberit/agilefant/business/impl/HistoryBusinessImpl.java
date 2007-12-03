package fi.hut.soberit.agilefant.business.impl;

import java.util.Date;
import java.util.LinkedList;

import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HistoryEntry;

public class HistoryBusinessImpl implements HistoryBusiness {
    private BacklogDAO backlogDAO;
    
    public void updateBacklogHistory(int backlogId) {
        Backlog backlog = backlogDAO.get(backlogId);
        BacklogHistory history = backlog.getBacklogHistory();
        
        if( history == null ) {
            history = newBacklogHistory(backlog);
        }
        
        AFTime originalEstimate = new AFTime(0);
        AFTime effortLeft = new AFTime(0);
        
        for(BacklogItem item : backlog.getBacklogItems()) {
            if( item.getOriginalEstimate() != null ) {
                originalEstimate.add( item.getOriginalEstimate() );
            }
            if( item.getEffortLeft() != null ) {
                effortLeft.add( item.getEffortLeft() );
            }
        }
        
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();
        if( entry == null ) {
            entry = new HistoryEntry<BacklogHistory>();
            entry.setDate(new java.sql.Date( new Date().getTime()));
            entry.setHistory(history);
            history.getEffortHistoryEntries().add(entry);
        }
        entry.setEffortLeft( effortLeft );
        entry.setOriginalEstimate( originalEstimate );
    }

    /**
     * Creates a <code>BacklogHistory</code> for a Backlog which is missing it.
     * @param backlog
     */
    private BacklogHistory newBacklogHistory(Backlog backlog) {
        BacklogHistory history = new BacklogHistory();
        history.setEffortHistoryEntries( new LinkedList<HistoryEntry<BacklogHistory>>());
        backlog.setBacklogHistory(history);
        return history;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }
}
