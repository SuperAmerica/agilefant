package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.TaskState;

@Repository("hourEntryDAO")
public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry>
        implements HourEntryDAO {

    public HourEntryDAOHibernate() {
        super(HourEntry.class);
    }

    public long calculateSumByUserAndTimeInterval(int userId,
            DateTime startDate, DateTime endDate) {
        Criteria crit = getCurrentSession().createCriteria(HourEntry.class);
        crit.createCriteria("user").add(Restrictions.idEq(userId));
        crit.add(Restrictions.between("date", startDate, endDate));
        crit.setProjection(Projections.sum("minutesSpent"));
        Long result = (Long) crit.uniqueResult();
        if (result == null)
            return 0;
        return result;
    }

    private long calculateHourSum(boolean task, int storyId) {
        Class<?> type = task ? TaskHourEntry.class : StoryHourEntry.class;
        Criteria crit = getCurrentSession().createCriteria(type);
        crit.setProjection(Projections.sum("minutesSpent"));
        if(task)
            crit = crit.createCriteria("task");
        crit = crit.createCriteria("story").add(Restrictions.idEq(storyId)).add(
                Restrictions.ne("state", TaskState.DEFERRED));
        Long result = (Long) crit.uniqueResult();
        
        if (result == null)
            return 0;
        return result;
    }
    
    public long calculateSumByStory(int storyId) {
        return calculateHourSum(true, storyId) + calculateHourSum(false, storyId);
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
    
    private void setDateUserFilter(Criteria crit, DateTime start, DateTime end, Set<Integer> users) {
        if(start != null) {
            crit.add(Restrictions.ge("date", start));
        }
        if(end != null) {
            crit.add(Restrictions.le("date", end));
        }
        if(users != null && users.size() > 0) {
            crit.createAlias("user", "usr");
            crit.add(Restrictions.in("usr.id", users));
        }
    }

    public List<BacklogHourEntry> getBacklogHourEntriesByFilter(
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds) {
        if (backlogIds == null || backlogIds.size() == 0) {
            return Collections.emptyList();
        }
        Criteria crit = getCurrentSession().createCriteria(
                BacklogHourEntry.class);
        crit.createAlias("backlog", "bl", CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("backlog.parent", "blParent",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("backlog.parent.parent", "blParentParent",
                CriteriaSpecification.LEFT_JOIN);
        crit.add(Restrictions.or(Restrictions.in("bl.id", backlogIds),
                Restrictions.or(Restrictions.in("blParent.id", backlogIds),
                        Restrictions.in("blParentParent.id", backlogIds))));
        crit.addOrder(Order.desc("date"));
        this.setDateUserFilter(crit, startDate, endDate, userIds);
        return asList(crit);
    }

    public List<StoryHourEntry> getStoryHourEntriesByFilter(
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds) {
        if (backlogIds == null || backlogIds.size() == 0) {
            return Collections.emptyList();
        }

        Criteria crit = getCurrentSession()
                .createCriteria(StoryHourEntry.class);
        crit
                .createAlias("story.backlog", "bl",
                        CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("story.backlog.parent", "blParent",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("story.backlog.parent.parent", "blParentParent",
                CriteriaSpecification.LEFT_JOIN);

        crit.add(Restrictions.or(Restrictions.in("bl.id", backlogIds),
                Restrictions.or(Restrictions.in("blParent.id", backlogIds),
                        Restrictions.in("blParentParent.id", backlogIds))));
        crit.addOrder(Order.desc("date"));
        this.setDateUserFilter(crit, startDate, endDate, userIds);
        return asList(crit);
    }

    public List<TaskHourEntry> getTaskHourEntriesByFilter(
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate,
            Set<Integer> userIds) {
        if(backlogIds == null || backlogIds.size() == 0) {
            return Collections.emptyList();
        }
        
        List<TaskHourEntry> result;
        
        Criteria crit = getCurrentSession().createCriteria(TaskHourEntry.class);
        
        crit.createAlias("task.story", "story");
        crit.createAlias("task.story.backlog", "bl",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("task.story.backlog.parent", "blParent",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("task.story.backlog.parent.parent", "blParentParent",
                CriteriaSpecification.LEFT_JOIN);
        

        Criterion parentProject = Restrictions.or(Restrictions.in("bl.id", backlogIds), Restrictions
                .in("blParent.id", backlogIds));
        crit.add(Restrictions.or(Restrictions.in("blParentParent.id",
                backlogIds), parentProject));
        crit.addOrder(Order.desc("date"));
        this.setDateUserFilter(crit, startDate, endDate, userIds);
        
        result = asList(crit);
        
        //entries where task has no story attachment
        crit = getCurrentSession().createCriteria(TaskHourEntry.class);
        
        crit.createAlias("task", "task");
        crit.createAlias("task.iteration", "iBl", CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("task.iteration.parent", "iBlParent",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("task.iteration.parent.parent", "iBlParentParent",
                CriteriaSpecification.LEFT_JOIN);
        
        Criterion iterationParents = Restrictions.or(Restrictions.in(
                "iBlParent.id", backlogIds), Restrictions.in("iBlParentParent.id",
                backlogIds));
        crit.add( Restrictions.or(iterationParents,
                Restrictions.in("iBl.id", backlogIds)));
        crit.add(Restrictions.isNull("task.story"));
        crit.addOrder(Order.desc("date"));
        this.setDateUserFilter(crit, startDate, endDate, userIds);
        List<TaskHourEntry> hourentriesWithoutStory = asList(crit);
        result.addAll(hourentriesWithoutStory);
        
        return result;
    }
    
    public long calculateIterationHourEntriesSum(int iterationId) {
        long tasksEntrySum = getSumForTaskHourEntriesWithoutStoryForIteration(iterationId);
        long tasksWithStoryEntrySum = getSumForTaskHourEntriesWithStoryForIteration(iterationId);
        long storyEntrySum = getSumForStoryHourEntriesForIteration(iterationId);
        long backlogEntrySum = getSumForBacklogHourEntriesForIteration(iterationId);
        
        return tasksEntrySum + tasksWithStoryEntrySum + storyEntrySum + backlogEntrySum;
    }
    
    private long getSumForTaskHourEntriesWithoutStoryForIteration(int iterationId) {
        Criteria criteria = getCurrentSession().createCriteria(TaskHourEntry.class);
        
        criteria.setProjection(Projections.sum("minutesSpent"));

        criteria = criteria.createCriteria("task");
        criteria.add(Restrictions.isNull("story"));
        criteria = criteria.createCriteria("iteration");
        criteria.add(Restrictions.idEq(iterationId));
        
        Long result = (Long)uniqueResult(criteria);
        
        if (result == null) {
            return 0;
        }
        return result;
    }

    private long getSumForTaskHourEntriesWithStoryForIteration(int iterationId) {
        Criteria criteria = getCurrentSession().createCriteria(TaskHourEntry.class);
        
        criteria.setProjection(Projections.sum("minutesSpent"));

        criteria = criteria.createCriteria("task");
        criteria.add(Restrictions.isNotNull("story"));
        criteria = criteria.createCriteria("story");
        criteria = criteria.createCriteria("backlog");
        criteria.add(Restrictions.idEq(iterationId));
        
        Long result = (Long)uniqueResult(criteria);
        
        if (result == null) {
            return 0;
        }
        return result;
    }

    
    private long getSumForStoryHourEntriesForIteration(int iterationId) {
        Criteria criteria = getCurrentSession().createCriteria(StoryHourEntry.class);
        
        criteria.setProjection(Projections.sum("minutesSpent"));

        criteria = criteria.createCriteria("story");
        criteria = criteria.createCriteria("backlog");
        criteria.add(Restrictions.idEq(iterationId));
        
        Long result = (Long)uniqueResult(criteria);
        
        if (result == null) {
            return 0;
        }
        return result;
    }

    private long getSumForBacklogHourEntriesForIteration(int iterationId) {
        Criteria criteria = getCurrentSession().createCriteria(BacklogHourEntry.class);
        
        criteria.setProjection(Projections.sum("minutesSpent"));
        
        criteria = criteria.createCriteria("backlog");
        criteria.add(Restrictions.idEq(iterationId));
        
        Long result = (Long)uniqueResult(criteria);
        
        if (result == null) {
            return 0;
        }
        return result;
        
    }

    public long calculateSumByUserAndTimeInterval(User user,
            DateTime startDate, DateTime endDate) {
        if(user == null) {
            return 0L;
        }
        return this.calculateSumByUserAndTimeInterval(user.getId(), startDate, endDate);
    }

    public List<HourEntry> getHourEntriesByFilter(DateTime startTime,
            DateTime endTime, int userId) {
        Criteria crit = this.getCurrentSession().createCriteria(HourEntry.class);
        if(startTime != null) {
            crit.add(Restrictions.ge("date", startTime));
        }
        if(endTime != null) {
            crit.add(Restrictions.le("date", endTime));
        }
        if(userId != 0) {
            crit.createCriteria("user").add(Restrictions.idEq(userId));
        }
        return asList(crit); 
    }

    public List<HourEntry> getBacklogHourEntries(int backlogId, int limit) {
        Criteria crit = getCurrentSession().createCriteria(BacklogHourEntry.class);
        crit.add(Restrictions.eq("backlog.id", backlogId));
        crit.addOrder(Order.desc("date"));
        if (limit > 0) {
            crit.setMaxResults(limit);
        }
        return asList(crit);
    }
    public List<HourEntry> getTaskHourEntries(int taskId, int limit) {
        Criteria crit = getCurrentSession().createCriteria(TaskHourEntry.class);
        crit.add(Restrictions.eq("task.id", taskId));
        crit.addOrder(Order.desc("date"));
        if (limit > 0) {
            crit.setMaxResults(limit);
        }
        return asList(crit);
    }
    public List<HourEntry> getStoryHourEntries(int storyId, int limit) {
        Criteria crit = getCurrentSession().createCriteria(StoryHourEntry.class);
        crit.add(Restrictions.eq("story.id", storyId));
        crit.addOrder(Order.desc("date"));
        if (limit > 0) {
            crit.setMaxResults(limit);
        }
        return asList(crit);
    }

    public List<HourEntry> retrieveByUserAndInterval(User user,
            Interval interval) {
        Criteria crit = getCurrentSession().createCriteria(HourEntry.class);
        crit.add(Restrictions.eq("user", user));
        crit.add(Restrictions.between("date", interval.getStart(), interval.getEnd()));
      
        return asList(crit);
    }
}
