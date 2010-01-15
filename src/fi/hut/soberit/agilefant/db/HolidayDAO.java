package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

public interface HolidayDAO extends GenericDAO<Holiday> {

    public List<Holiday> retrieveFutureHolidaysByUser(User user);
}

