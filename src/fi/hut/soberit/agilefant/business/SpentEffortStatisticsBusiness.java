package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyUserSpentEffortTO;

public interface SpentEffortStatisticsBusiness {
    List<DailyUserSpentEffortTO> retrieveByUser(User user, DateTime start, int daysForward);
}
