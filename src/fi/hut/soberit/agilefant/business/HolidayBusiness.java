package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

public interface HolidayBusiness extends GenericBusiness<Holiday> {
    public List<Holiday> retrieveFutureHolidaysByUser(User user);
}
