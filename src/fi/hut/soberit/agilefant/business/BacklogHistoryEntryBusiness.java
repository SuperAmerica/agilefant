package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;

public interface BacklogHistoryEntryBusiness extends
        GenericBusiness<BacklogHistoryEntry> {

    void updateHistory(int backlogId);

    List<BacklogHistoryEntry> retrieveForTimestamps(List<DateTime> timestamps,
            int projectId);

}
