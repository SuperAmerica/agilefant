package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.util.EffortSumData;

public class ProductAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 1834399750050895118L;

    private ProductDAO productDAO;

    private BacklogItemDAO backlogItemDAO;

    private BacklogBusiness backlogBusiness;

    private int productId;

    private Product product;

    private Backlog backlog;

    private Collection<Product> products = new ArrayList<Product>();

    private EffortSumData effortLeftSum;

    private EffortSumData origEstSum;

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
        /*
         * BacklogValueInjector.injectMetrics(backlog, startDate, taskEventDAO,
         * backlogItemDAO);
         */
        // Calculate effort lefts and original estimates
        Collection<BacklogItem> items = backlog.getBacklogItems();
        effortLeftSum = backlogBusiness.getEffortLeftSum(items);
        origEstSum = backlogBusiness.getOriginalEstimateSum(items);

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
        if (this.product.getName().equals("")) {
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
}