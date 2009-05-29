package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import flexjson.JSONSerializer;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
@Service("backlogBusiness")
public class BacklogBusinessImpl extends GenericBusinessImpl<Backlog> implements
        BacklogBusiness {

    private BacklogDAO backlogDAO;
    private ProductDAO productDAO;

    @Autowired
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.genericDAO = backlogDAO;
        this.backlogDAO = backlogDAO;
    }
    
    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
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
    
    /** {@inheritDoc} */
    public String getBacklogAsJSON(Backlog backlog) {
        if (backlog == null) {
            return "{}";
        }
        return new JSONSerializer().serialize(backlog);
    }
    
    /** {@inheritDoc} */
    public String getBacklogAsJSON(int backlogId) {
        Backlog backlog = this.retrieveIfExists(backlogId);
        return getBacklogAsJSON(backlog);
    }
    
    /** {@inheritDoc} */
    public String getAllProductsAsJSON() {
        return new JSONSerializer().serialize(productDAO.getAll());
    }

}
