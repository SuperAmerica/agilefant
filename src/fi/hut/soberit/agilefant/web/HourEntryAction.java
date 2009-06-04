package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import flexjson.JSONSerializer;

@Component("hourEntryAction")
@Scope("prototype")
public class HourEntryAction extends ActionSupport implements CRUDAction {
    private static final long serialVersionUID = -3817350069919875136L;

    private int hourEntryId;
    private HourEntry hourEntry;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private TaskBusiness taskBusiness;
    @Autowired
    private ProjectBusiness projectBusiness;
    @Autowired
    private UserBusiness userBusiness;
    private TimesheetLoggable target;
    private int userId = 0;
    private String date;
    private DateTime internalDate;


    private int backlogId = 0;
    private int storyId = 0;
    private int taskId = 0;
    private int iterationId;
    private int projectId;
    private int productId;
    private String jsonData = "";

    // multi edit
    private Map<Integer, String[]> userIdss = new HashMap<Integer, String[]>();
    private Map<Integer, String[]> dates = new HashMap<Integer, String[]>();
    private Map<Integer, String[]> descriptions = new HashMap<Integer, String[]>();
    private Map<Integer, String[]> efforts = new HashMap<Integer, String[]>();

    // private Map<Integer, String> userIds = new HashMap<Integer, String>();
    private Set<Integer> userIds = new HashSet<Integer>();

    // private Log logger = LogFactory.getLog(getClass());

    /**
     * {@inheritDoc}
     */
    public String create() {
        hourEntryId = 0;
        hourEntry = new HourEntry();
        hourEntry.setDate(new DateTime());
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String delete() {
        HourEntry h = hourEntryBusiness.retrieve(hourEntryId);
        if (h == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            return CRUDAction.AJAX_ERROR;
        }
        hourEntryBusiness.delete(hourEntryId);
        return CRUDAction.AJAX_SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public String edit() {

        hourEntry = hourEntryBusiness.retrieve(hourEntryId);
        if (hourEntry == null) {
            super.addActionError(super.getText("hourEntry.notFound"));
            create();
            return Action.ERROR;
        }
        internalDate = hourEntry.getDate();
        return Action.SUCCESS;
    }

    /**
     * {@inheritDoc} TODO: check that target is valid
     */
    public String store() {
        HourEntry storable = new HourEntry();
        if (hourEntryId > 0) {
            storable = hourEntryBusiness.retrieve(hourEntryId);
            if (storable == null) {
                super.addActionError(super.getText("hourEntry.notFound"));
                return CRUDAction.AJAX_ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return CRUDAction.AJAX_ERROR;
        }
        // Existing entries cannot be "shared"
        TimesheetLoggable parent = getParent();
        if (hourEntryId > 0 || userId > 0) {
            storable.setUser(userBusiness.retrieve(userId));
            jsonData = new JSONSerializer().serialize(hourEntryBusiness.store(
                    parent, storable));
        } else if (userId == 0 /* We have a list of user ids */) {
            if (userIds.size() < 1) {
                super.addActionError(super.getText("hourEntry.noUsers"));
                return CRUDAction.AJAX_ERROR;
            }
            hourEntryBusiness.addHourEntryForMultipleUsers(parent, storable,
                    userIds);
            jsonData = new JSONSerializer().serialize(new Object());

            // hack in order to make the returned data look like json data
        }
        return CRUDAction.AJAX_SUCCESS;
    }


    public String multiEdit() { 
        hourEntryBusiness.updateMultiple(userIdss,
                dates, efforts, descriptions); 
        return CRUDAction.AJAX_SUCCESS; 
    }

    protected void fillStorable(HourEntry storable) {
        storable.setDate(this.internalDate);
        storable.setDescription(this.hourEntry.getDescription());
        storable.setMinutesSpent(this.hourEntry.getMinutesSpent());
        storable.setUser(this.hourEntry.getUser());
    }

    private TimesheetLoggable getParent() {
        TimesheetLoggable parent = null;
        if (storyId > 0) {
            parent = storyBusiness.retrieve(storyId);
        } else if (backlogId > 0) {
            parent = projectBusiness.retrieve(backlogId);
        } else if (taskId > 0) {
            parent = taskBusiness.retrieve(taskId);
        }
        return parent;
    }

    public int getHourEntryId() {
        return hourEntryId;
    }

    public void setHourEntryId(int hourEntryId) {
        this.hourEntryId = hourEntryId;
    }

    public HourEntry getHourEntry() {
        return hourEntry;
    }

    public void setHourEntry(HourEntry hourEntry) {
        this.hourEntry = hourEntry;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public TimesheetLoggable getTarget() {
        // TODO: Ugly workaround, refactor?
        this.target = getParent();
        return target;
    }

    public void setTarget(TimesheetLoggable parent) {
        this.target = parent;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String sDate) {
        try {
            this.internalDate = new DateTime(CalendarUtils.parseDateFromString(sDate));
        } catch (ParseException e) {

        }
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public int getBacklogItemId() {
        return storyId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.storyId = backlogItemId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public void setUserIdss(Map<Integer, String[]> userIdss) {
        this.userIdss = userIdss;
    }

    public void setDates(Map<Integer, String[]> dates) {
        this.dates = dates;
    }

    public void setDescriptions(Map<Integer, String[]> descriptions) {
        this.descriptions = descriptions;
    }

    public void setEfforts(Map<Integer, String[]> efforts) {
        this.efforts = efforts;
    }

    public Map<Integer, String[]> getUserIdss() {
        return userIdss;
    }

    public Map<Integer, String[]> getDates() {
        return dates;
    }

    public Map<Integer, String[]> getDescriptions() {
        return descriptions;
    }

    public Map<Integer, String[]> getEfforts() {
        return efforts;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
    
    public DateTime getInternalDate() {
        return internalDate;
    }

}
