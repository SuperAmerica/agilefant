package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private Criteria addIterationRestriction(Criteria criteria, Collection<String> joins, Iteration iteration) {
        for(String join : joins)
            criteria = criteria.createCriteria(join);
        criteria.add(Restrictions.idEq(iteration.getId()));
        return criteria;
    }    
    
    public List<Task> getAllTasksForIteration(Iteration iteration) {
        Criteria criteria = getCurrentSession().createCriteria(Task.class);
        return asList(addIterationRestriction(criteria, Arrays.asList("iteration"), iteration));
    }
    
    private Pair<Integer, Integer> getCounOfDoneAndAll(Class<?> type, Object doneValue, Collection<String> joins, Iteration iteration) {
        Criteria criteria = getCurrentSession().createCriteria(type);
        criteria.setProjection(
                Projections.projectionList()
                .add(Projections.property("state"))
                .add(Projections.rowCount(), "taskCount")
                .add(Projections.groupProperty("state"), "state")
                );
        criteria = addIterationRestriction(criteria, joins, iteration);
        
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
        Pair<Integer, Integer> noStory = getCounOfDoneAndAll(Task.class, TaskState.DONE, Arrays.asList("iteration"), iteration);
        Pair<Integer, Integer> inStory = getCounOfDoneAndAll(Task.class, TaskState.DONE, Arrays.asList("story", "backlog"), iteration);
        return Pair.create(noStory.first + inStory.first, noStory.second + inStory.second);
    }

    public Pair<Integer, Integer> getCountOfDoneAndAllStories(Iteration iteration) {
        return getCounOfDoneAndAll(Story.class, StoryState.DONE, Arrays.asList("backlog"), iteration);
    }
    
    public List<Iteration> retrieveIterationsByIds(Set<Integer> iterationIds) {
        if(iterationIds == null || iterationIds.size() == 0) {
            return Collections.emptyList();
        }
        Criteria crit = getCurrentSession().createCriteria(Iteration.class);
        crit.add(Restrictions.in("id", iterationIds));
        return asList(crit);
    }
    
    public Map<Integer, Integer> getTotalAvailability(Set<Integer> iterationIds) {
        if(iterationIds == null || iterationIds.size() == 0) {
            return Collections.emptyMap();
        }
        Criteria crit = getCurrentSession().createCriteria(Iteration.class);
        crit.add(Restrictions.in("id", iterationIds));
        crit.createAlias("assignments", "assignments");
        crit.setProjection(Projections.projectionList().add(
                Projections.groupProperty("id")).add(
                Projections.sum("assignments.availability")));
        List<Object[]> data = asList(crit);
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for(Object[] row : data) {
            result.put((Integer)row[0], (Integer)row[1]);
        }
        return result;
    }
}
