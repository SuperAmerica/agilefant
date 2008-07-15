package fi.hut.soberit.agilefant.business.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;

public class BusinessThemeBusinessImpl implements BusinessThemeBusiness {

    private BusinessThemeDAO businessThemeDAO;
    private ProductDAO productDAO;
    private BacklogItemDAO backlogItemDAO;
    private BacklogDAO backlogDAO;

    public BusinessTheme getBusinessTheme(int businessThemeId) {
        return businessThemeDAO.get(businessThemeId);
    }

    public Collection<BusinessTheme> getAll() {
        return businessThemeDAO.getAll();
    }
    
    public Collection<BusinessTheme> getActiveBusinessThemes(int productId) {
        Product product = productDAO.get(productId);
        if(product == null) {
            return new HashSet<BusinessTheme>();
        }
        return businessThemeDAO.getSortedBusinessThemesByProductAndActivity(product, true);
    }
    
    public Collection<BusinessTheme> getNonActiveBusinessThemes(int productId) {
        Product product = productDAO.get(productId);
        if(product == null) {
            return new HashSet<BusinessTheme>();
        }
        return businessThemeDAO.getSortedBusinessThemesByProductAndActivity(product, false);
    }
    
    public Map<BacklogItem, List<BusinessTheme>> getBusinessThemeListByBacklogItems(int backlogId) {
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null) {
            return new HashMap<BacklogItem, List<BusinessTheme>>();
        }
        Map<BacklogItem, List<BusinessTheme>> map = new HashMap<BacklogItem, List<BusinessTheme>>();
        for (BacklogItem bli: backlog.getBacklogItems()) {
            if (bli.getBusinessThemes() != null) {
                map.put(bli, getBacklogItemActiveBusinessThemes(bli.getId()));
            }           
        }
        return map;
    }
    
    public List<BusinessTheme> getBacklogItemActiveBusinessThemes(int backlogItemId) {
        BacklogItem bli = backlogItemDAO.get(backlogItemId);
        if (bli == null) {
            return new ArrayList<BusinessTheme>();
        }
        List<BusinessTheme> activeThemes = new ArrayList<BusinessTheme>();
        for (BusinessTheme t: bli.getBusinessThemes()) {
            if (t.isActive()) {
                activeThemes.add(t);
            }
        }
                        
        return activeThemes;
    }

    public void delete(int themeId) throws ObjectNotFoundException {

        BusinessTheme businessTheme = businessThemeDAO.get(themeId);

        if (businessTheme == null) {
            throw new ObjectNotFoundException();
        }
        try {
            Collection<BacklogItem> associations = businessTheme.getBacklogItems();
            for(BacklogItem bli : associations) {
                bli.getBusinessThemes().remove(businessTheme);
            }
            businessTheme.getProduct().getBusinessThemes().remove(businessTheme);
            businessThemeDAO.remove(themeId);
        } catch (Exception e) { }
    }

    public void setBusinessThemeDAO(BusinessThemeDAO businessThemeDAO) {
        this.businessThemeDAO = businessThemeDAO;
    }

    /**
     * {@inheritDoc}
     */
    public BusinessTheme store(int businessThemeId, int productId, BusinessTheme theme)
            throws ObjectNotFoundException, DataIntegrityViolationException, Exception {
        BusinessTheme persistable = null;
        Product product = null;

        if (businessThemeId > 0 && productId > 0) {
            persistable = businessThemeDAO.get(businessThemeId);
            product = productDAO.get(productId);
            
            if (persistable == null) {
                throw new ObjectNotFoundException(
                        "Selected theme was not found.");
            }
            if (product == null) {
                throw new ObjectNotFoundException(
                    "Product was not found.");
            }
            persistable.setDescription(theme.getDescription());
            persistable.setName(theme.getName());
            persistable.setProduct(product);
            persistable.setActive(theme.isActive());
        } else if (productId > 0) {
            product = productDAO.get(productId);
            if (product == null) {
                throw new ObjectNotFoundException(
                    "Product was not found.");
            }
            theme.setProduct(product);
            persistable = theme;
        }
        try {
            if (persistable.getId() > 0) {
                businessThemeDAO.store(persistable);
            } else {
                int newId = (Integer) businessThemeDAO.create(persistable);
                persistable = businessThemeDAO.get(newId);
            }
        } catch (DataIntegrityViolationException dve) {
            throw new DataIntegrityViolationException("businessTheme.duplicateName");            
        } catch (Exception e) {
            throw new Exception();
        }
        
        return persistable;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

}
