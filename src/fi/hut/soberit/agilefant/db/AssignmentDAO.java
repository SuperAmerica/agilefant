package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.joda.time.Interval;

import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.User;

public interface AssignmentDAO extends GenericDAO<Assignment> {
    public List<Assignment> assigmentsInBacklogTimeframe(Interval interval, User user); 
}
