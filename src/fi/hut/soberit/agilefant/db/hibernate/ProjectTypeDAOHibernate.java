package fi.hut.soberit.agilefant.db.hibernate;

import org.apache.log4j.Logger;
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

    private final Logger log = Logger.getLogger(this.getClass());

    public ProjectTypeDAOHibernate() {
        super(ProjectType.class);
    }

    public int count() {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.setProjection(Projections.rowCount());
        int result =  ((Integer) super.getHibernateTemplate().findByCriteria(criteria)
                .get(0)).intValue();
        log.debug(result);
        return result;
    }

}
