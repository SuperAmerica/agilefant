package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate implementation of UserDAO interface using GenericDAOHibernate.
 */
public class UserDAOHibernate extends GenericDAOHibernate<User> implements
        UserDAO {

    public UserDAOHibernate() {
        super(User.class);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("all")
    public User getUser(String loginName) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Expression.eq("loginName", loginName));
        return super.getFirst(super.getHibernateTemplate().findByCriteria(
                criteria));
    }

    /** {@inheritDoc} */
    @SuppressWarnings("all")
    public List<User> getEnabledUsers() {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Expression.eq("enabled", true));
        return super.getHibernateTemplate().findByCriteria(
                criteria);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("all")
    public List<User> getDisabledUsers() {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Expression.eq("enabled", false));
        return super.getHibernateTemplate().findByCriteria(
                criteria);
    }
}