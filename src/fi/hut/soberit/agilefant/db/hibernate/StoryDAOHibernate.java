package fi.hut.soberit.agilefant.db.hibernate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.StoryMetrics;

@Repository("storyDAO")
public class StoryDAOHibernate extends GenericDAOHibernate<Story> implements
        StoryDAO {

    public StoryDAOHibernate() {
        super(Story.class);
    }

    public int countByCreator(User user) {
        DetachedCriteria crit = createCriteria().add(
                Restrictions.eq("creator", user)).setProjection(
                Projections.rowCount());
        return ((Integer) hibernateTemplate.findByCriteria(crit).get(0))
                .intValue();
    }

    @SuppressWarnings("unchecked")
    public List<Story> getStoriesByBacklog(Backlog backlog) {
        DetachedCriteria crit = DetachedCriteria.forClass(Story.class);
        crit.add(Restrictions.eq("backlog", backlog));
        return (List<Story>) hibernateTemplate.findByCriteria(crit);
    }

    @SuppressWarnings("unchecked")
    private StoryMetrics calculateMetrics(int id, boolean withoutStory) {
        StoryMetrics metrics = new StoryMetrics();
        Criteria criteria = getCurrentSession().createCriteria(Task.class);
        criteria.setProjection(
                Projections.projectionList()
                .add(Projections.rowCount(), "taskCount")
                .add(Projections.sum("originalEstimate"), "originalEstimateSum")
                .add(Projections.sum("effortLeft"), "effortLeftSum")
                .add(Projections.groupProperty("state"), "state")
                );
        if (withoutStory) {
            criteria.add(Restrictions.eq("iteration.id", id));
            criteria.add(Restrictions.isNull("story"));
        } else {
            criteria.add(Restrictions.eq("story.id", id));            
        }
        List<Object[]> resultsByState = criteria.list();
        int allTasksCount = 0;
        long allTasksOriginalEstimateSum = 0;
        long allTasksEffortLeftSum = 0;
        if (resultsByState != null) {
            for (Object[] results : resultsByState) {
                int count = ((Integer)results[0]).intValue();
                ExactEstimate originalEstimateSum = (ExactEstimate) results[1];
                ExactEstimate effortLeftSum = (ExactEstimate) results[2];
                TaskState state = (TaskState) results[3];
                if (state == TaskState.DONE) {
                    metrics.setDoneTasks(count);
                }
                if (originalEstimateSum != null) {
                    allTasksOriginalEstimateSum += originalEstimateSum.getMinorUnits();
                }
                if (effortLeftSum != null) {
                    allTasksEffortLeftSum += effortLeftSum.getMinorUnits();
                }
                allTasksCount += count;
            }
        }
        metrics.setTotalTasks(allTasksCount);
        metrics.setOriginalEstimate(allTasksOriginalEstimateSum);
        metrics.setEffortLeft(allTasksEffortLeftSum);
        return metrics;        
    }
    
    public int getStoryPointSumByBacklog(int backlogId) {
        Criteria criteria = getCurrentSession().createCriteria(Story.class);
        criteria.add(Restrictions.eq("backlog.id", backlogId));
        criteria.add(Restrictions.isNotNull("storyPoints"));
        criteria.setProjection(Projections.sum("storyPoints"));
        Object result = criteria.uniqueResult();
        if (result == null) {
            return 0;
        }
        return Integer.parseInt(result.toString());
    }
    
    public StoryMetrics calculateMetrics(int storyId) {
        return calculateMetrics(storyId, false);
    }
    
    public StoryMetrics calculateMetricsWithoutStory(int iterationId) {
        return calculateMetrics(iterationId, true);
    }

    public Map<Integer, Integer> getNumOfResponsiblesByStory(
            Set<Integer> storyIds) {
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.add(Restrictions.in("id", storyIds));
        crit.createAlias("responsibles", "responsible");
        ProjectionList sums = Projections.projectionList();
        sums.add(Projections.groupProperty("id"));
        sums.add(Projections.count("responsible.id"));
        
        crit.setProjection(sums);
        List<Object[]> rawData = asList(crit);
        
        Map<Integer, Integer> result = new HashMap<Integer, Integer>();
        for(Object[] row : rawData) {
            result.put((Integer)row[0], (Integer)row[1]);
        }
        return result;
    }

}
