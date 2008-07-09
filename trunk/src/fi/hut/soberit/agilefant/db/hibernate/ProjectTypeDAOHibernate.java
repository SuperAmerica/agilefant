package fi.hut.soberit.agilefant.db.hibernate;

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
}
