package fi.hut.soberit.agilefant.db;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.util.ProjectBurnupData;

public interface BacklogHistoryEntryDAO extends GenericDAO<BacklogHistoryEntry> {

    BacklogHistoryEntry retrieveLatest(DateTime timestamp, int backlogId);
    
    ProjectBurnupData retrieveBurnupData(int projectId);

}
