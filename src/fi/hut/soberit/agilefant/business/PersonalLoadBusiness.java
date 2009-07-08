package fi.hut.soberit.agilefant.business;


import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;

public interface PersonalLoadBusiness {
    public ComputedLoadData retrieveUserLoad(User user, int weeksAhead);
}
