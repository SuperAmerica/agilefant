package fi.hut.soberit.agilefant.web;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.AssignmentTO;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.IterationRowMetrics;

@Component("iterationAction")
@Scope("prototype")
public class IterationAction implements
        CRUDAction, Prefetching {

    private static final long serialVersionUID = -448825368336871703L;

    @PrefetchId
    private int iterationId;

    private Iteration iteration;

    private Backlog parentBacklog;

    private int parentBacklogId;

    private IterationMetrics iterationMetrics;
    
    private IterationRowMetrics iterationRowMetrics;
    
    private Set<AssignmentTO> assignments;
    
    private Set<Integer> assigneeIds = new HashSet<Integer>();

    @Autowired
    private IterationBusiness iterationBusiness;

    public String create() {
        iterationId = 0;
        iteration = new Iteration();
        iteration.setStartDate(new DateTime());
        iteration.setEndDate(new DateTime());
        return Action.SUCCESS;
    }

    public String retrieve() {
        iteration = iterationBusiness.retrieve(iterationId);
        parentBacklog = iteration.getParent();
        // Load metrics data
        iterationMetrics = iterationBusiness.getIterationMetrics(iteration);
        return Action.SUCCESS;
    }
    
    public String fetchIterationData() {
        iterationBusiness.retrieve(iterationId);
        iteration = iterationBusiness.getIterationContents(iterationId);
        return Action.SUCCESS;
    }

    public String delete() {
        iteration = iterationBusiness.retrieve(iterationId);
        iterationBusiness.delete(iterationId);
        return Action.SUCCESS;
    }

    public String iterationRowMetrics() {
        iterationRowMetrics = iterationBusiness.getIterationRowMetrics(iterationId);
        return Action.SUCCESS;
    }
    
    public String iterationAssignments() {
        iteration = iterationBusiness.retrieve(iterationId);
        assignments = iterationBusiness.calculateAssignedLoadPerAssignee(iteration);
        return Action.SUCCESS;
    }
    
    public String iterationMetrics() {
        iteration = iterationBusiness.retrieve(iterationId);
        iterationMetrics = iterationBusiness.getIterationMetrics(iteration);
        return Action.SUCCESS;
    }
    /*
    @Validations(
            requiredFields = {@RequiredFieldValidator(type=ValidatorType.SIMPLE, fieldName="iteration.name", key="iteration.missingName"),
                    @RequiredFieldValidator(type=ValidatorType.SIMPLE, fieldName="iteration.startDate", key="iteration.missingStartDate"),
                    @RequiredFieldValidator(type=ValidatorType.SIMPLE, fieldName="iteration.endDate", key="iteration.missingEndDate")},
            expressions = {@ExpressionValidator(expression = "iteration.startDate < iteration.endDate", key="iteration.startBeforeEnd")}
    )
    */
    public String store() {
        Set<Integer> assignees = null;
        if(!this.assigneeIds.isEmpty()) {
            assignees = this.assigneeIds;
        }
        iteration = this.iterationBusiness.store(iterationId, parentBacklogId, iteration, assignees);
        return Action.SUCCESS;
    }
    
    public void initializePrefetchedData(int objectId) {
        this.iteration = this.iterationBusiness.retrieve(objectId);
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

    public int getParentBacklogId() {
        return parentBacklogId;
    }

    public void setParentBacklogId(int parentBacklogId) {
        this.parentBacklogId = parentBacklogId;
    }

    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }

    public IterationMetrics getIterationMetrics() {
        return iterationMetrics;
    }
    
    public IterationRowMetrics getIterationRowMetrics() {
        return iterationRowMetrics;
    }

    public Backlog getParentBacklog() {
        return parentBacklog;
    }

    public Set<AssignmentTO> getAssignments() {
        return assignments;
    }

    public Set<Integer> getAssigneeIds() {
        return assigneeIds;
    }

    public void setAssigneeIds(Set<Integer> assigneeIds) {
        this.assigneeIds = assigneeIds;
    }
}