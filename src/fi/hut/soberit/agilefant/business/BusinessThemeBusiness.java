package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
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
     * 
     * @param backlogId
     * @return
     */
    public Collection<BusinessTheme> getActiveBusinessThemes(int backlogId);

    /**
     * Gets the product's non-active themes.
     * 
     * @param backlogId
     * @return
     */
    public Collection<BusinessTheme> getNonActiveBusinessThemes(int backlogId);
    
    /**
     * Get global themes by activity. 
     * @param active
     * @return
     */
    public Collection<BusinessTheme> getSortedGlobalThemes(Boolean active);

    /**
     * Gets a backlog's items' themes.
     * 
     * @param backlogId
     * @return
     */
    public List<BusinessTheme> getBacklogItemActiveBusinessThemes(
            int backlogItemId);

    /**
     * Gets a product's themes' number of done blis.
     * 
     * @param productId
     * @return
     */
    public Map<BusinessTheme, BusinessThemeMetrics> getThemeMetrics(
            int productId);

    /**
     * Delete a business theme.
     * @param businessThemeId
     * @return true, if theme was global, false otherwise
     * @throws ObjectNotFoundException
     */
    public boolean delete(int businessThemeId) throws ObjectNotFoundException;

    /**
     * Store existing business theme or update existing entry.
     * 
     * @param businessThemeId
     *                Id for existing business theme entry or 0 if new entry
     *                should be created.
     * @param theme
     *                Business theme data.
     * @return Persisted business theme object.
     * @throws ObjectNotFoundException
     * @throws Exception
     */
    public BusinessTheme store(int businessThemeId, int productId,
            BusinessTheme theme) throws ObjectNotFoundException, Exception;

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

    @Deprecated
    public Map<Integer, List<BusinessTheme>> loadThemeCacheByBacklogId(
            int backlogId);

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
     * @param allocation
     *                Either percentage ("x.y%", "x%" or "x,y") or AFTime
     *                format.
     */
    public void addOrUpdateThemeToBacklog(int themeId, int backlogId,
            String allocation);

    /**
     * Add multiple theme bindings to a backlog
     * 
     * @see addOrUpdateThemeToBacklog(int themeId, int backlogId, String
     *      allocation)
     * @param themeIds
     * @param backlogId
     * @param allocations
     */
    public void multipleAddOrUpdateThemeToBacklog(int[] themeIds,
            int backlogId, String[] allocations);

    /**
     * Attach multiple themes to given backlog item. Will remove all existing
     * themes from the backlog item.
     * 
     * @param themeIds
     * @param bli
     */
    public void setBacklogItemThemes(Set<Integer> themeIds, BacklogItem bli);
    
    public void setBacklogItemThemes(Set<Integer> themeIds, int backlogItemId);
    /**
     * Remove given theme from the given backlog item.
     * 
     * @param backlog
     * @param businessTheme
     */
    public void removeThemeFromBacklog(Backlog backlog,
            BusinessTheme businessTheme);

    /**
     * Remove matching BacklogThemeBinding
     * 
     * @param backlogId
     * @param businessThemeId
     */
    public void removeThemeFromBacklog(int backlogId, int businessThemeId);

    /**
     * Remove given backlog theme binding.
     * 
     * @see removeThemeBinding(BacklogThemeBinding binding)
     * @param bindingId
     */
    public void removeThemeBinding(int bindingId);

    /**
     * Remove given backlog theme binding.
     * 
     * @see removeThemeBinding(int bindingId)
     * @param binding
     */
    public void removeThemeBinding(BacklogThemeBinding binding);

    /**
     * Get all backlog theme bindings for project's iterations.
     * 
     * @param project
     * @return
     */
    public List<BacklogThemeBinding> getIterationThemesByProject(Project project);

    /**
     * Get all backlog theme bindings for project's iterations.
     * 
     * @param projectId
     * @return
     */
    public List<BacklogThemeBinding> getIterationThemesByProject(int projectId);

    /**
     * Product themes to JSON
     * 
     * @param product
     * @param includeGlobal Include global themes in the result
     * @return
     */
    public String getThemesForProductAsJSON(Product product, boolean includeGlobal);

    /**
     * Product themes to JSON
     * 
     * @param productId
     * @param includeGlobal Include global themes in the result
     * @return
     */
    public String getThemesForProductAsJSON(int productId, boolean includeGlobal);
    
    /**
     * Product themes to JSON
     * 
     * @param backlog
     * @return
     */
    public String getActiveThemesForBacklogAsJSON(Backlog backlog);

    /**
     * Product themes to JSON
     * 
     * @param backlogId
     * @return
     */
    public String getActiveThemesForBacklogAsJSON(int backlogId);

    /**
     * Load theme metrics for given backlog. Loads all theme bound to given
     * backlog and calculates number of backlog items bound to that theme AND
     * the given backlog and computes the done percentage.
     * 
     * @param backlog
     */
    public void loadBacklogThemeMetrics(Backlog backlog);    
    public List<BusinessTheme> getBacklogItemActiveOrSelectedThemes(int backlogItemId);
    
    public Map<BacklogItem, List<BusinessTheme>> getBacklogItemBusinessThemesByBacklog(Backlog backlog);
}
