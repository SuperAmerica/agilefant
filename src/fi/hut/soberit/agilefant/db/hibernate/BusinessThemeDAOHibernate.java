package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BusinessThemeDAO;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;

public class BusinessThemeDAOHibernate extends GenericDAOHibernate<BusinessTheme> implements BusinessThemeDAO {

    public BusinessThemeDAOHibernate() {
        super(BusinessTheme.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<BusinessTheme> getSortedBusinessThemesByProductAndActivity(Product product, boolean active) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("product", product));
        criteria.add(Restrictions.eq("active", active));
        criteria.addOrder(Order.asc("name"));

        return (List<BusinessTheme>)super.getHibernateTemplate()
                            .findByCriteria(criteria);
        
    }
    
}
