package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.apache.log4j.Logger;

import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;

public class ProjectTypeBusinessImpl implements ProjectTypeBusiness {

    protected final Logger log = Logger.getLogger(this.getClass());

    private ProjectTypeDAO projectTypeDAO;

    public Collection<ProjectType> getAll() {
        return projectTypeDAO.getAll();
    }

    public ProjectType get(int id) {
        return projectTypeDAO.get(id);
    }

    public void store(ProjectType projectType) {
        projectTypeDAO.store(projectType);
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }

}
