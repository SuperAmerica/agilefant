package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.State;

/**
 * Hibernate implementation of TaskDAO interface using GenericDAOHibernate.
 */
public class TaskDAOHibernate extends GenericDAOHibernate<Task> implements
        TaskDAO {

    public TaskDAOHibernate() {
        super(Task.class);
    }

    /**
     * */

    /**
     * Build a HQL "where" - clause, which makes given states pass. Fills given
     * id- and value-arrays, with corresponding HQL parameter names and values.
     * Starts filling from given index.
     * 
     * @param allowedStates
     *                states to allow
     * @param ids
     *                (out) HQL state parameter names
     * @param values
     *                (out) values for HQL state correspoding ids
     * @param startIndex
     *                first index of "ids" and "values" to use
     * @return where clause
     */
    private String getStateClause(State[] allowedStates, String[] ids,
            Object[] values, int startIndex) {
        String query = "";

        boolean prev = false;
        int i = 0;
        for (State state : allowedStates) {
            if (prev)
                query += " or ";
            query += "( t.state = :state" + i + " )";
            ids[i + startIndex] = "state" + i;
            values[i + startIndex] = state;
            i++;
        }

        return query;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Task> getTasksByStateAndBacklogItem(BacklogItem bli,
            State[] states) {
        String[] ids = new String[states.length + 1];
        Object[] values = new Object[states.length + 1];
        String query;

        ids[0] = "bliid";
        values[0] = bli.getId();

        query = "from Task t where t.backlogItem.id = :bliid ";

        if (states != null && states.length != 0) {
            query += " and ( ";
            query += getStateClause(states, ids, values, 1);
            query += " )";
        }

        return (Collection<Task>) super.getHibernateTemplate()
                .findByNamedParam(query, ids, values);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Task> getTasksByState(State[] states) {
        String[] ids = new String[states.length];
        Object[] values = new Object[states.length];

        String query = "from Task t";

        if (states != null && states.length != 0) {
            query += " where ( ";
            query += getStateClause(states, ids, values, 0);
            query += " )";
        }

        return (Collection<Task>) super.getHibernateTemplate()
                .findByNamedParam(query, ids, values);
    }

    /*
     * @Override public Serializable create(Task object) { if (object.getRank() ==
     * null) { object.setRank(getNewTaskRank(object)); } return
     * super.create(object); }
     * 
     * @Override public void store(Task object) { if (object.getRank() == null) {
     * object.setRank(getNewTaskRank(object)); } super.store(object); }
     */

    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            BacklogItem backlogItem) {
        List projects = null;

        if (lowLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Task t set t.rank = (t.rank + 1) where (t.rank < ?) and (t.backlogItem=?)",
                            new Object[] { lowLimitRank, backlogItem });
        } else if (upperLimitRank == null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Task t set t.rank = (t.rank + 1) where (t.rank >= ?) and (t.backlogItem=?)",
                            new Object[] { lowLimitRank, backlogItem });
        } else if (lowLimitRank != null && upperLimitRank != null) {
            super
                    .getHibernateTemplate()
                    .bulkUpdate(
                            "update Task t set t.rank = (t.rank + 1) where (t.rank >= ?) and (t.rank < ?) and (t.backlogItem=?)",
                            new Object[] { lowLimitRank, upperLimitRank,
                                    backlogItem });
        } else
            throw new IllegalArgumentException("Both limits cannot be null.");
    }

    private Integer getNewTaskRank(Task task) {
        Task lowestRankedTask = getLowestRankedTask(task.getBacklogItem());
        Integer rank;
        if (lowestRankedTask == null) {
            return new Integer(0);
        } else {
            rank = lowestRankedTask.getRank();
            if (rank == null) {
                return new Integer(0);
            } else {
                return rank + 1;
            }
        }
    }

    public Task getLowestRankedTask(BacklogItem backlogItem) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Task.class);
        criteria.add(Restrictions.eq("backlogItem", backlogItem));
        criteria.addOrder(Order.desc("rank"));
        List<Task> results = getHibernateTemplate().findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public Task findLowerRankedTask(Task task) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Task.class);
        criteria.add(Restrictions.eq("backlogItem", task.getBacklogItem()));
        criteria.add(Restrictions.gt("rank", task.getRank()));
        criteria.addOrder(Order.asc("rank"));
        List<Task> results = getHibernateTemplate().findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    public Task findUpperRankedTask(Task task) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Task.class);
        criteria.add(Restrictions.eq("backlogItem", task.getBacklogItem()));
        criteria.add(Restrictions.lt("rank", task.getRank()));
        criteria.addOrder(Order.desc("rank"));
        List<Task> results = getHibernateTemplate().findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
