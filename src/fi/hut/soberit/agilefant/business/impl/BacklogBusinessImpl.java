package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
@Service("backlogBusiness")
@Transactional
public class BacklogBusinessImpl extends GenericBusinessImpl<Backlog> implements
        BacklogBusiness {

    private BacklogDAO backlogDAO;
    private ProductDAO productDAO;
    private AssignmentBusiness assignmentBusiness;
    @Autowired
    private StoryDAO storyDAO;

    public BacklogBusinessImpl() {
        super(Backlog.class);
    }
    
    @Autowired
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.genericDAO = backlogDAO;
        this.backlogDAO = backlogDAO;
    }

    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Autowired
    public void setAssignmentBusiness(AssignmentBusiness assignmentBusiness) {
        this.assignmentBusiness = assignmentBusiness;
    }

    @Transactional(readOnly = true)
    public Collection<Backlog> retrieveMultiple(Collection<Integer> idList) {
        return backlogDAO.retrieveMultiple(idList);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public int getNumberOfChildren(Backlog backlog) {
        return backlogDAO.getNumberOfChildren(backlog);
    }

    @Transactional(readOnly = true)
    public Collection<Backlog> getChildBacklogs(Backlog backlog) {
        Collection<Backlog> childBacklogs = new ArrayList<Backlog>();
        if (backlog == null) {
            childBacklogs.addAll(productDAO.getAll());
        } else {
            childBacklogs.addAll(backlog.getChildren());
        }

        return childBacklogs;
    }


    @Transactional(readOnly = true)
    public int calculateStoryPointSum(int backlogId) {
        return backlogDAO.calculateStoryPointSum(backlogId);
    }

    public int calculateDoneStoryPointSum(int backlogId) {
        return backlogDAO.calculateDoneStoryPointSum(backlogId);
    }
    
    public void addAssignees(int backlogId, Set<Integer> userIds) {
        Backlog backlog = this.retrieve(backlogId);
        this.assignmentBusiness.addMultiple(backlog, userIds,
                SignedExactEstimate.ZERO, 100);
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Product getParentProduct(Backlog backlog) {
        Backlog parent = backlog;
        while (!(parent instanceof Product)) {
            parent = parent.getParent();
        }
        return (Product)parent;
    }
    
    @Transactional(readOnly = true)
    public int getStoryPointSumByBacklog(Backlog backlog) {
        return storyDAO.getStoryPointSumByBacklog(backlog.getId());
    }
}
