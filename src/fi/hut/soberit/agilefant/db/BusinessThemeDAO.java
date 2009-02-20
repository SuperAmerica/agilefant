package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;

/**
 * Interface for a DAO of a Theme.
 * 
 * @see GenericDAO
 */
public interface BusinessThemeDAO extends GenericDAO<BusinessTheme> {
    
    /**
     * Get filtered and sorted list of business themes
     * 
     * Filters business themes and returns list of themes sorted by theme name
     * in ascending order.
     *   
     * @param product
     * @param active
     * @return
     */
    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(Product product, boolean active);

    /**
     * 
     * @param product
     * @param active
     * @param includeGlobal
     * @return
     */
    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(Product product, Boolean active, boolean includeGlobal);
    
    public List<BusinessTheme> getSortedGlobalThemes(Boolean active);
    
    public Map<Integer, Integer> numberOfBacklogItemsByProduct(Product product, State backlogItemState);
    
    @Deprecated
    public List<?> getThemesByBacklog(Backlog backlog); 
    
    public void saveOrUpdateBacklogThemeBinding(BacklogThemeBinding binding);
    
    public void removeBacklogThemeBinding(BacklogThemeBinding binding);
    
    public List<BacklogThemeBinding> getIterationThemesByProject(Project project);
    
    public BacklogThemeBinding getBindingById(int bindingId);
    
    public Map<BacklogItem, List<BusinessTheme>> getBacklogItemBusinessThemesByBacklog(
            Backlog backlog);
    
}
