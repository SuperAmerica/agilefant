package fi.hut.soberit.agilefant.db.hibernate;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.Pair;

@Repository("iterationHistoryEntryDAO")
public class IterationHistoryEntryDAOHibernate extends
        GenericDAOHibernate<IterationHistoryEntry> implements
        IterationHistoryEntryDAO {

    public IterationHistoryEntryDAOHibernate() {
        super(IterationHistoryEntry.class);
    }

    public IterationHistoryEntry retrieveLatest(int iterationId) {
        Criteria crit = getCurrentSession().createCriteria(
                IterationHistoryEntry.class);
        crit.setMaxResults(1);
        crit.addOrder(Order.desc("timestamp"));
        crit.add(Restrictions.eq("iteration.id", iterationId));
        return (IterationHistoryEntry) crit.uniqueResult();
    }

    public Pair<ExactEstimate, ExactEstimate> calculateCurrentHistoryData(int iterationId) {
        Criteria crit = getCurrentSession().createCriteria(Task.class);
        crit.add(Restrictions.eq("iteration.id", iterationId));
        crit.setProjection(Projections.projectionList().add(
                Projections.sum("effortLeft")).add(
                Projections.sum("originalEstimate")));
        Object[] results = (Object[]) crit.uniqueResult();
        return Pair.create((ExactEstimate) results[0], (ExactEstimate) results[1]);
    }
    
    
    public List<IterationHistoryEntry> getHistoryEntriesForIteration(
            int iterationId) {
        Criteria crit = getCurrentSession().createCriteria(IterationHistoryEntry.class);
        crit.add(Restrictions.eq("iteration.id", iterationId));
        crit.addOrder(Order.asc("timestamp"));
        return asList(crit);
    }
    
}
