package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;

/**
 * Interface for a DAO of a Product.
 * 
 * @see GenericDAO
 */
public interface ProductDAO extends GenericDAO<Product> {

    Collection<Product> getAllOrderByName();
    
    public List<Product> retrieveBacklogTree();

    public List<Story> retrieveLeafStories(Product product);
}
