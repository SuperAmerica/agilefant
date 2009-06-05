package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.History;
import fi.hut.soberit.agilefant.model.HistoryEntry;

public interface HistoryDAO extends GenericDAO<History<?>> {
   
    /**
     * Get the latest history entry whose date is before given date.
     * @param backlogId the id of the history's owner backlog
     * @param date the date of the entry to fetch 
     * @return the history entry or null if not found
     */
    public HistoryEntry<BacklogHistory> getEntryByDate(int backlogId, java.util.Date date);

}
