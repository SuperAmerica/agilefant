package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.Status;
import fi.hut.soberit.agilefant.model.User;

public class ProjectAction extends BacklogContentsAction implements CRUDAction {

    Logger log = Logger.getLogger(this.getClass());
    
    private static final long serialVersionUID = -4636900464606739866L;

    private int projectId;

    private int productId;

    private int projectTypeId;
    
    private Status status;

    private Project project;

    private ProjectDAO projectDAO;

    private ProjectTypeBusiness projectTypeBusiness;

    private ProductDAO productDAO;

    private List<ProjectType> projectTypes;

//    private BacklogItemDAO backlogItemDAO;

    private String startDate;

    private String endDate;

    private String dateFormat;

    private int[] selectedUserIds;

    private List<User> users = new ArrayList<User>();
    
    private List<User> enabledUsers = new ArrayList<User>();
    
    private List<User> disabledUsers = new ArrayList<User>();

    private Collection<User> assignedUsers = new HashSet<User>();

    private Map<User, Integer> unassignedHasWork = new HashMap<User, Integer>();
    
    private List<User> assignableUsers = new ArrayList<User>();

    private UserBusiness userBusiness;

    private ProjectBusiness projectBusiness;
    
//    private Map<Iteration, EffortSumData> effLeftSums;
//    
//    private Map<Iteration, EffortSumData> origEstSums;
//    
//    private AFTime defaultOverhead;
//    
//    private Map<String,Assignment> assignments = new HashMap<String, Assignment>();
//    
//    private Map<Integer, AFTime> totalOverheads = new HashMap<Integer, AFTime>();
//    
//    private BacklogMetrics projectMetrics = new BacklogMetrics();
//        
//    private List<BacklogThemeBinding> iterationThemes;
      
    private boolean projectBurndown; 
    

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
        this.prepareProjectTypes();
        // TODO: fiksumpi virheenkäsittely
        // if (this.projectTypes.isEmpty()){
        // super.addActionError("project.projectTypesNotFound");
        // return Action.ERROR;
        // }
        projectId = 0;
        project = new Project();
        backlog = project;

