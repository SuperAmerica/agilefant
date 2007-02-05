package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO{
	
	public UserDAOHibernate(){
		super(User.class);
	}
	
	public User getUser(String loginName){
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getPersistentClass());
		criteria.add(Expression.eq("loginName", loginName));
		return super.getFirst(super.getHibernateTemplate().findByCriteria(criteria));
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Task> getUnfinishedTasks(User user) {
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				"from Task t where t.assignee.id = :id and t.status != 4",
				"id", 
				new Integer(user.getId()));
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Task> getUnfinishedWatchedTasks(User user) {
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				"from Task t where :id in indices(t.watchers) and t.status != 4",
				"id", 
				new Integer(user.getId()));
	}
}