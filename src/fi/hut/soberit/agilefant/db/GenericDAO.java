package fi.hut.soberit.agilefant.db;

import java.io.Serializable;
import java.util.Collection;

/**
 * Generic interface for a DAO of some type. Defines minimal functionality for a
 * DAO.
 * <p>
 * Actual DAO interfaces implement this interface, possibly adding some new
 * functionality.
 * 
 * @param <T>
 *            type of the entity bean / data model object the DAO is for
 * @see fi.hut.soberit.agilefant.db.hibernate.GenericDAOHibernate
 */
public interface GenericDAO<T> {

	/**
	 * Get all objects of this type.
	 * 
	 * @return collection of all objects of this type
	 */
	public Collection<T> getAll();

	/**
	 * Get data model object of this type by id.
	 * 
	 * @param id
	 *            requested id
	 * @return object with given id, or null if not found
	 */
	public T get(int id);

	/**
	 * Get data model object of this type by id.
	 * 
	 * @param id
	 *            requested id
	 * @return object with given id, or null if not found
	 */
	public T get(Serializable id);

	/**
	 * Removes the object of this type with given id.
	 * 
	 * @param id
	 *            requested id
	 */
	public void remove(int id);

	/**
	 * Removes the object of this type with given id.
	 * 
	 * @param id
	 *            requested id
	 */
	public void remove(Serializable id);

	/**
	 * Removes given object.
	 * 
	 * @param object
	 *            object instance to remove
	 */
	public void remove(T object);

	/**
	 * Persists given object. An ID is given, if the object doesn't already
	 * exist in the database.
	 * 
	 * @param object
	 *            object instance to store
	 */
	public void store(T object);

	/**
	 * Creates and persists a new object
	 * 
	 * @param object
	 *            object instance to store
	 * @return generated ID
	 */
	public Serializable create(T object);

	/**
	 * Refereshed an data model object from the database.
	 * 
	 * @param object
	 *            object instance to refreshed
	 */
	public void refresh(T object);
}
