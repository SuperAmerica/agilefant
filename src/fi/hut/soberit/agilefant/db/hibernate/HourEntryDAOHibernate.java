package fi.hut.soberit.agilefant.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

@Repository("hourEntryDAO")
public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO {

    public HourEntryDAOHibernate() {
        super(HourEntry.class);
    }

    public long calculateSumByUserAndTimeInterval(User user,
            DateTime startDate, DateTime endDate) {
        Criteria crit = getCurrentSession().createCriteria(HourEntry.class);
        crit.add(Restrictions.eq("user", user));
        crit.add(Restrictions.between("date", startDate, endDate));
        crit.setProjection(Projections.sum("minutesSpent"));
        Long result = (Long) crit.uniqueResult();
        if (result == null) return 0;
        return result;
    }
    
}
