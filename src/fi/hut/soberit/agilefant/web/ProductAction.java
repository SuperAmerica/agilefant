package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.transfer.ProjectTO;
import fi.hut.soberit.agilefant.util.DateTimeUtils;
import fi.hut.soberit.agilefant.util.Pair;

@Component("productAction")
@Scope("prototype")
public class ProductAction implements CRUDAction, Prefetching, ContextAware {

    private static final long serialVersionUID = 1834399750050895118L;

    @Autowired
    private ProductBusiness productBusiness;

    @PrefetchId
    private int productId;

    private Product product = new Product();

    private Collection<Product> products = new ArrayList<Product>();
    
    private List<ProjectTO> childBacklogs = new ArrayList<ProjectTO>();
    
    private List<Story> stories = new ArrayList<Story>();
    
    private DateTime scheduleStart;
    private DateTime scheduleEnd;
    
    private Set<Integer> teamIds = new HashSet<Integer>();
    
    private boolean teamsChanged;

    public String create() {
        productId = 0;
        product = new Product();
        return Action.SUCCESS;
    }

    public String delete() {
        productBusiness.delete(productId);
        return Action.SUCCESS;
    }

    public String retrieve() {
        product = productBusiness.retrieve(productId);
        Pair<DateTime, DateTime> schedule = productBusiness.calculateProductSchedule(product);
        // Round the dates
        this.scheduleEnd = DateTimeUtils.roundToNearestMidnight(schedule.second);
        this.scheduleStart = DateTimeUtils.roundToNearestMidnight(schedule.first);
        return Action.SUCCESS;
    }
    
    public String retrieveLeafStories() {
        Product product = productBusiness.retrieve(productId);
        this.product = productBusiness.retrieveLeafStoriesOnly(product);
        return Action.SUCCESS;
    }

    public String store() {
        Set<Integer> teams = null;
        if (teamsChanged) {
            teams = teamIds;
        }
        
        this.product = this.productBusiness.store(productId, product, teams);
        this.productId = this.product.getId();
        return Action.SUCCESS;
    }

    public String retrieveAll() {
        products = productBusiness.retrieveAll();
        return Action.SUCCESS;
    }
    
    public String retrieveProjects() {
        this.product = this.productBusiness.retrieve(productId);
        this.childBacklogs = this.productBusiness.retrieveProjects(product);
        return Action.SUCCESS;
    }
    public void initializePrefetchedData(int objectId) {
       product = productBusiness.retrieve(objectId);
    }
    
    public String getContextName() {
        return "backlog";
    }
    
    public int getContextObjectId() {
        return productId;
    }
    

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Collection<Product> getProducts() {
        return products;
    }

    public void setProducts(Collection<Product> products) {
        this.products = products;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    public List<Story> getStories() {
        return stories;
    }

    public List<ProjectTO> getChildBacklogs() {
        return childBacklogs;
    }

    public DateTime getScheduleStart() {
        return scheduleStart;
    }

    public DateTime getScheduleEnd() {
        return scheduleEnd;
    }
    
    public void setTeamsChanged(boolean teamsChanged) {
        this.teamsChanged = teamsChanged;
    }

    public Set<Integer> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Set<Integer> teamIds) {
        this.teamIds = teamIds;
    }

}