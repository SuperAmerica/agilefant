package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

/**
 * Hibernate implementation of ProductDAO interface using GenericDAOHibernate.
 */
public class ProductDAOHibernate extends GenericDAOHibernate<Product> implements
        ProductDAO {

    public ProductDAOHibernate() {
        super(Product.class);
    }

    @SuppressWarnings("unchecked")
    public List<Product> getAllOrderByName() {
        final String query = "from Product p order by p.name asc";
        return (List<Product>) super.getHibernateTemplate().find(query);
    }

}
