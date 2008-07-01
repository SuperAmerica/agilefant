package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BusinessTheme;

public interface BusinessThemeBusiness {

    /**
     * Get theme by id.
     * 
     * @param userId
     *                id number of the theme
     * @return the theme with id themeId
     */
    public BusinessTheme getBusinessTheme(int businessThemeId);
    
    public Collection<BusinessTheme> getAll();
    
    public void delete(int businessThemeId) throws ObjectNotFoundException;
    
}
