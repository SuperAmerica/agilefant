package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.ProjectType;

public interface ProjectTypeBusiness {

    Collection<ProjectType> getAll();

    ProjectType get(int id);

    void store(ProjectType projectType);

    int count();

}
