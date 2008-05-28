package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.HourEntry;

/**
 * Business interface for handling functionality related to Hour Entries
 * @author kjniiran
 *
 */
public interface HourEntryBusiness {

    /**
     * Creates one entry for each of the selected users
     * @param hourEntry the hour entry that we well "copy"
     * @param userIds the IDs of the users that we are adding entries for
     */
    public void addHourEntryForMultipleUsers(HourEntry hourEntry, int[] userIds);
}
