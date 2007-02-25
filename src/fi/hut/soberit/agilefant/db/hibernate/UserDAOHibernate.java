package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

/**
 * Hibernate implementation of UserDAO interface using GenericDAOHibernate.
 */
public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO {
	
	public UserDAOHibernate(){
		super(User.class);
	}
	
	/** {@inheritDoc} */
	public User getUser(String loginName){
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getPersistentClass());
		criteria.add(Expression.eq("loginName", loginName));
		return super.getFirst(super.getHibernateTemplate().findByCriteria(criteria));
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getUnfinishedTasks(User user) {
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				"from Task t where t.assignee.id = :id and t.status != 4",
				"id", 
				new Integer(user.getId()));
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getUnfinishedWatchedTasks(User user) {
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				"from Task t where :id in indices(t.watchers) and t.status != 4",
				"id", 
				new Integer(user.getId()));
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<Task> getUnfinishedTasksByTime(User user, Date start, Date end) {
		
		final String names[] = {"id", "start", "end"};
		Object values[] = {user.getId(), start, end};
		
		return (Collection<Task>)super.getHibernateTemplate().findByNamedParam(
				
				"select distinct t from Task t, Deliverable d, Iteration i where "+ 
				"t.assignee.id = :id and t.status != 4 and " +
				"((t.backlogItem.backlog.id in "+
				"	(select dd.id from Deliverable dd) "+ 
				"and d.id = t.backlogItem.backlog.id and d.startDate <= :end and d.endDate >= :start ) "+
				"or "+
				"(t.backlogItem.backlog.id in "+
				"	(select ii.id from Iteration ii) "+ 
				"and i.id = t.backlogItem.backlog.id and i.startDate <= :end and i.endDate >= :start ))",

				names, 
				values);		
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	public Collection<BacklogItem> getBacklogItemsByTime(User user, Date start, Date end) {

		final String names[] = {"id", "start", "end"};
		Object values[] = {user.getId(), start, end};
		
		return (Collection<BacklogItem>)super.getHibernateTemplate().findByNamedParam(
				
				"select distinct bli from BacklogItem bli, Deliverable d, Iteration i where "+ 
				"bli.assignee.id = :id and " +
				"((bli.backlog.id in "+
				"	(select dd.id from Deliverable dd) "+ 
				"and d.id = bli.backlog.id and d.startDate <= :end and d.endDate >= :start ) "+
				"or "+
				"(bli.backlog.id in "+
				"	(select ii.id from Iteration ii) "+
				"and i.id = bli.backlog.id and i.startDate <= :end and i.endDate >= :start ))",

				names, 
				values);	
		
	}
}