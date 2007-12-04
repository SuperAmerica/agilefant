package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;

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
    public void deleteMultipleItems(int backlogId, int backlogItemIds[]);

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
     * @param backlogItemIds <code>Array</code> containing the IDs of the items to prioritize
     * @param priority the new priority to set
     */
    public void changePriorityOfMultipleItems(int backlogItemIds[],
            Priority priority);
}
