package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO {

    protected HourEntryDAOHibernate() {
        super(HourEntry.class);
    }

    public List<HourEntry> getEntriesByTarget(TimesheetLoggable target) {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<Integer, Integer> getSumsByBacklog(Backlog backlog) {
        // TODO Auto-generated method stub
        return null;
    }


}
