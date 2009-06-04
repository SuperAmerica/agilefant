package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;

@Repository("hourEntryDAO")
public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO {

    public HourEntryDAOHibernate() {
        super(HourEntry.class);
    }

}
