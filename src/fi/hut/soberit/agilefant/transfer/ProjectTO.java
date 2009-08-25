package fi.hut.soberit.agilefant.transfer;

import java.util.Collection;
import java.util.HashSet;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class ProjectTO extends Project {

    private Collection<Iteration> ongoingIterations = new HashSet<Iteration>();
    private Collection<Iteration> pastIterations = new HashSet<Iteration>();
    private Collection<Iteration> futureIterations = new HashSet<Iteration>();
    
    public ProjectTO(Project project) {
        BeanCopier.copy(project, this);
//        this.setChildren(project.getChildren());
    }

    public void setOngoingIterations(Collection<Iteration> ongoingIterations) {
        this.ongoingIterations = ongoingIterations;
    }

    @JSON
    public Collection<Iteration> getOngoingIterations() {
        return ongoingIterations;
    }

    public void setPastIterations(Collection<Iteration> pastIterations) {
        this.pastIterations = pastIterations;
    }

    @JSON
    public Collection<Iteration> getPastIterations() {
        return pastIterations;
    }

    public void setFutureIterations(Collection<Iteration> futureIterations) {
        this.futureIterations = futureIterations;
    }

    @JSON
    public Collection<Iteration> getFutureIterations() {
        return futureIterations;
    }
    
}
