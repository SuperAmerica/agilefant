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
    Collection<T> getAll();

    /**
     * Get data model object of this type by id.
     * 
     * @param id
     *            requested id
     * @return object with given id, or null if not found
     */
    T get(int id);

    /**
     * Get data model object of this type by id.
     * 
     * @param id
     *            requested id
     * @return object with given id, or null if not found
     */
    T get(Serializable id);

    /**
     * Removes the object of this type with given id.
     * 
     * @param id
     *            requested id
     */
    void remove(int id);

    /**
     * Removes the object of this type with given id.
     * 
     * @param id
     *            requested id
     */
    void remove(Serializable id);

    /**
     * Removes given object.
     * 
     * @param object
     *            object instance to remove
     */
    void remove(T object);

    /**
     * Persists given object. An ID is given, if the object doesn't already
     * exist in the database.
     * 
     * @param object
     *            object instance to store
     */
    void store(T object);

    /**
     * Creates and persists a new object
     * 
     * @param object
     *            object instance to store
     * @return generated ID
     */
    Serializable create(T object);

    /**
     * Refereshed an data model object from the database.
     * 
     * @param object
     *            object instance to refreshed
     */
    void refresh(T object);

    /**
     * Retrieves the amount of data model objects of this type in the database
     * 
     * @return amount of objects
     */
    int count();

    boolean exists(int id);

}
