package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IntervalLoadContainer;

public interface PersonalLoadBusiness {
    public List<IntervalLoadContainer> retrieveUserLoad(User user, int weeksAhead);
}
