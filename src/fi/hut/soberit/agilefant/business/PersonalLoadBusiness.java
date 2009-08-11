package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;
import fi.hut.soberit.agilefant.transfer.UserLoadLimits;

public interface PersonalLoadBusiness {
    public ComputedLoadData retrieveUserLoad(User user, int weeksAhead);

    public UserLoadLimits getDailyLoadLimitsByUser(User user);
}
