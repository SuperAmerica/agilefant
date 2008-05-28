package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

public interface HourEntryDAO extends GenericDAO<HourEntry> {
    public List<HourEntry> getEntriesByTarget(TimesheetLoggable target);
    public Map<Integer,Integer> getSumsByBacklog(Backlog backlog);
}
