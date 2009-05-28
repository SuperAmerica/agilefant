package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate implementation of UserDAO interface using GenericDAOHibernate.
 */
@Repository("userDAO")
public class UserDAOHibernate extends GenericDAOHibernate<User> implements
        UserDAO {

    public UserDAOHibernate() {
        super(User.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public User getByLoginName(String loginName) {
        DetachedCriteria crit = createCriteria().add(
                Restrictions.eq("loginName", loginName));
        return getFirst(hibernateTemplate.findByCriteria(crit));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<User> listUsersByEnabledStatus(boolean enabled) {
        DetachedCriteria criteria = createCriteria().add(
                Expression.eq("enabled", enabled));
        return hibernateTemplate.findByCriteria(criteria);
    }

}
