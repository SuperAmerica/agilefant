package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.model.ActivityType;

/**
 * Hibernate implementation of ActivityTypeDAO interface using GenericDAOHibernate.
 */
public class ActivityTypeDAOHibernate extends GenericDAOHibernate<ActivityType> implements ActivityTypeDAO {

	public ActivityTypeDAOHibernate(){
		super(ActivityType.class);
	}
}
