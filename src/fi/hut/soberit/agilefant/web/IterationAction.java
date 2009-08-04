package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;

@Component("iterationAction")
@Scope("prototype")
public class IterationAction implements
        CRUDAction {

    private static final long serialVersionUID = -448825368336871703L;

    private int iterationId;

    private Iteration iteration;

    private Project project;

    private int projectId;

    private IterationMetrics iterationMetrics;

    @Autowired
    private IterationBusiness iterationBusiness;

    public String create() {
        iterationId = 0;
        iteration = new Iteration();
        return Action.SUCCESS;
    }

    public String retrieve() {
        iteration = iterationBusiness.retrieve(iterationId);
        project = (Project) iteration.getParent();
        projectId = project.getId();
        // Load metrics data
        iterationMetrics = iterationBusiness.getIterationMetrics(iteration);
        return Action.SUCCESS;
    }

    public String delete() {
        iteration = iterationBusiness.retrieve(iterationId);
        iterationBusiness.delete(iterationId);
        return Action.SUCCESS;
    }

    public String iterationMetrics() {
        iteration = iterationBusiness.retrieve(iterationId);
        iterationMetrics = iterationBusiness.getIterationMetrics(iteration);
        return Action.SUCCESS;
    }

    public String store() {
        return "";
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }

    public IterationMetrics getIterationMetrics() {
        return iterationMetrics;
    }

}