package fi.hut.soberit.agilefant.db;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;

public interface BacklogHistoryEntryDAO extends GenericDAO<BacklogHistoryEntry> {

    BacklogHistoryEntry retrieveLatest(DateTime timestamp, int backlogId);

    /**
     * Calculates a BacklogHistoryEntry.
     * 
     * The returned object's backlog is null, so it must be manually
     * set by the caller before the entry can be stored.
     * 
     * @param backlogIds backlogIds
     * @return a history entry that has no backlog
     */
    BacklogHistoryEntry calculateForBacklog(int backlogId);

}
