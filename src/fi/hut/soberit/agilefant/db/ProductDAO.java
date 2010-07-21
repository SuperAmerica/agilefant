package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * Interface for a DAO of a Product.
 * 
 * @see GenericDAO
 */
public interface ProductDAO extends GenericDAO<Product> {

    Collection<Product> getAllOrderByName();
    
    public List<Product> retrieveBacklogTree();

    public List<Story> retrieveLeafStories(Product product);
    
    public Pair<DateTime, DateTime> retrieveScheduleStartAndEnd(Product product);
}
