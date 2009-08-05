package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.model.Assignment;

@Component("assignmentAction")
@Scope("prototype")
public class AssignmentAction extends ActionSupport implements Prefetching {

    private static final long serialVersionUID = 484323193355552426L;
    private Assignment assignment = new Assignment();
    @PrefetchId
    private int assignmentId = 0;

    @Autowired
    private AssignmentBusiness assignmentBusiness;

    public String modify() {
        this.assignment = this.assignmentBusiness.store(assignmentId,
                assignment.getPersonalLoad(), assignment.getAvailability());
        return Action.SUCCESS;
    }

    public String delete() {
        this.assignmentBusiness.delete(assignmentId);
        return Action.SUCCESS;
    }

    public void initializePrefetchedData(int objectId) {
        this.assignmentBusiness.retrieve(objectId);

    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public void setAssignmentBusiness(AssignmentBusiness assignmentBusiness) {
        this.assignmentBusiness = assignmentBusiness;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }
}
