package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Product;

/**
 * Interface for a DAO of a Product.
 * 
 * @see GenericDAO
 */
public interface ProductDAO extends GenericDAO<Product> {

    Collection<Product> getAllOrderByName();
    
    public List<Product> retrieveBacklogTree();

}
