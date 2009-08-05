package fi.hut.soberit.agilefant.business.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;

@Service("assignmentBusiness")
@Transactional
public class AssignmentBusinessImpl extends GenericBusinessImpl<Assignment> implements AssignmentBusiness {

    public void store(int assignmentId, ExactEstimate personalLoad,
            short availability, int userId) {
        // TODO Auto-generated method stub
        
    }

}
