package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;

@Service("projectTypeBusiness")
public class ProjectTypeBusinessImpl extends GenericBusinessImpl<ProjectType> implements
        ProjectTypeBusiness {

    private ProjectTypeDAO projectTypeDAO;

    @Autowired
    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.genericDAO = projectTypeDAO;
        this.projectTypeDAO = projectTypeDAO;
    }

}
