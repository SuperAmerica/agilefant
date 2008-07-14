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
    
    /**
     * Gets the product's active themes.
     * @param productId
     * @return
     */
    public Collection<BusinessTheme> getActiveBusinessThemes(int productId);

    public void delete(int businessThemeId) throws ObjectNotFoundException;

    /**
     * Store existing business theme or update existing entry.
     * 
     * @param businessThemeId Id for existing business theme entry or 0 if new entry should be created.
     * @param theme Business theme data.
     * @return Persisted business theme object.
     * @throws ObjectNotFoundException 
     * @throws Exception
     */
    public BusinessTheme store(int businessThemeId, int productId, BusinessTheme theme)
            throws ObjectNotFoundException, Exception;

}
