package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.dao.DataIntegrityViolationException;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public class BusinessThemeBusinessImpl implements BusinessThemeBusiness {

    private BusinessThemeDAO businessThemeDAO;
    

    public BusinessTheme getBusinessTheme(int businessThemeId) {
        return businessThemeDAO.get(businessThemeId);
    }

    public Collection<BusinessTheme> getAll() {
        return businessThemeDAO.getAll();
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
            businessThemeDAO.remove(themeId);
        } catch (Exception e) { }
    }

    public void setBusinessThemeDAO(BusinessThemeDAO businessThemeDAO) {
        this.businessThemeDAO = businessThemeDAO;
    }

    /**
     * {@inheritDoc}
     */
    public BusinessTheme store(int businessThemeId, BusinessTheme theme)
            throws ObjectNotFoundException, Exception {
        BusinessTheme persistable = null;

        if (businessThemeId > 0) {
            persistable = businessThemeDAO.get(businessThemeId);
            if (persistable == null) {
                throw new ObjectNotFoundException(
                        "Selected business theme waas not found.");
            }
            persistable.setDescription(theme.getDescription());
            persistable.setName(theme.getName());
        } else {
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
            throw new Exception("businessTheme.duplicateName");
        }
        return persistable;
    }

}
