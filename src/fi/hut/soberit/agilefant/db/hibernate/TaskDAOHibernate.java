package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

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
     * Build a HQL "where" - clause, which makes given states pass. Fills
     * given id- and value-arrays, with corresponding HQL parameter names and
     * values. Starts filling from given index.
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
}
