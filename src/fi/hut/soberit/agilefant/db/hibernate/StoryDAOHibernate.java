package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.StoryMetrics;

@Repository("storyDAO")
public class StoryDAOHibernate extends GenericDAOHibernate<Story> implements
        StoryDAO {

    public StoryDAOHibernate() {
        super(Story.class);
    }

    public StoryMetrics calculateMetrics(int storyId) {
        StoryMetrics metrics = new StoryMetrics();
        Criteria criteria = getCurrentSession().createCriteria(Task.class);
        criteria.setProjection(
                Projections.projectionList()
                .add(Projections.sum("originalEstimate"), "originalEstimateSum")
                .add(Projections.sum("effortLeft"), "effortLeftSum")
                );
        criteria.add(Restrictions.eq("story.id", storyId));            
        Object[] result = (Object[])criteria.uniqueResult();
        if (result[0] != null) {
            metrics.setOriginalEstimate((Long)result[0]);
        }
        if (result[1] != null) {
            metrics.setEffortLeft((Long)result[1]);
        }
        return metrics;        
    }
    
    public int getStoryPointSumByBacklog(int backlogId) {
        Criteria criteria = getCurrentSession().createCriteria(Story.class);
        criteria.add(Restrictions.eq("backlog.id", backlogId));
        criteria.add(Restrictions.isNotNull("storyPoints"));
        criteria.add(Restrictions.not(Restrictions.eq("state", StoryState.DEFERRED)));
        criteria.setProjection(Projections.sum("storyPoints"));
        Object result = criteria.uniqueResult();
        if (result == null) {
            return 0;
        }
        return Integer.parseInt(result.toString());
    }

    public Map<Integer, Integer> getNumOfResponsiblesByStory(
            Set<Integer> storyIds) {
        if(storyIds == null || storyIds.size() == 0) {
            return Collections.emptyMap();
        }
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
            result.put((Integer)row[0], ((Long)row[1]).intValue());
        }
        return result;
    }


    
    public Collection<Story> getAllIterationStoriesByResponsibleAndInterval(User user, Interval interval) {
        ArrayList<Story> stories = new ArrayList<Story>();
        
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.createCriteria("responsibles")
            .add(Restrictions.idEq(user.getId()));
        
        Criteria iteration = crit.createCriteria("backlog");
        IterationDAOHelpers.addIterationIntervalLimit(iteration, interval);
        crit.add(Restrictions.ne("state", StoryState.DONE));

        List<Story> dummy = asList(crit); 
        stories.addAll(dummy);
        
        return stories;
    }
    
    // :)
    private static final String QUERY_RETRIEVE_ACTIVE_ITERATION_STORIES_WITH_USER_RESPONSIBLE =
        "SELECT story FROM Story story" + " LEFT JOIN story.responsibles AS responsible"
        + " LEFT JOIN story.backlog.parent AS project"
        + " WHERE responsible.id = :userId"
        + " AND story.backlog.endDate > :now"
        + " AND story.backlog.class = :backlogType";
    
    @SuppressWarnings("unchecked")
    public List<Story> retrieveActiveIterationStoriesWithUserResponsible(int userId) {
        Query query = getCurrentSession().createQuery(QUERY_RETRIEVE_ACTIVE_ITERATION_STORIES_WITH_USER_RESPONSIBLE);
        query.setParameter("userId", userId);
        query.setParameter("now", new DateTime());
        query.setParameter("backlogType", "Iteration");
        return query.list();
    }
    
    public List<Story> searchByName(String name) {
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
        crit.addOrder(Order.asc("name"));
        crit.setMaxResults(SearchBusiness.MAX_RESULTS_PER_TYPE);
        return asList(crit);
    }

}
