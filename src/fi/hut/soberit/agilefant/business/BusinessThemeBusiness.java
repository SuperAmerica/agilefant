package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
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
     * @param backlogId
     * @return
     */
    public Collection<BusinessTheme> getActiveBusinessThemes(int backlogId);

    /**
     * Gets the product's non-active themes.
     * @param backlogId
     * @return
     */
    public Collection<BusinessTheme> getNonActiveBusinessThemes(int backlogId);
    
    
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

    public Map<Integer, List<BusinessTheme>> loadThemeCacheByBacklogId(int backlogId);
    
    /**
     * Add or update binding between a backlog and business theme.
     * 
     * @param binding
     */
    public void addOrUpdateThemeToBacklog(BacklogThemeBinding binding);
    
    /**
     * Add or update binding between a backlog and business theme.
     * 
     * @param themeId
     * @param backlogId
     * @param allocation Either percentage ("x.y%", "x%" or "x,y") or AFTime format.
     */
    public void addOrUpdateThemeToBacklog(int themeId, int backlogId, String allocation);
    
    public void multipleAddOrUpdateThemeToBacklog(int[] themeIds, int backlogId, String[] allocations);
    
    public void addMultipleThemesToBacklogItem(int[] themeIds, int backlogItemId);
    
    public void removeThemeFromBacklog(Backlog backlog, BusinessTheme businessTheme);
    
    public void removeThemeFromBacklog(int backlogId, int businessThemeId);
    
    public void removeThemeBinding(int bindingId);
    
    public void removeThemeBinding(BacklogThemeBinding binding);
    
    public List<BacklogThemeBinding> getIterationThemesByProject(Project project);
    public List<BacklogThemeBinding> getIterationThemesByProject(int projectId);
    
    public String getThemesForProductAsJSON(Product product);
    public String getThemesForProductAsJSON(int productId);
    
    public void loadBacklogThemeMetrics(Backlog backlog);
}
