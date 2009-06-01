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
        Criteria crit = sess.createCriteria(Project.class);
        crit.add(Restrictions.idEq(project.getId()));
        crit = crit.createCriteria("assignments");
        crit = crit.createCriteria("user");
        return asCollection(crit);
    }
}
