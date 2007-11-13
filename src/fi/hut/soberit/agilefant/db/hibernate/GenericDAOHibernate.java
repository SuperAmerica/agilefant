package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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
public abstract class GenericDAOHibernate<T> extends HibernateDaoSupport
		implements GenericDAO<T> {

	private Class clazz;

	protected GenericDAOHibernate(Class clazz) {
		this.clazz = clazz;
	}

	protected Class getPersistentClass() {
		return clazz;
	}

	/** {@inheritDoc} */
	public T get(int id) {
		return this.get(new Integer(id));
	}

	/** {@inheritDoc} */
	public Collection<T> getAll() {
		return super.getHibernateTemplate().loadAll(getPersistentClass());
	}

	/** {@inheritDoc} */
	public void refresh(T object) {
		super.getHibernateTemplate().refresh(object);
	}

	/** {@inheritDoc} */
	public void remove(int id) {
		this.remove(this.get(id));
	}

	/** {@inheritDoc} */
	public void remove(T object) {
		super.getHibernateTemplate().delete(object);
	}

	/** {@inheritDoc} */
	public void store(T object) {
		super.getHibernateTemplate().saveOrUpdate(object);
	}

	/** {@inheritDoc} */
	public Serializable create(T object) {
		return super.getHibernateTemplate().save(object);
	}

	/** {@inheritDoc} */
	public T get(Serializable id) {
		return (T) super.getHibernateTemplate().get(this.getPersistentClass(),
				id);
	}

	/** {@inheritDoc} */
	public void remove(Serializable id) {
		this.remove(this.get(id));
	}

	protected T getFirst(Collection<T> list) {
		if (list == null) {
			return null;
		} else {
			try {
				return list.iterator().next();
			} catch (NoSuchElementException e) {
				return null;
			}
		}
	}
}
