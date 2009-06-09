package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;

public interface TaskHourEntryDAO extends GenericDAO<TaskHourEntry> {
    List<TaskHourEntry> retrieveByTask(Task parent);
}
