package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.GenericBusiness;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;

public abstract class GenericBusinessImpl<T> implements GenericBusiness<T> {

    protected GenericDAO<T> genericDAO;

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
            throw new ObjectNotFoundException(this.getClass().getName());
        }
        return object;
    }

    @Transactional(readOnly = true)
    public Collection<T> retrieveAll() {
        return genericDAO.getAll();
    }

    @Transactional(readOnly = true)
    public T retrieveIfExists(int id) {
        return genericDAO.get(id);
    }

    @Transactional
    public void store(T object) {
        genericDAO.store(object);
    }

}
