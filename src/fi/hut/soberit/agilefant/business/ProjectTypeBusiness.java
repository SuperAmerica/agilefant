package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.ProjectType;

public interface ProjectTypeBusiness {

    public Collection<ProjectType> getAll();

    public ProjectType get(int id);

    public void store(ProjectType projectType);

}
