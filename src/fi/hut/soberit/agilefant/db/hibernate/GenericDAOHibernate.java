package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;

import fi.hut.soberit.agilefant.db.GenericDAO;

/**
 * Generically implements basic DAO functionality specified by GenericDAO.
 * <p>
 * All the concrete DAOs under this same package inherit from this class. They
 * also implement the corresponding DAO interface, "delegating" method
 * implementations to this class.
 * 
 * @param <T>
 *            type of the entity bean / data model object the DAO is for
 * @see fi.hut.soberit.agilefant.db.GenericDAO
 */
public abstract class GenericDAOHibernate<T> implements GenericDAO<T> {

    private Class<?> clazz;

    protected SessionFactory sessionFactory;

    protected HibernateTemplate hibernateTemplate;

    @PostConstruct
    public void init() {
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory cannot be null");
        }
    }

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    protected GenericDAOHibernate(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected Class<?> getPersistentClass() {
        return clazz;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public T get(int id) {
        return (T) hibernateTemplate.get(this.getPersistentClass(), id);
    }

    /** {@inheritDoc} */
    public T getAndDetach(int id) {
        T object = (T) this.get(id);
        this.sessionFactory.getCurrentSession().evict(object);
        return object;
    }
    
    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public Collection<T> getAll() {
        return hibernateTemplate.loadAll(getPersistentClass());
    }
    
    /** {@inheritDoc} */
    public Collection<T> getMultiple(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashSet<T>();
        }
        Criteria c = getCurrentSession().createCriteria(getPersistentClass());
        c.add(Restrictions.in("id", ids));
        return asCollection(c);
    }

    /** {@inheritDoc} */
    public void remove(int id) {
        this.remove(this.get(id));
    }

    /** {@inheritDoc} */
    public void remove(T object) {
        hibernateTemplate.delete(object);
    }

    /** {@inheritDoc} */
    public void store(T object) {
        hibernateTemplate.saveOrUpdate(object);
    }

    /** {@inheritDoc} */
    public Serializable create(T object) {
        return hibernateTemplate.save(object);
    }

    protected T getFirst(Collection<T> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.iterator().next();
    }

    protected <ResultType> ResultType getFirstTypeSafe(
            Collection<ResultType> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.iterator().next();
    }

    protected DetachedCriteria createCriteria() {
        return DetachedCriteria.forClass(this.getPersistentClass());
    }

    public int count() {
        DetachedCriteria criteria = createCriteria().setProjection(
                Projections.rowCount());
        return ((Long) hibernateTemplate.findByCriteria(criteria).get(0))
                .intValue();
    }

    public boolean exists(int id) {
        DetachedCriteria crit = createCriteria().add(Restrictions.idEq(id))
                .setProjection(Projections.rowCount());
        return ((Long) hibernateTemplate.findByCriteria(crit).get(0))
                .intValue() > 0;
    }

    public Session getCurrentSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    protected <ResultType> Collection<ResultType> asCollection(Criteria criteria) {
        Collection<ResultType> list = criteria.list();
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    protected <ResultType> List<ResultType> asList(Criteria criteria) {
        List<ResultType> list = criteria.list();
        if (list == null) {
            return Collections.EMPTY_LIST;
        }
        return list;
    }

    protected <ResultType> ResultType firstResult(Criteria criteria) {
        List<ResultType> list = asList(criteria);
        return getFirstTypeSafe(list);
    }

    @SuppressWarnings("unchecked")
    protected <ResultType> ResultType uniqueResult(Criteria criteria) {
        return (ResultType) criteria.uniqueResult();
    }

}
