package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.User;

/**
 * This description contains generic information on <code>Business</code>
 * classes rather than on <code>BacklogBusiness</code> in particular.
 * 
 * The <code>Business</code> objects ideally contain all transactional logic
 * of the application. Any method calls made inside <code>Business</code>
 * objects are intercepted and made transactional. The transactions propagate
 * such that the same transaction is still used if a <code>Business</code>
 * object calls the methods of another <code>Business</code> object.
 * 
 * Note that unlike DAOs, the Business objects need NOT have a 1:1 relationship
 * with underlying model. One should add <code>Business</code> classes for
 * different <em>aspects</em> of the program, not for different classes.
 * Hence, <code>BacklogBusiness</code> can naturally handle actions targeted
 * at both <code>BacklogItem</code> and <code>Backlog</code>. How and when
 * new <code>Business</code> objects are created is usually be the
 * responsibility of the project architect.
 * 
 * Also note that Business objects automatically commit transactions and that
 * when a transaction is committed, Hibernate will automatically cause all
 * peristent objects that have changed to be updated in the database.
 * 
 * @author Teemu Ilmonen
 * 
 */
public interface BacklogBusiness {

    /**
     * Delete multiple <code>BacklogItems</code> at once.
     * 
     * @param backlogItemIds
     *                <code>Array</code> containing the IDs of the items to
     *                delete.
     */
    public void deleteMultipleItems(int backlogId, int backlogItemIds[])
            throws ObjectNotFoundException;

    /**
     * Creates new backlogItem to given backlog.
     * 
     * @param backlogId
     *                the ID of backlog where the new item should be created
     * @return created backlog item
     */

    public BacklogItem createBacklogItemToBacklog(int backlogId);

    /**
     * Change the priority of multiple <code>BacklogItem</code>s at once.
     * 
     * @param backlogItemIds
     *                <code>Array</code> containing the IDs of the items to
     *                prioritize
     * @param priority
     *                the new priority to set
     */
    public void changePriorityOfMultipleItems(int backlogItemIds[],
            Priority priority) throws ObjectNotFoundException;

    /**
     * Moves multiple backlog items to target backlog. Object with one of the
     * backlog item ids is not found or target backlog with the id is not found,
     * ObjectNotFoundException is thrown.
     * 
     * @param backlogItemIds
     *                <code>Array</code> of backlog item ids for items to move
     * @param targetBacklogId
     *                id of the target backlog
     */

    public void moveMultipleBacklogItemsToBacklog(int backlogItemIds[],
            int targetBacklogId) throws ObjectNotFoundException;

    /**
     * Calculates the sum of effort lefts of the given backlog items
     * 
     * @param bliList
     *                List of backlog items
     * @return sum of effort lefts
     */
    public AFTime getEffortLeftSum(List<BacklogItem> bliList);
    
    /**
     * Calculates the sum of original estimates of the given backlog items
     * 
     * @param bliList List of backlog items
     * @return sum of original estimates
     */
    public AFTime getOriginalEstimateSum(Collection<BacklogItem> bliList);
    
    /**
     * Assigns the selected users to the given backlog.
     * 
     * @param selectedUserIds Ids of the users to be assigned.
     * @param backlog The backlog the users are assigned to.
     */
    public void setAssignments(int[] selectedUserIds, Backlog backlog);
    
    /**
     * Returns users either are or are not assigned to the given backlog.
     * 
     * @param backlog Backlog, where users are/are not assigned.
     * @param areAssigned Whether assign/non-assign status is wanted.
     * @return Users who fulfill the requirement.
     */
    public Collection<User> getUsers(Backlog backlog, boolean areAssigned);
    
    
    /**
     * Returns the number of users assigned to the backlog.
     * 
     * @param backlog Backlog, where users are assigned.
     * @return Number of users.
     */
    public int getNumberOfAssignedUsers(Backlog backlog);
    
    /**
     * Removes all given user's assignments from the system.
     * @param user The user whose assignments are to be removed.
     */
    public void removeAssignments(User user);
    
}