        // populate all users to drop-down list
        users.addAll(userBusiness.retrieveAll());
        enabledUsers = userBusiness.getEnabledUsers();
        disabledUsers = userBusiness.getDisabledUsers();
        assignableUsers.addAll(projectBusiness.getUsersAssignableToProject(this.project));
        return Action.SUCCESS;
    }

    public String edit() {       
        Date startDate;
        this.prepareProjectTypes();
        project = projectDAO.get(projectId);

        if (project == null) {
            super.addActionError("Invalid project id!");
            return Action.ERROR;
        }
        startDate = project.getStartDate();

        if (startDate == null) {
            startDate = new Date(0);
        }

        productId = project.getParent().getId();
        backlog = project;
        super.initializeContents();
        
        // Calculate project's iterations' effort lefts and original estimates
//        effLeftSums = new HashMap<Iteration, EffortSumData>();
//        origEstSums = new HashMap<Iteration, EffortSumData>(); 
//        defaultOverhead = project.getDefaultOverhead();        
//        for (Assignment ass: project.getAssignments()) {
//            assignments.put("" + ass.getUser().getId(), ass);
//        }
        //totalOverheads = projectBusiness.calculateTotalOverheads(project);
        
        // Get backlog metrics
//        if (project.getIterations().size() == 0) {  
//            projectMetrics = backlogBusiness.getBacklogMetrics(project);
//        }
//        
//        Collection<Iteration> iterations = project.getIterations();
//        for (Iteration iter : iterations) {
//            Collection<BacklogItem> blis = iter.getBacklogItems();
//            EffortSumData effLeftSum = backlogBusiness.getEffortLeftSum(blis);
//            EffortSumData origEstSum = backlogBusiness.getOriginalEstimateSum(blis);
//            effLeftSums.put(iter, effLeftSum);
//            origEstSums.put(iter, origEstSum);
//            iter.setMetrics(backlogBusiness.getBacklogMetrics(iter));
//        }
//        
//        iterationThemes = businessThemeBusiness.getIterationThemesByProject(project);
        
        projectBurndown = settingBusiness.isProjectBurndownEnabled();
        
        return Action.SUCCESS;
    }

    public String store() {
       
        Project storable = new Project();
        if (projectId > 0) {
            storable = projectDAO.get(projectId);
            if (storable == null) {
                super.addActionError(super.getText("project.notFound"));
                return CRUDAction.AJAX_ERROR; 
            }
            if(storable.getParent() != null && productId > 0 &&
                    storable.getParent().getId() != productId) {
//                backlogBusiness.removeThemeBindings(storable);
            }
        }
        
        try {
            this.fillStorable(storable);
        } catch (ParseException e) {
            super.addActionError(e.toString());
        }

        if (super.hasActionErrors()) {
            return CRUDAction.AJAX_ERROR; 
        }
        // project-olion on oltava kannassa ennen kuin Assignmentit tehdään.
        if (projectId == 0) {
            projectId = (Integer) projectDAO.create(storable);
        } else {
            projectDAO.store(storable);
        }
//        backlogBusiness.setAssignments(selectedUserIds, this.assignments, projectDAO
//                .get(projectId));
        return CRUDAction.AJAX_SUCCESS;
    }

    public String ajaxStoreProject() {
        Project storable = new Project();
        if (projectId > 0) {
            storable = projectDAO.get(projectId);
            if (storable == null) {
                super.addActionError(super.getText("project.notFound"));
                return CRUDAction.AJAX_ERROR;
            }
            if(storable.getParent() != null && productId > 0 &&
                    storable.getParent().getId() != productId) {
//                backlogBusiness.removeThemeBindings(storable);
            }
        }

        try {
            this.fillStorable(storable);
        } catch (ParseException e) {
            super.addActionError(e.toString());
            return CRUDAction.AJAX_ERROR;
        }

        if (super.hasActionErrors()) {
            return CRUDAction.AJAX_ERROR;
        }        
        if (projectId == 0) {
            projectId = (Integer) projectDAO.create(storable);
        } else {
            projectDAO.store(storable);
        }
//        backlogBusiness.setAssignments(selectedUserIds, this.assignments, projectDAO
//                .get(projectId));
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String saveProjectAssignments() {
        if (projectId == 0) {
            super.addActionError(super.getText("project.notFound"));
            return Action.ERROR;
        }
//        backlogBusiness.setAssignments(selectedUserIds, this.assignments, projectDAO
//                .get(projectId));
        return Action.SUCCESS;
    }

    public String delete() {
        project = projectDAO.get(projectId);
        if (project == null) {
            super.addActionError(super.getText("project.notFound"));
            return Action.ERROR;
        }
//        if (project.getBacklogItems().size() > 0
//                || project.getIterations().size() > 0
//                || (project.getBusinessThemeBindings() != null
//                        && project.getBusinessThemeBindings().size() > 0)) {
//            super.addActionError(super.getText("project.notEmptyWhenDeleting"));
//            return Action.ERROR;
//        }
//        
//        projectBusiness.removeAllHourEntries( project );
//        
//        backlogBusiness.setAssignments(null, null, project);
//        Product product = project.getProduct();
//        productId = product.getId();
//        product.getProjects().remove(project);
//        project.setProduct(null);
        projectBusiness.delete(projectId);
        return Action.SUCCESS;
    }

    protected void fillStorable(Project storable) throws ParseException {
//        if(project.getDefaultOverhead() != null && project.getDefaultOverhead().getTime() < 0) {
//            super.addActionError("Default overhead cannot be negative.");
//            return;
//        }
        
        if (startDate == null) {
            super.addActionError(super.getText("Invalid startdate!"));
            return;
        } else if (endDate == null) {
            super.addActionError(super.getText("Invalid enddate!"));
            return;
        }

        if (this.project.getName() == null ||
                this.project.getName().trim().equals("")) {
            super.addActionError(super.getText("project.missingName"));
            return;
        }
//        project.setStartDate(CalendarUtils.parseDateFromString(startDate));
//        if (project.getStartDate() == null) {
//            super.addActionError(super.getText("project.missingStartDate"));
//            return;
//        }
//
//        project.setEndDate(CalendarUtils.parseDateFromString(endDate));
//        if (project.getEndDate() == null) {
//            super.addActionError(super.getText("project.missingEndDate"));
//            return;
//        }
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
        }
//        } else if (storable.getProduct() != product) {
//            /*
//             * Setting the relation in one end of the relation is enought to
//             * change the relation in both ends! Hibernate takes care of both
//             * ends.
//             */
//            storable.setProduct(product);
//            // product.getProjects().add(storable);
//        }

//        if (this.project.getProjectType() != null) {
//            ProjectType type = projectTypeBusiness.get(this.project
//                    .getProjectType().getId());
//            storable.setProjectType(type);
//        }
        /*
        if (storable.getProjectType() == null
                || storable.getProjectType().getId() != projectTypeId) {
            ProjectType projectType = null;
            if (projectTypeId > 0) {
                projectType = projectTypeDAO.get(projectTypeId);
            }
            storable.setProjectType(projectType);
            
            
            else {
                super.addActionError(super
                        .getText("project.missingProjectType"));
                return;
            }
            
        }
        */
//        storable.setStatus(project.getStatus());
//        storable.setEndDate(CalendarUtils.parseDateFromString(endDate));
//        storable.setStartDate(CalendarUtils.parseDateFromString(startDate));
//        storable.setName(project.getName());
//        storable.setDescription(project.getDescription());
//        storable.setDefaultOverhead(project.getDefaultOverhead());
//        storable.setBacklogSize(this.project.getBacklogSize());
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

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public void setProjectTypeBusiness(ProjectTypeBusiness projectTypeBusiness) {
        this.projectTypeBusiness = projectTypeBusiness;
    }

    private void prepareProjectTypes() {
//        this.projectTypes = (List<ProjectType>)projectTypeBusiness.getAll();
        Collections.sort(this.projectTypes);
    }

    public Collection<ProjectType> getProjectTypes() {
        return this.projectTypes;
    }

    public void setProjectTypes(List<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }

//    /**
//     * @return the backlogItemDAO
//     */
//    public BacklogItemDAO getBacklogItemDAO() {
//        return backlogItemDAO;
//    }
//
//    /**
//     * @param backlogItemDAO
//     *                the backlogItemDAO to set
//     */
//    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
//        this.backlogItemDAO = backlogItemDAO;
//    }

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

    public int[] getSelectedUserIds() {
        return selectedUserIds;
    }

    public void setSelectedUserIds(int[] selectedUserIds) {
        this.selectedUserIds = selectedUserIds;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public Collection<User> getAssignedUsers() {
        return assignedUsers;
    }
    
    public Map<User, Integer> getUnassignedHasWork() {
      	return unassignedHasWork;
    }
	
    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }
//
//    public Map<Iteration, EffortSumData> getEffLeftSums() {
//        return effLeftSums;
//    }
//
//    public Map<Iteration, EffortSumData> getOrigEstSums() {
//        return origEstSums;
//    }
//
//    public void setDefaultOverhead(AFTime defaultOverhead) {
//        this.defaultOverhead = defaultOverhead;
//    }
//
//    public Map<String, Assignment> getAssignments() {
//        return assignments;
//    }
//
//    public void setAssignments(Map<String, Assignment> assignments) {
//        this.assignments = assignments;
//    }

    public List<User> getEnabledUsers() {
        return enabledUsers;
    }

    public void setEnabledUsers(List<User> enabledUsers) {
        this.enabledUsers = enabledUsers;
    }

    public List<User> getDisabledUsers() {
        return disabledUsers;
    }

    public void setDisabledUsers(List<User> disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    public List<User> getAssignableUsers() {
        return assignableUsers;
    }

    public void setAssignableUsers(List<User> assignableUsers) {
        this.assignableUsers = assignableUsers;
    }

//    public BacklogMetrics getProjectMetrics() {
//        return projectMetrics;
//    }
//
//    public void setProjectMetrics(BacklogMetrics projectMetrics) {
//        this.projectMetrics = projectMetrics;
//    }
//    public AFTime getDefaultOverhead() {
//        return defaultOverhead;
//    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

//    public Map<Integer, AFTime> getTotalOverheads() {
//        return totalOverheads;
//    }
//
//    public List<BacklogThemeBinding> getIterationThemes() {
//        return iterationThemes;
//    }

    public boolean isProjectBurndown() {
        return projectBurndown;
    }
}