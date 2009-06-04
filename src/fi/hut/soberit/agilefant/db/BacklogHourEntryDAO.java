package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;

public interface BacklogHourEntryDAO extends GenericDAO<BacklogHourEntry> {

    List<BacklogHourEntry> retrieveByBacklog(Backlog parent);

}
