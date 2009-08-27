package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;

@Service("backlogHistoryEntryBusiness")
public class BacklogHistoryEntryBusinessImpl extends
        GenericBusinessImpl<BacklogHistoryEntry> implements
        BacklogHistoryEntryBusiness {

    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;
      
    @Autowired
    private BacklogDAO backlogDAO;

    public BacklogHistoryEntryBusinessImpl() {
        super(BacklogHistoryEntry.class);
    }
    
    @Autowired
    public void setBacklogHistoryEntryDAO(
            BacklogHistoryEntryDAO backlogHistoryEntryDAO) {
        this.backlogHistoryEntryDAO = backlogHistoryEntryDAO;
        this.genericDAO = backlogHistoryEntryDAO;
    }

    @Transactional
    public void updateHistory(int backlogId) {
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog instanceof Iteration) {
            backlog = backlog.getParent();
        } else if (backlog instanceof Product) {
            return;
        }
        BacklogHistoryEntry entry = backlogHistoryEntryDAO
                .calculateForBacklog(backlog.getId());
        entry.setBacklog(backlog);
        backlogHistoryEntryDAO.store(entry);
    }

    @Transactional(readOnly = true)
    public List<BacklogHistoryEntry> retrieveForTimestamps(
            List<DateTime> timestamps, int projectId) {
        List<BacklogHistoryEntry> result = new ArrayList<BacklogHistoryEntry>();
        for (DateTime timestamp : timestamps) {
            BacklogHistoryEntry entry = backlogHistoryEntryDAO.retrieveLatest(
                    timestamp, projectId);
            if (entry == null) {
                entry = new BacklogHistoryEntry();
                entry.setTimestamp(timestamp);
            }
            result.add(entry);
        }
        return result;
    }

}
