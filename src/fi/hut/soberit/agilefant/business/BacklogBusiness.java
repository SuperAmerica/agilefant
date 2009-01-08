package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogLoadData;
import fi.hut.soberit.agilefant.util.BacklogMetrics;
import fi.hut.soberit.agilefant.util.EffortSumData;

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
     * Get a backlog by its id.
     * @param backlogId
     * @return
     * @throws ObjectNotFoundException
     */
    public Backlog getBacklog(int backlogId) throws ObjectNotFoundException;
    
    /**
     * Delete multiple <code>BacklogItems</code> at once.
     * 
     * @param backlogItemIds
     *                <code>Array</code> containing the IDs of the items to
     *                delete.
     */
    public void deleteMultipleItems(int backlogId, int backlogItemIds[])
            throws ObjectNotFoundException, OperationNotPermittedException;

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
     * Change the state of multiple <code>BacklogItem</code>s at once.
     * @param backlogItemIds <code>Array</code> containing the IDs of the items,
     *                          whose state should be changed
     * @param state the new state
     */
    public void changeStateOfMultipleItems(int backlogItemIds[],
            State state) throws ObjectNotFoundException;
    
    /**
     * Change the iterationGoals of multiple <code>BacklogItem</code>s at once.
     * @param backlogItemIds <code>Array</code> containing the IDs of the items,
     *                          whose iteration goal should be changed
     * @param state id of the new iteration goal
     */
    public void changeIterationGoalOfMultipleItems(int backlogItemIds[],
            int iterationGoalId) throws ObjectNotFoundException;

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
     * Changes themes for multiple backlog items. OverWrites BLIs current themes. 
     * 
     * @param backlogItemIds selected BLI's
     * @param businessThemeIds selected themes
     */
    public void changeBusinessThemesOfMultipleBacklogItems(int backlogItemIds[], 
            Set<Integer> businessThemeIds) throws ObjectNotFoundException;
    /**
     * Changes responsibles for multiple backlog items.
     * @param backlogItemIds
     * @param responsibleIds
     */
    public void setResponsiblesForMultipleBacklogItems(int backlogItemIds[],
            Set<Integer> responsibleIds) throws ObjectNotFoundException;
    
    /**
     * Calculates the sum of effort lefts of the given backlog items.
     * Includes hours and number of non-estimated items.
     * 
     * @param bliList
     *                List of backlog items
     * @return sum of effort lefts
     */
    public EffortSumData getEffortLeftSum(Collection<BacklogItem> bliList);
    
    /**
     * Calculates the sum of effort lefts of the given backlog items.
     * If assigned to multiple responsible, divides with number of responsibles
     * Includes hours and number of non-estimated items.
     * 
     * @param bliList
     *                List of backlog items
     * @return sum of effort lefts
     */    
    public EffortSumData getEffortLeftResponsibleDividedSum(Collection<BacklogItem> bliList);
    /**
     * Calculates the sum of original estimates of the given backlog items
     * Includes hours and number of non-estimated items.
     * 
     * @param bliList List of backlog items
     * @return sum of original estimates
     */
    public EffortSumData getOriginalEstimateSum(Collection<BacklogItem> bliList);
    
    /**
     * Assigns the selected users to the given backlog.
     * 
     * @param selectedUserIds Ids of the users to be assigned.
     * @param backlog The backlog the users are assigned to.
     * @param assignmentData overheads of users
     */
    public void setAssignments(int[] selectedUserIds, Map<String, Assignment> assignmentData, Backlog backlog);
    
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
    
    
    /**
     * Get weekdays left for an ongoing backlog. Only works for projects
     * and iterations.
     * 
     * @param backlog backlog to get
     * @param from
     * @return number of weekdays left
     */
    public int getWeekdaysLeftInBacklog(Backlog backlog, Date from);
    
    /**
     * Get number of days for a backlog on a certain week.
     * Starts from monday.
     * @param backlog backlog
     * @param time a day on the week.
     * @return
     */
    public int getNumberOfDaysForBacklogOnWeek(Backlog backlog, Date time);
    
    /**
     * Get number of days on a week after a certain time.
     */
    public int getNumberOfDaysLeftForBacklogOnWeek(Backlog backlog, Date time);
    
    /**
     * Calculate the load for an user for a certain period of time. 
     * @param backlog the backlog
     * @param user the user
     * @param from the date to start from
     * @return an instance of <code>BacklogLoadData</code>, where the data is stored
     */
    public BacklogLoadData calculateBacklogLoadData(Backlog backlog, User user, Date from, int numberOfWeeks);
    
    /**
     * Get a week's overhead for a certain project and an user.
     * @param project the project
     * @param user the user
     * @param daysOnWeek number of days on the week
     * @return the overhead
     */
    public AFTime getOverheadForWeek(Project project, User user, int daysOnWeek);
    
    /**
     * Get all backlogs, where user has assigned items or is assigned to.
     * If backlog is not current, don't show it.
     * @return collection of requested backlogs
     */
    public List<Backlog> getUserBacklogs(User user, Date now, int weeksAhead);
    
    
    /**
     * Get metrics information for a backlog.
     * <p>
     * Currently only at iteration and project levels. Using a product as parameter
     * will return null.
     * @param backlog
     * @return
     */
    public BacklogMetrics getBacklogMetrics(Backlog backlog);
    
    /**
     * Get the backlog's iteration goals as JSON.
     * @param backlogId
     * @return
     */
    public String getIterationGoalsAsJSON(int backlogId);
    
    /**
     * Get the backlog's iteration goals as JSON.
     * @param backlogId
     * @return
     */
    public String getIterationGoalsAsJSON(Backlog backlog);
    
    /**
     * Remove all business theme bindings.
     * 
     * @param backlog
     */
    public void removeThemeBindings(Backlog backlog);
    
    /**
     * Get all backlogs as a json string. 
     * @return
     */
    public String getAllBacklogsAsJSON();
    
    /**
     * Get all products as a json string. 
     * @return
     */
    public String getAllProductsAsJSON();
    
    /**
     * Get the backlog as a json string.
     */
    public String getBacklogAsJSON(Backlog backlog);
    
    /**
     * Get the backlog as a json string.
     */
    public String getBacklogAsJSON(int backlogId);
    
    /**
     * Calculate total effort left, original estimate and number of items for
     * items directly under given backlog. E.g. calling this method for project
     * return only data for backlog items under the project, not for backlog
     * items under project's iterations.
     * 
     * @param backlog
     * @return Metrics object with original estimate, effort left, total number
     *         of items and number of completed items.
     */
    public BacklogMetrics calculateLimitedBacklogMetrics(Backlog backlog);
    
    /**
     * Checks, if two backlogs are under the same product. We already know that
     * the backlogs are not null.
     * @param backlog1
     * @param backlog2
     * @return
     */
    public boolean isUnderSameProduct(Backlog backlog1, Backlog backlog2);

    /**
     * Checks, if two backlogs are under the same product
     * 
     * @see isUnderSameProduct(Backlog backlog1, Backlog backlog2)
     * @param backlog1Id
     * @param backlog2Id
     * @return false if either one of the backlog ids is invalid or backlogs are
     *         under different products. Else true.
     */
    public boolean isUnderSameProduct(int backlogId1, int backlogId2);
    
    /**
     * Returns product's and products sub-backlog's top level backlog items. Meaning those that don't have a parent BLI.
     * @return a list of top level BLIs.
     */
    public List<BacklogItem> getProductTopLevelBacklogItems(int productId);
    
    /**
     * Returns product's and products sub-backlog's top level backlog items as JSONs. Meaning those that don't have a parent BLI.
     * @return a list of top level BLIs.
     */
    public String getProductTopLevelBacklogItemsAsJson(int productId);
}
