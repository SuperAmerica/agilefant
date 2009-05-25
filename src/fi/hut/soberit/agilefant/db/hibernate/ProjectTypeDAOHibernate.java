package fi.hut.soberit.agilefant.db.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;

import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;

/**
 * Hibernate implementation of ProjectTypeDAO interface using
 * GenericDAOHibernate.
 */
public class ProjectTypeDAOHibernate extends GenericDAOHibernate<ProjectType>
        implements ProjectTypeDAO {

    public ProjectTypeDAOHibernate() {
        super(ProjectType.class);
    }

    public int count() {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.setProjection(Projections.rowCount());
        return ((Integer) super.getHibernateTemplate().findByCriteria(criteria)
                .get(0)).intValue();
    }

}
