package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;

@Repository("storyRankDAO")
public class StoryRankDAOHibernate extends GenericDAOHibernate<StoryRank>
        implements StoryRankDAO {

    protected StoryRankDAOHibernate() {
        super(StoryRank.class);
    }

    public StoryRank retrieveHeadByBacklog(Backlog backlog) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                StoryRank.class);
        crit.add(Restrictions.eq("backlog", backlog));
        crit.addOrder(Order.asc("previous"));
        List<StoryRank> res = asList(crit);
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public StoryRank retrieveByBacklogAndStory(Backlog backlog, Story story) {
        Criteria crit = sessionFactory.getCurrentSession().createCriteria(
                StoryRank.class);
        crit.add(Restrictions.eq("backlog", backlog));
        crit.add(Restrictions.eq("story", story));
        return uniqueResult(crit);
    }

}
