package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.GenericBusiness;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;

public abstract class GenericBusinessImpl<T> implements GenericBusiness<T> {

    protected GenericDAO<T> genericDAO;

    protected Class<T> modelType;
    
    public GenericBusinessImpl(Class<T> modelType) {
        this.modelType = modelType;
    }
    
    @Transactional(readOnly = true)
    public int countAll() {
        return genericDAO.count();
    }

    @Transactional
    public int create(T object) {
        return ((Integer) genericDAO.create(object)).intValue();
    }

    @Transactional
    public void delete(T object) {
        genericDAO.remove(object);
    }

    @Transactional
    public void delete(int id) {
        genericDAO.remove(id);
    }

    @Transactional(readOnly = true)
    public boolean exists(int id) {
        return genericDAO.exists(id);
    }

    @Transactional(readOnly = true)
    public T retrieve(int id) {
        T object = genericDAO.get(id);
        if (object == null) {
           throw new ObjectNotFoundException("Object with id " + id +" was not found", this.modelType);
        }
        return object;
    }

    @Transactional(readOnly = true)
    public Collection<T> retrieveAll() {
        return genericDAO.getAll();
    }
    
    @Transactional(readOnly = true)
    public Collection<T> retrieveMultiple(Collection<Integer> ids) {
        return genericDAO.getMultiple(ids);
    }

    @Transactional(readOnly = true)
    public T retrieveIfExists(int id) {
        return genericDAO.get(id);
    }
    
    @Transactional(readOnly = true)
    public T retrieveDetached(int id) {
        T object = genericDAO.getAndDetach(id);
        if (object == null) {
            throw new ObjectNotFoundException("Object with id " + id +" was not found", this.modelType);
        }
        return object;
    }

    @Transactional
    public void store(T object) {
        genericDAO.store(object);
    }

}
