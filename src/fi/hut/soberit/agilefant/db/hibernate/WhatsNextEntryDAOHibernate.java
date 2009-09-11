package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;

@Repository("whatsNextEntryDAO")
public class WhatsNextEntryDAOHibernate extends GenericDAOHibernate<WhatsNextEntry> implements
WhatsNextEntryDAO {

    public WhatsNextEntryDAOHibernate() {
        super(WhatsNextEntryDAO.class);
    }

    private Criteria filterDoneTasks(Criteria crit) {
        Criteria returned = crit.createCriteria("task");
        returned.add(Restrictions.ne("state", TaskState.DONE));
        return returned;
    }

    public WhatsNextEntry getLastTaskInRank(User user) {
        Criteria entry = getCurrentSession().createCriteria(WhatsNextEntry.class);

        entry.add(Restrictions.eq("user", user));
        entry.addOrder(Order.desc("rank"));
        entry.setFetchMode("user", FetchMode.SELECT);
        entry.setFetchMode("task", FetchMode.JOIN);
        entry.setMaxResults(1);
        filterDoneTasks(entry);

        return uniqueResult(entry);
    }

    public Collection<WhatsNextEntry> getTasksWithRankBetween(int lower, int upper, User user) {
        Criteria entry = getCurrentSession().createCriteria(WhatsNextEntry.class);
        entry.add(Restrictions.eq("user", user));
        entry.add(Restrictions.between("rank", lower, upper));
        entry.setFetchMode("user", FetchMode.SELECT);
        entry.setFetchMode("task", FetchMode.JOIN);
        return asList(entry);
    }

    public WhatsNextEntry getWhatsNextEntryFor(User user, Task task) {
        Criteria crit = getCurrentSession().createCriteria(WhatsNextEntry.class);
        crit.add(Restrictions.eq("user", user));
        crit.add(Restrictions.eq("task", task));

        // Filter out tasks that are done!
        filterDoneTasks(crit);
        crit.setMaxResults(1);
        return uniqueResult(crit);
    }

    public Collection<WhatsNextEntry> getWhatsNextEntriesFor(User user) {
        Criteria crit = getCurrentSession().createCriteria(WhatsNextEntry.class);
        crit.add(Restrictions.eq("user", user));
        crit.setFetchMode("task", FetchMode.JOIN);

        filterDoneTasks(crit);

        crit.addOrder(Order.asc("rank"));
        return asList(crit);
    }
    
    public Collection<WhatsNextEntry> getWhatsNextEntriesForIteration(int iterationId) {
        String hqlQuery = 
            "SELECT entry FROM WhatsNextEntry AS entry " +
                "INNER JOIN FETCH entry.user " +
                "WHERE (entry.task.iteration IS NULL OR entry.task.iteration.id = :iterationId)" +
                "OR (entry.task.story.iteration IS NULL OR entry.task.story.iteration = :iterationId";
        
        // Query q = getCurrentSession().createQuery(hqlQuery);
        return null;
    }
}
