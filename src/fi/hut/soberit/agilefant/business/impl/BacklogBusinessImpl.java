package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryUserComparator;

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
    private ProjectDAO projectDAO;
    private IterationDAO iterationDAO;

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
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    @Autowired
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
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
        }
        else {
            childBacklogs.addAll(backlog.getChildren());
        }
        
        return childBacklogs;
    }
    
    public Map<Story, List<ResponsibleContainer>> getResponsiblesByBacklog(
            Backlog backlog) {
        // TODO: TEST THIS
        // TODO: OPTIMIZE THIS
        if(backlog != null) {
            Collection<User> responsibles = null;
            Map<Story, List<ResponsibleContainer>> result = new HashMap<Story, List<ResponsibleContainer>>();
            
            List<Object[]> data = backlogDAO.getResponsiblesByBacklog(backlog);
            for(Object[] row : data) {
                Story item = (Story)row[0];
                User user = (User)row[1];
                if(user == null) {
                    continue;
                }
                boolean inProject = false;
                if(result.get(item) == null) {
                    result.put(item, new ArrayList<ResponsibleContainer>());
                }
                if(responsibles == null || responsibles.contains(user)) {
                    inProject = true;
                }
                result.get(item).add(new ResponsibleContainer(user,inProject));
            }
            //order users
            for(Story story : result.keySet()) {
               Collections.sort(result.get(story), new StoryUserComparator());
            }
            return result;
         }
         return null;
    }
}
