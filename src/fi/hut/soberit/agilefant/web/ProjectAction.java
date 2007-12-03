package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -4636900464606739866L;

    private int projectId;

    private int productId;

    private int activityTypeId;

    private Project project;

    private ProjectDAO projectDAO;

    private ActivityTypeDAO activityTypeDAO;

    private ProductDAO productDAO;

    private Collection<ActivityType> activityTypes;

    private Backlog backlog;

    private BacklogItemDAO backlogItemDAO;

    private String startDate;

    private String endDate;

    private String dateFormat;

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat
     *                the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String create() {
        this.prepareActivityTypes();
        // TODO: fiksumpi virheenkäsittely
        // if (this.activityTypes.isEmpty()){
        // super.addActionError("project.activityTypesNotFound");
        // return Action.ERROR;
        // }
        projectId = 0;
        project = new Project();
        backlog = project;
        return Action.SUCCESS;
    }

    public String edit() {
        Date startDate;
        this.prepareActivityTypes();
        if (this.activityTypes.isEmpty()) {
            super.addActionError("project.activityTypesNotFound");
            return Action.ERROR;
        }
        project = projectDAO.get(projectId);
        startDate = project.getStartDate();

        if (project == null) {
            // super.addActionError(super.getText("project.notFound"));
            // return Action.ERROR;
            return Action.SUCCESS;
        }
        if (startDate == null) {
            startDate = new Date(0);
        }

        productId = project.getProduct().getId();
        backlog = project;
        /*
         * BacklogValueInjector.injectMetrics(backlog, startDate, taskEventDAO,
         * backlogItemDAO);
         */

        return Action.SUCCESS;
    }

    public String store() {
        Project storable = new Project();
        if (projectId > 0) {
            storable = projectDAO.get(projectId);
            if (storable == null) {
                super.addActionError(super.getText("project.notFound"));
                return Action.ERROR;
            }
        }

        try {
            this.fillStorable(storable);
        } catch (ParseException e) {
            super.addActionError(e.toString());
            return Action.ERROR;
        }

        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        if (projectId == 0)
            projectId = (Integer) projectDAO.create(storable);
        else
            projectDAO.store(storable);

        return Action.SUCCESS;
    }

    public String delete() {
        project = projectDAO.get(projectId);
        if (project == null) {
            super.addActionError(super.getText("project.notFound"));
            return Action.ERROR;
        }
        if (project.getBacklogItems().size() > 0
                || project.getIterations().size() > 0) {
            super.addActionError(super.getText("project.notEmptyWhenDeleting"));
            return Action.ERROR;
        }
        Product product = project.getProduct();
        productId = product.getId();
        product.getProjects().remove(project);
        project.setProduct(null);
        projectDAO.remove(project);
        return Action.SUCCESS;
    }

    protected void fillStorable(Project storable) throws ParseException {
        Date current = Calendar.getInstance().getTime();
        if (this.project.getName().equals("")) {
            super.addActionError(super.getText("project.missingName"));
            return;
        }

        project.setStartDate(startDate, dateFormat);
        if (project.getStartDate() == null) {
            super.addActionError(super.getText("project.missingStartDate"));
            return;
        }

        project.setEndDate(endDate, dateFormat);
        if (project.getEndDate() == null) {
            super.addActionError(super.getText("project.missingEndDate"));
            return;
        }
        if (project.getStartDate().after(project.getEndDate())) {
            super
                    .addActionError(super
                            .getText("backlog.startDateAfterEndDate"));
            return;
        }

        Product product = productDAO.get(productId);
        if (product == null) {
            super.addActionError(super.getText("product.notFound"));
            return;
        } else if (storable.getProduct() != product) {
            /*
             * Setting the relation in one end of the relation is enought to
             * change the relation in both ends! Hibernate takes care of both
             * ends.
             */
            storable.setProduct(product);
            // product.getProjects().add(storable);
        }

        if (storable.getActivityType() == null
                || storable.getActivityType().getId() != activityTypeId) {
            ActivityType activityType = null;
            if (activityTypeId > 0) {
                activityType = activityTypeDAO.get(activityTypeId);
            }
            if (activityType != null) {
                storable.setActivityType(activityType);
            } else {
                super.addActionError(super
                        .getText("project.missingActivityType"));
                return;
            }
        }
        storable.setEndDate(endDate, dateFormat);
        storable.setStartDate(startDate, dateFormat);
        storable.setName(project.getName());
        storable.setDescription(project.getDescription());
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Collection<Project> getAllProjects() {
        return this.projectDAO.getAll();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.backlog = project;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public int getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
        this.activityTypeDAO = activityTypeDAO;
    }

    private void prepareActivityTypes() {
        this.activityTypes = activityTypeDAO.getAll();
    }

    public Collection<ActivityType> getActivityTypes() {
        return this.activityTypes;
    }

    public void setActivityTypes(Collection<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public Backlog getBacklog() {
        return this.backlog;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}