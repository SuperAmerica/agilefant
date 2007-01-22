package fi.hut.soberit.agilefant.db.hibernate;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;

import fi.hut.soberit.agilefant.db.UserDAO;
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
}