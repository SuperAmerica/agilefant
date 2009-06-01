package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.annotations.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Assignment;
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
    
    @SuppressWarnings("unchecked")
    public Collection<User> getAssignedUsers(Project project) {
        Session sess =  sessionFactory.getCurrentSession();
        Criteria crit = sess.createCriteria(Assignment.class);
        crit.add(Restrictions.eq("project",project));
        crit.createCriteria("users");
        return crit.list();
    }
}
