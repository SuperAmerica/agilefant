package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;

public interface AssignmentBusiness extends GenericBusiness<Assignment> {
    public void store(int assignmentId, ExactEstimate personalLoad, short availability, int userId);
}
