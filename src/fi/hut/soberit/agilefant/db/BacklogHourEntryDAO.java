package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;

public interface BacklogHourEntryDAO extends GenericDAO<BacklogHourEntry> {
    public List<BacklogHourEntry> getEntriesByBacklog(Backlog target);
    public List<BacklogHourEntry> getSumsByBacklog(Backlog backlog); // getSumsByBacklog?

}
