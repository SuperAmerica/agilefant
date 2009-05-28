package fi.hut.soberit.agilefant.business;

import java.util.Collection;

public interface GenericBusiness<T> {

    Collection<T> retrieveAll();

    T retrieve(int id);

    void store(T object);
    
    void delete(int id);

    int create(T object);

    int countAll();

    boolean exists(int id);

    T retrieveIfExists(int id);

}
