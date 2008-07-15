package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.EffortSumData;

public class ProductAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 1834399750050895118L;

    private ProductDAO productDAO;

    private BacklogItemDAO backlogItemDAO;

    private BacklogBusiness backlogBusiness;
    
    private ProjectBusiness projectBusiness;

    private HourEntryBusiness hourEntryBusiness;
    
    private int productId;

    private Product product;

    private Backlog backlog;

    private Collection<Product> products = new ArrayList<Product>();

    private EffortSumData effortLeftSum;

    private EffortSumData origEstSum;
    
    private Map<Project, EffortSumData> effLeftSums;
    
    private Map<Project, EffortSumData> origEstSums;
    
    private Map<BusinessTheme, String> doneBlis;
    
    private BusinessThemeBusiness businessThemeBusiness; 

    public String create() {
        productId = 0;
        product = new Product();
        backlog = product;
        return Action.SUCCESS;
    }

    public String delete() {
        Product p = productDAO.get(productId);
        if (p == null) {
            super.addActionError(super.getText("product.notFound"));
            return Action.ERROR;
        }
        if (p.getBacklogItems().size() > 0 || p.getProjects().size() > 0) {
            super.addActionError(super.getText("product.notEmptyWhenDeleting"));
            return Action.ERROR;
        }
        productDAO.remove(productId);
        return Action.SUCCESS;
    }

    public String edit() {
        Date startDate = new Date(0);
        product = productDAO.get(productId);
        if (product == null) {
            super.addActionError(super.getText("product.notFound"));
            return Action.ERROR;
        }
        backlog = product;

        // Calculate effort lefts and original estimates
        Collection<BacklogItem> items = backlog.getBacklogItems();
        effortLeftSum = backlogBusiness.getEffortLeftSum(items);
        origEstSum = backlogBusiness.getOriginalEstimateSum(items);

        // Calculate product's projects' effort lefts and original estimates
        
        effLeftSums = new HashMap<Project, EffortSumData>();
        origEstSums = new HashMap<Project, EffortSumData>();
        
        // Calculate themes' done blis
        doneBlis = businessThemeBusiness.getThemesDoneBacklogItems(productId);
        
        // Load Hour Entry sums to this backlog's BLIs.
        hourEntryBusiness.loadSumsToBacklogItems(backlog);
        
        Collection<Project> projects = product.getProjects();
        
        for (Project pro : projects) {
            Collection<BacklogItem> blis = projectBusiness.getBlisInProjectAndItsIterations(pro);
            EffortSumData projectEffLeftSum = backlogBusiness.getEffortLeftSum(blis);
            EffortSumData projectOrigEstSum = backlogBusiness.getOriginalEstimateSum(blis);
            effLeftSums.put(pro, projectEffLeftSum);
            origEstSums.put(pro, projectOrigEstSum);
        }
        
        
        return Action.SUCCESS;
    }

    public String store() {
        Product storable = new Product();

        if (productId > 0) {
            storable = productDAO.get(productId);
            if (storable == null) {
                super.addActionError(super.getText("product.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        if (productId == 0) {
            productId = (Integer) productDAO.create(storable);
        } else {
            productDAO.store(storable);
        }
        return Action.SUCCESS;
    }

    protected void fillStorable(Product storable) {
        if (this.product == null) {
            super.addActionError(super.getText("product.internalError"));
            return;
        }
        if (this.product.getName() == null || 
                this.product.getName().trim().equals("")) {
            super.addActionError(super.getText("product.missingName"));
            return;
        }
        storable.setName(this.product.getName());
        storable.setDescription(this.product.getDescription());
    }

    public EffortSumData getEffortLeftSum() {
        return effortLeftSum;
    }

    public EffortSumData getOriginalEstimateSum() {
        return origEstSum;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.backlog = product;
    }

    public Backlog getBacklog() {
        return this.backlog;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    protected ProductDAO getProductDAO() {
        return this.productDAO;
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

    /**
     * @return the backlogItemDAO
     */
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    /**
     * @param backlogItemDAO
     *                the backlogItemDAO to set
     */
    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public Map<Project, EffortSumData> getEffLeftSums() {
        return effLeftSums;
    }

    public Map<Project, EffortSumData> getOrigEstSums() {
        return origEstSums;
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public EffortSumData getOrigEstSum() {
        return origEstSum;
    }

    public void setOrigEstSum(EffortSumData origEstSum) {
        this.origEstSum = origEstSum;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public void setEffortLeftSum(EffortSumData effortLeftSum) {
        this.effortLeftSum = effortLeftSum;
    }

    public void setEffLeftSums(Map<Project, EffortSumData> effLeftSums) {
        this.effLeftSums = effLeftSums;
    }

    public void setOrigEstSums(Map<Project, EffortSumData> origEstSums) {
        this.origEstSums = origEstSums;
    }
    
    public Collection<BusinessTheme> getActiveBusinessThemes() {
        return businessThemeBusiness.getActiveBusinessThemes(productId);
    }

    public Collection<BusinessTheme> getNonActiveBusinessThemes() {
        return businessThemeBusiness.getNonActiveBusinessThemes(productId);
    }
    
    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

    public Map<BusinessTheme, String> getDoneBlis() {
        return doneBlis;
    }
}