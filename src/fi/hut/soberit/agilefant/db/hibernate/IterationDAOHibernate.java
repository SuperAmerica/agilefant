package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * Hibernate implementation of IterationDAO interface using GenericDAOHibernate.
 */
@Repository("iterationDAO")
public class IterationDAOHibernate extends GenericDAOHibernate<Iteration> implements
        IterationDAO {

    public IterationDAOHibernate() {
        super(Iteration.class);
    }

    @SuppressWarnings("unchecked")
    public Collection<Task> getTasksWithoutStoryForIteration(Iteration iteration) {
        DetachedCriteria crit = DetachedCriteria.forClass(Task.class);
        crit.add(Restrictions.eq("iteration", iteration));
        crit.add(Restrictions.isNull("story"));
        return hibernateTemplate.findByCriteria(crit);
    }
    
    private Criteria addIterationRestriction(Criteria criteria, String key, Iteration iteration) {
        Criteria result = criteria.createCriteria(key);
        result.add(Restrictions.idEq(iteration.getId()));
        return result;
    }
    
    public List<Task> getAllTasksForIteration(Iteration iteration) {
        Criteria criteria = getCurrentSession().createCriteria(Task.class);
        return asList(addIterationRestriction(criteria, "iteration", iteration));
    }
    
    private Pair<Integer, Integer> getCounOfDoneAndAll(Class<?> type, Object doneValue, String key, Iteration iteration) {
        Criteria criteria = getCurrentSession().createCriteria(type);
        criteria.setProjection(
                Projections.projectionList()
                .add(Projections.property("state"))
                .add(Projections.rowCount(), "taskCount")
                .add(Projections.groupProperty("state"), "state")
                );
        criteria = addIterationRestriction(criteria, key, iteration);
        
        List<Object[]> results = asList(criteria);
        
        int total = 0;
        int done = 0;
        
        for(Object[] row : results) {
            Integer count = (Integer) row[1];
            total += count;
            if(row[0].equals(doneValue))
                done += count;
        }
        
        return Pair.create(done,total);
    }
    
    public Pair<Integer, Integer> getCountOfDoneAndAllTasks(Iteration iteration) {
        return getCounOfDoneAndAll(Task.class, TaskState.DONE, "iteration", iteration);
    }

    public Pair<Integer, Integer> getCountOfDoneAndAllStories(Iteration iteration) {
        return getCounOfDoneAndAll(Story.class, StoryState.DONE, "backlog", iteration);
    }
}
