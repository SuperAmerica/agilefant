package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;

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
    
    public List<Product> retrieveBacklogTree() {
        Criteria crit = this.getCurrentSession().createCriteria(this.getPersistentClass());
        crit.createAlias("children", "projects", CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("projects.children", "iterations", CriteriaSpecification.LEFT_JOIN);
        crit.addOrder(Order.asc("name"));
        crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return asList(crit);
    }

    public List<Story> retrieveLeafStories(Product product) {
        int productId = product.getId();
        
        Criteria leaftStoryCrit = getCurrentSession().createCriteria(Story.class);
        leaftStoryCrit.createAlias(
                "backlog.parent", "secondParent", CriteriaSpecification.LEFT_JOIN)
                .createAlias("secondParent.parent", "thirdParent",
                        CriteriaSpecification.LEFT_JOIN);
        leaftStoryCrit.add(Restrictions.or(Restrictions.or(Restrictions.eq(
                "backlog.id", productId), Restrictions.eq("secondParent.id",
                productId)), Restrictions.eq("thirdParent.id", productId)));
        
        leaftStoryCrit.add(Restrictions.isEmpty("children"));
        return asList(leaftStoryCrit);
    }

}
