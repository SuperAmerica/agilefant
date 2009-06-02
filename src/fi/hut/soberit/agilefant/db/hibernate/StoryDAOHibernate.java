package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

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
        return ((Integer) hibernateTemplate.findByCriteria(crit).get(0)).intValue();
    }
    
    @SuppressWarnings("unchecked")
    public List<Story> getStoriesByBacklog(Backlog backlog) {
        DetachedCriteria crit = DetachedCriteria.forClass(Story.class);
        crit.add(Restrictions.eq("backlog", backlog));
        return (List<Story>)hibernateTemplate.findByCriteria(crit);
    }
    
    public Collection<Task> getStoryTasks(Story story) {
        DetachedCriteria criteria = DetachedCriteria.forClass(Task.class);
        return null;
    }
}
