package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;

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
}
