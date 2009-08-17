package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import flexjson.JSON;

public class ProjectLoadContainer extends BacklogLoadContainer {
    private Project project;

    @JSON(include=false)
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public Backlog getBacklog() {
        return this.project;
    }

    @Override
    public long getTotalLoad() {
        return this.getTotalBaselineLoad() + this.getTotalFutureLoad();
    }
    
}
