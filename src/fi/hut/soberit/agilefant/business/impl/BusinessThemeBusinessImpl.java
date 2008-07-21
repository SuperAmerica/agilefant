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
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.util.BusinessThemeMetrics;

public class BusinessThemeBusinessImpl implements BusinessThemeBusiness {

    private BusinessThemeDAO businessThemeDAO;
    private ProductDAO productDAO;
    private BacklogDAO backlogDAO;
    private BacklogItemDAO backlogItemDAO;

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
    
    public Map<Integer, List<BusinessTheme>> loadThemesByBacklog(int backlogId) {
        List rawThemeData = businessThemeDAO.getThemesByBacklog(backlogDAO
                .get(backlogId));
        Map<Integer, List<BusinessTheme>> res = new HashMap<Integer, List<BusinessTheme>>();
        for (Object row : rawThemeData) {
            try {
                Object tmpData[] = (Object[]) row;
                int bliId = (Integer) tmpData[0];
                BusinessTheme tmpTheme = new BusinessTheme();
                tmpTheme.setId((Integer) tmpData[1]);
                tmpTheme.setName((String) tmpData[2]);
                tmpTheme.setDescription((String) tmpData[3]);
                if (res.get(bliId) == null) {
                    res.put(bliId, new ArrayList<BusinessTheme>());
                }
                res.get(bliId).add(tmpTheme);
            } catch (Exception e) {
            }
        }
        return res;
    }
    public Map<BusinessTheme, BusinessThemeMetrics> getThemeMetrics(int productId) {
        Product product = productDAO.get(productId);
        
        if (product == null) {
            return new HashMap<BusinessTheme, BusinessThemeMetrics>();
        }
        Map<Integer, Integer> allItems = businessThemeDAO.numberOfBacklogItemsByProduct(product, null);
        Map<Integer, Integer> doneItems = businessThemeDAO.numberOfBacklogItemsByProduct(product, State.DONE);
        
        Map<BusinessTheme, BusinessThemeMetrics> metricsMap = new HashMap<BusinessTheme, BusinessThemeMetrics>();

        for (BusinessTheme theme: product.getBusinessThemes()) {
            
            BusinessThemeMetrics metrics = new BusinessThemeMetrics();
            int donePercentage = 0;
            
            /*
            metrics.setNumberOfBlis(theme.getBacklogItems().size());            
            int doneBlis = 0;
            int donePercentage = 0;
            for (BacklogItem bli: theme.getBacklogItems()) {
                if (bli.getState() == State.DONE) {
                    doneBlis++;
                }
            }
            metrics.setNumberOfDoneBlis(doneBlis);
            */
            metrics.setNumberOfBlis(((allItems.get(theme.getId()) != null ) ? allItems.get(theme.getId()) : 0));
            metrics.setNumberOfDoneBlis(((doneItems.get(theme.getId()) != null ) ? doneItems.get(theme.getId()) : 0));

            if (metrics.getNumberOfBlis() > 0) {
                donePercentage = (int) ((float) metrics.getNumberOfDoneBlis() / (float) metrics.getNumberOfBlis() * 100.0);
            }
            metrics.setDonePercentage(donePercentage);
            metricsMap.put(theme, metrics);
        }
        return metricsMap;
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
    
    public void activateBusinessTheme(int businessThemeId)
            throws ObjectNotFoundException {
        BusinessTheme theme = getBusinessTheme(businessThemeId);
        if (theme == null) {
            throw new ObjectNotFoundException();
        }
        activateBusinessTheme(theme);
    }

    public void activateBusinessTheme(BusinessTheme businessTheme) {
        if (businessTheme == null) {
            return;
        }
        businessTheme.setActive(true);
        businessThemeDAO.store(businessTheme);
    }

    public void deactivateBusinessTheme(int businessThemeId)
            throws ObjectNotFoundException {
        BusinessTheme theme = getBusinessTheme(businessThemeId);
        if (theme == null) {
            throw new ObjectNotFoundException();
        }
        deactivateBusinessTheme(theme);
    }

    public void deactivateBusinessTheme(BusinessTheme businessTheme) {
        if (businessTheme == null) {
            return;
        }
        businessTheme.setActive(false);
        businessThemeDAO.store(businessTheme);
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

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

}
