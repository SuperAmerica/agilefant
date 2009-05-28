package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.util.BacklogMetrics;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
public class BacklogDAOHibernate extends GenericDAOHibernate<Backlog> implements
        BacklogDAO {

    public BacklogDAOHibernate() {
        super(Backlog.class);
    }

    /** {@inheritDoc} */
    public int getNumberOfDoneBacklogItems(int backlogId) {
        return Integer.valueOf(super.getHibernateTemplate().find("select count(*) from BacklogItem b "
                + "where b.backlog = ? and b.state = ?",
                new Object[] { this.get(backlogId), State.DONE }).get(0).toString());
    }
    
    public int getNumberOfDoneBacklogItems(Backlog backlog) {
        return this.getNumberOfDoneBacklogItems(backlog.getId());
    }

    @SuppressWarnings("unchecked")
    public BacklogMetrics getBacklogMetrics(Backlog backlog) {
        BacklogMetrics metrics = new BacklogMetrics();
        DetachedCriteria bliCrit = DetachedCriteria.forClass(BacklogItem.class);
        bliCrit.add(Restrictions.eq("backlog", backlog));
        ProjectionList sums = Projections.projectionList();
        sums.add(Projections.sum("effortLeft"));
        sums.add(Projections.sum("originalEstimate"));
        sums.add(Projections.count("id"));
        sums.add(Projections.groupProperty("backlog"));
        bliCrit.setProjection(sums);
        List res = super.getHibernateTemplate().findByCriteria(bliCrit);
        try {
            Object[] sumData = (Object[])res.get(0);
            metrics.setEffortLeft((AFTime)sumData[1]);
            metrics.setOriginalEstimate((AFTime)sumData[2]);
            metrics.setTotalItems(metrics.getTotalItems() + (Integer)sumData[3]);
        } catch(Exception e) {
            return null;
        }
        return metrics;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<BacklogItem> getBlisWithSpentEffortByBacklog(Backlog bl, Date start, Date end, Set<Integer> users) {
        DetachedCriteria bliCrit = DetachedCriteria.forClass(BacklogItem.class);
        
        bliCrit.add(Restrictions.eq("backlog", bl));
        bliCrit.createAlias("hourEntries", "spentEffort");
        bliCrit.createAlias("hourEntries.user", "effUser");
        
        if(start != null) {
            bliCrit.add(Restrictions.ge("spentEffort.date", start));
        }
        if(end != null) {
            bliCrit.add(Restrictions.le("spentEffort.date", end));
        }
        if(users != null && users.size() > 0) {
            bliCrit.add(Restrictions.in("effUser.id", users));
        }
        
        //bliCrit.setProjection(Projections.projectionList()
        //        .add(Projections.sum("spentEffort.timeSpent")));
        
        List<BacklogItem> data = super.getHibernateTemplate().findByCriteria(bliCrit);
        try {
          Collection<BacklogItem> res = new HashSet<BacklogItem>();
          for(BacklogItem item : data) {
              if(!res.contains(item)) {
                  res.add(item);
              }
          }
          return res;  
        } catch(Exception e) { 
          return null;  
        } 
    }

}
