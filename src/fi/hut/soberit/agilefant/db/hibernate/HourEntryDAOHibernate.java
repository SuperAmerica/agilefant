package fi.hut.soberit.agilefant.db.hibernate;

import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;

@Repository("hourEntryDAO")
public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry>
        implements HourEntryDAO {

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
        if (result == null)
            return 0;
        return result;
    }

    public long calculateSumByStory(int storyId) {
        Criteria crit = getCurrentSession().createCriteria(TaskHourEntry.class);
        crit.setProjection(Projections.sum("minutesSpent"));
        crit.createCriteria("task").createCriteria("story").add(
                Restrictions.idEq(storyId));
        Long result = (Long) crit.uniqueResult();
        if (result == null)
            return 0;
        return result;
    }

    public long calculateSumFromTasksWithoutStory(int iterationId) {
        Criteria crit = getCurrentSession().createCriteria(TaskHourEntry.class);
        crit.setProjection(Projections.sum("minutesSpent"));
        Criteria taskCrit = crit.createCriteria("task");
        taskCrit.add(Restrictions.isNull("story"));
        taskCrit.createCriteria("iteration")
                .add(Restrictions.idEq(iterationId));
        Long result = (Long) crit.uniqueResult();
        if (result == null)
            return 0;
        return result;
    }

}
