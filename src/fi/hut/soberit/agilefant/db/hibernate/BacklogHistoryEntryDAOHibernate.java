package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHistoryEntry;
import fi.hut.soberit.agilefant.model.StoryState;

@Repository("backlogHistoryEntryDAO")
public class BacklogHistoryEntryDAOHibernate extends
        GenericDAOHibernate<BacklogHistoryEntry> implements
        BacklogHistoryEntryDAO {

    public BacklogHistoryEntryDAOHibernate() {
        super(StoryHistoryEntry.class);
    }

    public BacklogHistoryEntry retrieveLatest(DateTime timestamp, int backlogId) {
        Criteria crit = getCurrentSession().createCriteria(
                BacklogHistoryEntry.class);
        crit.add(Restrictions.eq("backlog.id", backlogId));
        crit.add(Restrictions.le("timestamp", timestamp));
        crit.addOrder(Order.desc("timestamp"));
        crit.setMaxResults(1);
        return uniqueResult(crit);
    }

    public BacklogHistoryEntry calculateForBacklog(int backlogId) {
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.createAlias("backlog", "backlog");
        crit.createAlias("backlog.parent", "parentBacklog",
                CriteriaSpecification.LEFT_JOIN);
        crit.add(Restrictions.or(Restrictions.eq("backlog.id", backlogId),
                Restrictions.eq("parentBacklog.id", backlogId)));
        crit.setProjection(Projections.projectionList().add(
                Projections.sum("storyPoints")).add(
                Projections.groupProperty("state"), "state"));
        List<Object[]> resultsByState = asList(crit);
        long estimateSum = 0;
        long doneSum = 0;
        if (resultsByState != null) {
            for (Object[] results : resultsByState) {
                int stateEstimateSum = ((Integer) results[0]).intValue();
                estimateSum += stateEstimateSum;
                StoryState state = (StoryState) results[1];
                if (state == StoryState.DONE) {
                    doneSum += stateEstimateSum;
                }
            }
        }
        BacklogHistoryEntry entry = new BacklogHistoryEntry();
        entry.setTimestamp(new DateTime());
        entry.setEstimateSum(estimateSum);
        entry.setDoneSum(doneSum);
        return entry;
    }
    
}
