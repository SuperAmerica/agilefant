package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

public class UserDAOHibernate extends GenericDAOHibernate<User> implements UserDAO{
	
	public UserDAOHibernate(){
		super(User.class);
	}
}