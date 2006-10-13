package fi.hut.soberit.agilefant.db;

import java.io.Serializable;
import java.util.Collection;

public interface GenericDAO<T> {
	
	public Collection<T> getAll();
	public T get(int id);
	public T get(Serializable id);
	public void remove(int id);
	public void remove(Serializable id);
	public void remove(T object);
	public void store(T object);
	public void refresh(T object);
}
