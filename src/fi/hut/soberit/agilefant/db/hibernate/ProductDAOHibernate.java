package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

public class ProductDAOHibernate extends GenericDAOHibernate<Product> implements ProductDAO {

	public ProductDAOHibernate(){
		super(Product.class);
	}
}
