package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.transfer.ProductTO;
import fi.hut.soberit.agilefant.transfer.ProjectTO;
import fi.hut.soberit.agilefant.util.Pair;

public interface ProductBusiness extends GenericBusiness<Product> {

    public Collection<Product> retrieveAllOrderByName();
    
    public Product store(int productId, Product productData);
    
    void delete(int id);
    
    void delete(Product product);

    List<ProjectTO> retrieveProjects(Product product);
    
    public ProductTO retrieveLeafStoriesOnly(Product product);
    
    public Pair<DateTime, DateTime> calculateProductSchedule(Product product);
}
