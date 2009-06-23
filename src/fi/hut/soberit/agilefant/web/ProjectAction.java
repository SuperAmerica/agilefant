package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.Status;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectDataContainer;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import flexjson.JSONSerializer;

@Component("projectAction")
@Scope("prototype")
public class ProjectAction extends BacklogContentsAction implements CRUDAction {

    Logger log = Logger.getLogger(this.getClass());
    
    private static final long serialVersionUID = -4636900464606739866L;

    private int projectId;

    private int productId;

    private int projectTypeId;
    
    private Status status;

    private Project project;

    private List<ProjectType> projectTypes;

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
    
    private Map<String, Assignment> assignments = new HashMap<String, Assignment>();
      
    private boolean projectBurndown;
    
    private ProjectMetrics projectMetrics;
    
    private String jsonData;

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private ProjectBusiness projectBusiness;
    
    @Autowired
    private ProductBusiness productBusiness;

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    
    public String projectContents() {
        ProjectDataContainer data = projectBusiness.getProjectContents(projectId);
        JSONSerializer ser = new JSONSerializer();
        
        ser.include("stories.userData");
        ser.include("stories.tasks");
        ser.include("stories.tasks.userData");
        
        jsonData = ser.serialize(data);
        
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String projectMetrics() {
        project = projectBusiness.retrieve(projectId);
        projectMetrics = projectBusiness.getProjectMetrics(project);
        return Action.SUCCESS;
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
        this.prepareProjectTypes();
        project = projectBusiness.retrieve(projectId);
        productId = project.getParent().getId();
        backlog = project;
        super.initializeContents();
                
        for (Assignment ass: project.getAssignments()) {
            assignments.put("" + ass.getUser().getId(), ass);
        }
        
        return Action.SUCCESS;
    }
    
    private boolean projectStore() {
        // Data collection
        try {
            project.setId(projectId);
            project.setStartDate(CalendarUtils.parseDateFromString(startDate));
            project.setEndDate(CalendarUtils.parseDateFromString(endDate));
            project.setParent(productBusiness.retrieve(productId));
            
            // TODO: Fix when project types are done
            project.setProjectType(null);
        } catch (ParseException pe) {
            return false;
        } catch (ObjectNotFoundException onfe) {
            return false;
        }
        
        projectBusiness.storeProject(project, assignments.values());
        
        return true;
    }
    
    public String store() {
       if (!this.projectStore()) {
           return CRUDAction.AJAX_ERROR;
       }
       return CRUDAction.AJAX_SUCCESS;
    }
    
    public String ajaxStoreProject() {
        return this.store();
    }
    
    public String delete() {
        project = projectBusiness.retrieve(projectId);
        if (project == null) {
            super.addActionError(super.getText("project.notFound"));
            return Action.ERROR;
        }
        if (project.getStories().size() > 0
                || project.getChildren().size() > 0) {
//                || (project.getBusinessThemeBindings() != null
//                        && project.getBusinessThemeBindings().size() > 0)) {
            super.addActionError(super.getText("project.notEmptyWhenDeleting"));
            return Action.ERROR;
        }
//        
//        projectBusiness.removeAllHourEntries( project );
//        
        projectBusiness.setProjectAssignments(project, null);
        project.getParent().getChildren().remove(project);
        projectBusiness.delete(projectId);
        return Action.SUCCESS;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Collection<Project> getAllProjects() {
        return this.projectBusiness.retrieveAll();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.backlog = project;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    private void prepareProjectTypes() {
        // TODO: 090601 Reko: Fix this for project types to work
        // this.projectTypes = (List<ProjectType>)projectTypeBusiness.getAll();
        //Collections.sort(this.projectTypes);
        this.projectTypes = new ArrayList<ProjectType>();
    }

    public Collection<ProjectType> getProjectTypes() {
        return this.projectTypes;
    }

    public void setProjectTypes(List<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isProjectBurndown() {
        return projectBurndown;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    public Map<String, Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Map<String, Assignment> assignments) {
        this.assignments = assignments;
    }


    public String getJsonData() {
        return jsonData;
    }

    public ProjectMetrics getProjectMetrics() {
        return projectMetrics;
    }
}