package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
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
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.model.User;

@Service("assignmentBusiness")
@Transactional
public class AssignmentBusinessImpl extends GenericBusinessImpl<Assignment>
        implements AssignmentBusiness {

    private AssignmentDAO assignmentDAO;
    private UserBusiness userBusiness;
    
    public AssignmentBusinessImpl() {
        super(Assignment.class);
    }
    
    public Assignment store(int assignmentId, SignedExactEstimate personalLoad,
            int availability) {
        Assignment persisted = this.retrieve(assignmentId);
        persisted.setPersonalLoad(personalLoad);
        persisted.setAvailability(availability);
        this.assignmentDAO.store(persisted);
        return persisted;
    }

    private Set<Assignment> getAssignemntsFromBacklog(Backlog backlog) {
        if(backlog instanceof Iteration) {
            return ((Iteration)backlog).getAssignments();
        } else if(backlog instanceof Project) {
            return ((Project)backlog).getAssignments();
        }
        return new HashSet<Assignment>();
    }
    public Set<Integer> getAssignedUserIds(Backlog backlog) {
        Set<Integer> userIds = new HashSet<Integer>();
        Collection<Assignment> assignments = this.getAssignemntsFromBacklog(backlog);
        for(Assignment assignment : assignments) {
            userIds.add(assignment.getUser().getId());
        }
        return userIds;
    }
    
    public Set<Assignment> addMultiple(Backlog backlog, Set<Integer> userIds) {
        return this.addMultiple(backlog, userIds, SignedExactEstimate.ZERO, 100);
    }
  
    public Set<Assignment> addMultiple(Backlog backlog,
            Set<Integer> userIds, SignedExactEstimate personalLoad, int availability) {
        Set<Assignment> assignments = this.getAssignemntsFromBacklog(backlog);
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

    @Autowired
    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
        this.genericDAO = assignmentDAO;
    }

    @Autowired
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
}
