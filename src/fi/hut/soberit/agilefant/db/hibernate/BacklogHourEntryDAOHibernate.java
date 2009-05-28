package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;

@Repository("backlogHourEntryDAO")
public class BacklogHourEntryDAOHibernate extends GenericDAOHibernate<BacklogHourEntry> implements
        BacklogHourEntryDAO {

    public BacklogHourEntryDAOHibernate() {
        super(BacklogHourEntry.class);
    }

}
