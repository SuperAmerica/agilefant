package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;

public class BacklogItemHourEntryDAOHibernate extends GenericDAOHibernate<BacklogItemHourEntry> implements
        BacklogItemHourEntryDAO {

    protected BacklogItemHourEntryDAOHibernate() {
        super(BacklogItemHourEntry.class);
    }


    @SuppressWarnings("unchecked")
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem target) {
        Criteria crit = getSession().createCriteria(BacklogItemHourEntry.class)
            .add(Restrictions.eq("backlogItem",target));
        return (List<BacklogItemHourEntry>)crit.list();
    }

    public Map<Integer, Integer> getSumsByBacklog(Backlog backlog) {

        return null;
    }


}
