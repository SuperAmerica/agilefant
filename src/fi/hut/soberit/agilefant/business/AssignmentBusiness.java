package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;

public interface AssignmentBusiness extends GenericBusiness<Assignment> {
    public Assignment store(int assignmentId, ExactEstimate personalLoad,
            int availability);

    public Collection<Assignment> addMultiple(Backlog backlog,
            Set<Integer> userIds, ExactEstimate personalLoad, int availability);
}
