package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.ProjectTO;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

@Service("productBusiness")
@Transactional
public class ProductBusinessImpl extends GenericBusinessImpl<Product> implements
        ProductBusiness {

    private ProductDAO productDAO;
    @Autowired
    private ProjectBusiness projectBusiness;
    @Autowired
    private IterationBusiness iterationBusiness;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;

    public ProductBusinessImpl() {
        super(Product.class);
    }
    
    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.genericDAO = productDAO;
        this.productDAO = productDAO;
    }

    @Transactional(readOnly = true)
    public Collection<Product> retrieveAllOrderByName() {
        return productDAO.retrieveBacklogTree();
    }

    public Product store(int productId, Product productData) {
        this.validateProductData(productData);
        Product storable = new Product();
        if (productId > 0) {
            storable = this.retrieve(productId);
        }
        storable.setName(productData.getName());
        storable.setDescription(productData.getDescription());
        if (storable.getId() > 0) {
            this.store(storable);
            return storable;
        } else {
            int createdId = this.create(storable);
            return this.retrieve(createdId);
        }
    }

    public void validateProductData(Product productData)
            throws IllegalArgumentException {
        if (productData.getName() == null
                || productData.getName().trim().length() == 0) {
            throw new IllegalArgumentException("product.emptyName");
        }
    }
    
    public List<ProjectTO> retrieveProjects(Product product) {
        List<ProjectTO> projects = new ArrayList<ProjectTO>();
        for(Backlog child : product.getChildren()) {
            if(child instanceof Project) {
                projects.add(transferObjectBusiness.constructProjectTO((Project)child));
            }
        }
        return projects;
    }

    @Override
    public void delete(int id) {
        delete(retrieve(id));
    }
    
    @Override
    public void delete(Product product) {
        if (product == null)
            return;
        Set<Backlog> children = new HashSet<Backlog>(product.getChildren());
        
        if(children != null) {
            for (Backlog item : children) {
                if(item instanceof Project) {
                    projectBusiness.delete(item.getId());
                } else if(item instanceof Iteration) {
                    iterationBusiness.delete(item.getId());
                }
            }
        }
        
        Set<Story> stories = new HashSet<Story>(product.getStories());
        if (stories != null) {
            for (Story item : stories) {
                storyBusiness.delete(item, TaskHandlingChoice.DELETE,
                        HourEntryHandlingChoice.DELETE,
                        HourEntryHandlingChoice.DELETE);
            }
        }
        
        Set<BacklogHourEntry> hourEntries = new HashSet<BacklogHourEntry>(product.getHourEntries());   
        if (hourEntries != null) {
            hourEntryBusiness.deleteAll(hourEntries);
        }
        
        super.delete(product);
    }
}
