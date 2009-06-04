package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

/**
 * Business interface for handling functionality related to Hour Entries
 * 
 * @author kjniiran
 * 
 */
public interface HourEntryBusiness extends GenericBusiness<HourEntry> {

    HourEntry store(TimesheetLoggable parent, HourEntry hourEntry);

    List<BacklogHourEntry> retrieveByParent(Backlog item);

}
