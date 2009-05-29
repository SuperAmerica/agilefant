package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
@Service("backlogBusiness")
public class BacklogBusinessImpl extends GenericBusinessImpl<Backlog> implements
        BacklogBusiness {

    private BacklogDAO backlogDAO;

    @Autowired
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.genericDAO = backlogDAO;
        this.backlogDAO = backlogDAO;
    }

    @Transactional(readOnly = true)
    public Collection<Backlog> retrieveMultiple(Collection<Integer> idList) {
        ArrayList<Backlog> result = new ArrayList<Backlog>();
        for (Integer id : idList) {
            Backlog backlog = backlogDAO.get(id.intValue());
            if (backlog != null) {
                result.add(backlog);
            }
        }
        return result;        
    }

    /** {@inheritDoc} */    
    public Integer getNumberOfChildren(Backlog backlog) {
        return backlogDAO.getNumberOfChildren(backlog);
    }
}
