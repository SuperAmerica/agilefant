package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

@Service("assignmentBusiness")
@Transactional
public class AssignmentBusinessImpl extends GenericBusinessImpl<Assignment>
        implements AssignmentBusiness {

    @Autowired
    private AssignmentDAO assignmentDAO;

    @Autowired
    private UserBusiness userBusiness;

    public AssignmentBusinessImpl() {
        this.genericDAO = assignmentDAO;
    }
    
    public Assignment store(int assignmentId, ExactEstimate personalLoad,
            short availability) {
        Assignment persisted = this.retrieve(assignmentId);
        persisted.setPersonalLoad(personalLoad);
        persisted.setAvailability(availability);
        this.assignmentDAO.store(persisted);
        return persisted;
    }

    public Set<Integer> getAssignedUserIds(Backlog backlog) {
        Set<Integer> userIds = new HashSet<Integer>();
        Collection<Assignment> assignments = Collections.emptyList();
        if(backlog instanceof Iteration) {
            assignments = ((Iteration)backlog).getAssignments();
        } else if(backlog instanceof Project) {
            assignments = ((Project)backlog).getAssignments();
        }
        for(Assignment assignment : assignments) {
            userIds.add(assignment.getUser().getId());
        }
        return userIds;
    }
    
    public Collection<Assignment> addMultiple(Backlog backlog,
            Set<Integer> userIds, ExactEstimate personalLoad, short availability) {
        Collection<Assignment> assignments = new ArrayList<Assignment>();
        Set<Integer> assignedUserIds = this.getAssignedUserIds(backlog);
        for (int userId : userIds) {
            //only one assignment per user per backlog
            if(!assignedUserIds.contains(userId)) {
                User user = userBusiness.retrieve(userId);
                Assignment assignment = new Assignment(user, backlog);
                assignment.setAvailability(availability);
                assignment.setPersonalLoad(personalLoad);
                int assignmentId = (Integer) this.assignmentDAO.create(assignment);
                assignments.add(this.assignmentDAO.get(assignmentId));
            }
        }
        return assignments;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
        this.genericDAO = assignmentDAO;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
}
