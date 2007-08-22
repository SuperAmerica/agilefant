package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Product;

/**
 * Interface for a DAO of a Product.
 * 
 * @see GenericDAO
 */
public interface ProductDAO extends GenericDAO<Product> {
	
	/**
	 * Get all products ordered by name in descending order
	 * @return all products ordered by name in descending order
	 */
	public List<Product> getAllOrderByName();
}
