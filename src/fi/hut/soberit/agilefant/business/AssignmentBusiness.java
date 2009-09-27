package fi.hut.soberit.agilefant.business;

import java.util.Set;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;

public interface AssignmentBusiness extends GenericBusiness<Assignment> {
    public Assignment store(int assignmentId, SignedExactEstimate personalLoad,
            int availability);

    public Set<Assignment> addMultiple(Backlog backlog,
            Set<Integer> userIds, SignedExactEstimate personalLoad, int availability);
}
