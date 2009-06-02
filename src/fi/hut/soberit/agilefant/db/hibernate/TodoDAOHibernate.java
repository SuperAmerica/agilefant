package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Todo;

/**
 * Hibernate implementation of TodoDAO interface using GenericDAOHibernate.
 */
public class TodoDAOHibernate extends GenericDAOHibernate<Todo> implements
        TodoDAO {

    public TodoDAOHibernate() {
        super(Todo.class);
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
    public Collection<Todo> getTodosByStateAndStory(Task bli,
            State[] states) {
        String[] ids = new String[states.length + 1];
        Object[] values = new Object[states.length + 1];
        String query;

        ids[0] = "bliid";
        values[0] = bli.getId();

        query = "from Todo t where t.story.id = :bliid ";

        if (states != null && states.length != 0) {
            query += " and ( ";
            query += getStateClause(states, ids, values, 1);
            query += " )";
        }

        return (Collection<Todo>) hibernateTemplate
                .findByNamedParam(query, ids, values);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<Todo> getTodosByState(State[] states) {
        String[] ids = new String[states.length];
        Object[] values = new Object[states.length];

        String query = "from Todo t";

        if (states != null && states.length != 0) {
            query += " where ( ";
            query += getStateClause(states, ids, values, 0);
            query += " )";
        }

        return (Collection<Todo>) hibernateTemplate
                .findByNamedParam(query, ids, values);
    }

    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            Task task) {
        //List projects = null;

        if (lowLimitRank == null) {
                    hibernateTemplate
                    .bulkUpdate(
                            "update Todo t set t.rank = (t.rank + 1) where (t.rank < ?) and (t.story=?)",
                            new Object[] { lowLimitRank, task });
        } else if (upperLimitRank == null) {
                    hibernateTemplate
                    .bulkUpdate(
                            "update Todo t set t.rank = (t.rank + 1) where (t.rank >= ?) and (t.story=?)",
                            new Object[] { lowLimitRank, task });
        } else if (lowLimitRank != null && upperLimitRank != null) {
                    hibernateTemplate
                    .bulkUpdate(
                            "update Todo t set t.rank = (t.rank + 1) where (t.rank >= ?) and (t.rank < ?) and (t.story=?)",
                            new Object[] { lowLimitRank, upperLimitRank,
                                    task });
        } else
            throw new IllegalArgumentException("Both limits cannot be null.");
    }
    
    @SuppressWarnings("unchecked")
    public Todo getLowestRankedTodo(Task task) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Todo.class);
        criteria.add(Restrictions.eq("story", task));
        criteria.addOrder(Order.desc("rank"));
        List<Todo> results = hibernateTemplate.findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Todo findLowerRankedTodo(Todo todo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Todo.class);
        criteria.add(Restrictions.eq("story", todo.getTask()));
        criteria.add(Restrictions.gt("rank", todo.getRank()));
        criteria.addOrder(Order.asc("rank"));
        List<Todo> results = hibernateTemplate.findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Todo findUpperRankedTodo(Todo todo) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Todo.class);
        criteria.add(Restrictions.eq("story", todo.getTask()));
        criteria.add(Restrictions.lt("rank", todo.getRank()));
        criteria.addOrder(Order.desc("rank"));
        List<Todo> results = hibernateTemplate.findByCriteria(criteria);
        if (results == null || results.size() == 0) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
