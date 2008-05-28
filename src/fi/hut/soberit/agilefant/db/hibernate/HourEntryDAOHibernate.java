package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.HourEntry;

public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO {

    protected HourEntryDAOHibernate() {
        super(HourEntry.class);
    }


}
