package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate implementation of ProjectDAO interface using
 * GenericDAOHibernate.
 */
@Repository("projectDAO")
public class ProjectDAOHibernate extends GenericDAOHibernate<Project> implements
        ProjectDAO {

    public ProjectDAOHibernate() {
        super(Project.class);
    }
    
    public Collection<User> getAssignedUsers(Project project) {
        Session sess =  sessionFactory.getCurrentSession();
        Criteria crit = sess.createCriteria(User.class);
        crit = crit.createCriteria("assignments");
        crit = crit.createCriteria("backlog");
        crit.add(Restrictions.idEq(project.getId()));
        return asCollection(crit);
    }
    
    public Collection<Project> getProjectsWithUserAssigned(User user) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.idEq(user.getId()));
        criteria = criteria.createCriteria("assignments");
        criteria = criteria.createCriteria("backlog");
        return asCollection(criteria);
    }

    public List<Project> getActiveProjectsSortedByRank() {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.gt("endDate", new DateTime()));
        crit.addOrder(Order.desc("rank"));
        return asList(crit);
    }
    
    public Collection<Project> getProjectsWithRankBetween(int lower, int upper) {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.between("rank", lower, upper));
        return asCollection(crit);
    }
    
    public Collection<Project> getUnrankedProjects(LocalDate startDate, LocalDate endDate) {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.ge("endDate", startDate.toDateTimeAtStartOfDay()));
        crit.add(Restrictions.le("startDate", endDate.toDateTimeAtStartOfDay()));
        crit.add(Restrictions.lt("rank", 1));
        return asCollection(crit);
    }
           
    public List<Project> getRankedProjects(LocalDate startDate, LocalDate endDate) {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.ge("endDate", startDate.toDateTimeAtStartOfDay()));
        crit.add(Restrictions.le("startDate", endDate.toDateTimeAtStartOfDay()));
        crit.add(Restrictions.gt("rank", 0));
        crit.addOrder(Order.asc("rank"));
        return asList(crit);
    }
    
    public Project getMaxRankedProject() {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.gt("rank", 0));
        crit.addOrder(Order.desc("rank"));
        crit.setMaxResults(1);
        return uniqueResult(crit);
    }

    public Project getProjectWithRankLessThan(int rank) {
        Criteria crit = getCurrentSession().createCriteria(Project.class);
        crit.add(Restrictions.lt("rank", rank));
        crit.add(Restrictions.gt("rank", 0));
        crit.addOrder(Order.asc("rank"));
        crit.setMaxResults(1);
        return uniqueResult(crit);
    }
    
    public void increaseRankedProjectRanks() {
        Query query = getCurrentSession().createQuery("UPDATE Project project SET project.rank = project.rank + 1 WHERE project.rank > 0");
        query.executeUpdate();
    }
    
}
