package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;

@Repository("projectTypeDAO")
public class ProjectTypeDAOHibernate extends GenericDAOHibernate<ProjectType>
        implements ProjectTypeDAO {

    public ProjectTypeDAOHibernate() {
        super(ProjectType.class);
    }

}
