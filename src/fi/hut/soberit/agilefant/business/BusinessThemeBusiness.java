package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.util.BusinessThemeMetrics;

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

    /**
     * Gets the product's non-active themes.
     * @param productId
     * @return
     */
    public Collection<BusinessTheme> getNonActiveBusinessThemes(int productId);
    
    
    /**
     * Gets a backlog's items' themes.
     * @param backlogId
     * @return
     */
    public List<BusinessTheme> getBacklogItemActiveBusinessThemes(int backlogItemId);
    
    /**
     * Gets a product's themes' number of done blis.
     * @param productId
     * @return
     */
    public Map<BusinessTheme, BusinessThemeMetrics> getThemeMetrics(int productId);
    
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
    
    /**
     * Set business theme active.
     * 
     * @see BusinessThemeBusiness.deactivateBusinessTheme(BusinessTheme
     *      businessTheme)
     * @param businessThemeId
     */
    public void deactivateBusinessTheme(int businessThemeId)
            throws ObjectNotFoundException;;

    /**
     * Set business theme active.
     * 
     * @param businessTheme
     */
    public void deactivateBusinessTheme(BusinessTheme businessTheme);

    /**
     * Set business theme active.
     * 
     * @see BusinessThemeBusiness.activateBusinessTheme(BusinessTheme
     *      businessTheme)
     * @param businessThemeId
     */
    public void activateBusinessTheme(int businessThemeId)
            throws ObjectNotFoundException;;

    /**
     * Set business theme active.
     * 
     * @param businessTheme
     */
    public void activateBusinessTheme(BusinessTheme businessTheme);

}
