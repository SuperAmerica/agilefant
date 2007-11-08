package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.db.hibernate.UserDAOHibernate;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for user business.
 * @author rjokelai
 * 
 */
public interface UserBusiness {
	
	/**
	 * Get backlog items for the user in progress.
	 * @param user user, whose backlog items are wanted.
	 * @return list of backlog items for user.
	 */
	public List<BacklogItem> getBacklogItemsInProgress(User user);
}
