package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
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
        crit = crit.createCriteria("project");
        crit.add(Restrictions.idEq(project.getId()));
        return asCollection(crit);
    }
    
    public Collection<Project> getProjectsWithUserAssigned(User user) {
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.idEq(user.getId()));
        criteria = criteria.createCriteria("assignments");
        criteria = criteria.createCriteria("project");
        return asCollection(criteria);
    }
}
