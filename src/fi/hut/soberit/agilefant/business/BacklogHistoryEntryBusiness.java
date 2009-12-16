package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;

public interface BacklogHistoryEntryBusiness extends
        GenericBusiness<BacklogHistoryEntry> {

    public static final Duration UPDATE_INTERVAL = new Duration(1000 * 3600L * 2L); // 2
                                                                             // hours

    /**
     * Update project history associated with the given backlog, if the given
     * backlog is a Product nothing will be done. If an existing entry has been
     * created to the history within UPDATE_INTERVAL that entry will be updated,
     * else a new entry will be created. The intension of updating entries is to
     * have a smoother history if multiple entries are updated within a short
     * period.
     */
    void updateHistory(int backlogId);

    List<BacklogHistoryEntry> retrieveForTimestamps(List<DateTime> timestamps,
            int projectId);

}
