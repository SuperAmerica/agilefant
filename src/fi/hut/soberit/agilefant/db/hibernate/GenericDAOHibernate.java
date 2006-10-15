package fi.hut.soberit.agilefant.db.hibernate;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fi.hut.soberit.agilefant.db.GenericDAO;

public abstract class GenericDAOHibernate<T> extends HibernateDaoSupport implements GenericDAO<T>{
	
	private Class clazz;
	
	protected GenericDAOHibernate(Class clazz){
		this.clazz = clazz;
	}
	
	protected Class getPersistentClass(){
		return clazz;
	}

	public T get(int id) {
		return this.get(new Integer(id));		
	}

	public Collection<T> getAll() {
		return super.getHibernateTemplate().loadAll(getPersistentClass());
	}

	public void refresh(T object) {
		super.getHibernateTemplate().refresh(object);		
	}

	public void remove(int id) {
		this.remove(this.get(id));
	}

	public void remove(T object) {
		super.getHibernateTemplate().delete(object);
	}

	public void store(T object) {
		super.getHibernateTemplate().saveOrUpdate(object);
	}

	public T get(Serializable id) {
		return (T)super.getHibernateTemplate().get(this.getPersistentClass(), id);
	}
	
	public void remove(Serializable id) {
		this.remove(this.get(id));
	}
	
	protected T getFirst(Collection<T> list){
		if (list == null){
			return null;
		} else {
			return list.iterator().next();
		}
	}
}
