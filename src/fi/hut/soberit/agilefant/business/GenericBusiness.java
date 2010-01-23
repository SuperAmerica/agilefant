package fi.hut.soberit.agilefant.business;

import java.util.Collection;

public interface GenericBusiness<T> {

    Collection<T> retrieveAll();

    /**
     * Returns the object with the given id.
     * <p>
     * Throws <code>ObjectNotFoundException</code> if not found.
     */
    T retrieve(int id);
    
    public T retrieveDetached(int id);

    void store(T object);
    
    void delete(int id);
    
    void delete(T object);

    int create(T object);

    int countAll();

    boolean exists(int id);

    /**
     * Returns the object with the given id.
     * <p>
     * Will return <code>null</code> if not found.
     */
    T retrieveIfExists(int id);

}
