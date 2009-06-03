package fi.hut.soberit.agilefant.business;

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

}
