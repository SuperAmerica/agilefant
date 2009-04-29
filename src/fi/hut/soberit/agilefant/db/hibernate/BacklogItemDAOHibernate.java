package fi.hut.soberit.agilefant.db.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.TodoMetrics;

/**
 * Hibernate implementation of BacklogItemDAO interface using
 * GenericDAOHibernate.
 */
public class BacklogItemDAOHibernate extends GenericDAOHibernate<BacklogItem>
        implements BacklogItemDAO {

    public BacklogItemDAOHibernate() {
        super(BacklogItem.class);
    }

    @SuppressWarnings("unchecked")
    public List<BacklogItem> getBacklogItemsByBacklog(Backlog backlog) {
        DetachedCriteria criteria = DetachedCriteria.forClass(BacklogItem.class);
        criteria.add(Restrictions.eq("backlog", backlog));
        criteria.setFetchMode("iterationGoal", FetchMode.JOIN);
        List<BacklogItem> items =  this.getHibernateTemplate().findByCriteria(criteria);
        for(BacklogItem item : items) {
            this.getHibernateTemplate().evict(item);
        }
        return items;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> getResponsiblesByBacklog(Backlog backlog) {
        String hql = "from BacklogItem as bli left outer join bli.responsibles as resp WHERE bli.backlog = ?";
        return (List<Object[]>)this.getHibernateTemplate().find(hql, new Object[] {backlog});
    }

    @SuppressWarnings("unchecked")
    public Map<BacklogItem, TodoMetrics> getTasksByBacklog(Backlog backlog) {
        String hql = "from BacklogItem as bli left outer join bli.tasks as task WHERE bli.backlog = ?";
        List<Object[]> respBli = this.getHibernateTemplate().find(hql, new Object[] {backlog});
        Map<BacklogItem, TodoMetrics> res = new HashMap<BacklogItem, TodoMetrics>();
        for(Object[] row : respBli) {
           BacklogItem item = (BacklogItem)row[0];
           Task task = (Task)row[1];
           if(res.get(item) == null) {
               res.put(item, new TodoMetrics());
           }
           res.get(item).addTodo(task);
        }
        return res;
    }
}
