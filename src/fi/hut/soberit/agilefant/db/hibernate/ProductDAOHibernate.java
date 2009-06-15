package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

/**
 * Hibernate implementation of ProductDAO interface using GenericDAOHibernate.
 */
@Repository("productDAO")
public class ProductDAOHibernate extends GenericDAOHibernate<Product> implements
        ProductDAO {

    public ProductDAOHibernate() {
        super(Product.class);
    }

    @SuppressWarnings("unchecked")
    public Collection<Product> getAllOrderByName() {
        DetachedCriteria crit = createCriteria().addOrder(Order.asc("name"));
        return hibernateTemplate.findByCriteria(crit);
    }

}
