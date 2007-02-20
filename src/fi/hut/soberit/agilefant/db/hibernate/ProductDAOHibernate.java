package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

/**
 * Hibernate implementation of ProductDAO interface using GenericDAOHibernate.
 */
public class ProductDAOHibernate extends GenericDAOHibernate<Product> implements ProductDAO {

	public ProductDAOHibernate(){
		super(Product.class);
	}
}
