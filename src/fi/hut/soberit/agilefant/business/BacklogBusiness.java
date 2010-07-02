package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Set;

import org.joda.time.Days;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Schedulable;

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
 * at both <code>Story</code> and <code>Backlog</code>. How and when
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
public interface BacklogBusiness extends GenericBusiness<Backlog> {

    Collection<Backlog> retrieveMultiple(Collection<Integer> idList);

    /**
     * Get the number of backlog's child backlogs.
     */
    public int getNumberOfChildren(Backlog backlog);
    
    /**
     * Gets the backlog's child backlogs or all products if backlog is <code>null</code>.
     */
    public Collection<Backlog> getChildBacklogs(Backlog backlog);

    public int calculateStoryPointSum(int backlogId);
    
    public int calculateDoneStoryPointSum(int backlogId);
    
    public void addAssignees(int backlogId, Set<Integer> userIds);

    /**
     * Recurse the backlog's parent product.
     */
    public Product getParentProduct(Backlog backlog);
    
    public int getStoryPointSumByBacklog(Backlog backlog);
    
    public Days daysLeftInSchedulableBacklog(Schedulable backlog);
    public float calculateBacklogTimeframePercentageLeft(Schedulable backlog);
}
