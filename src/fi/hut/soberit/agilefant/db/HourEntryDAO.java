package fi.hut.soberit.agilefant.db;

import org.joda.time.DateTime;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public interface HourEntryDAO extends GenericDAO<HourEntry> {

    long calculateSumByUserAndTimeInterval(User user, DateTime startDate,
            DateTime endDate);
}
