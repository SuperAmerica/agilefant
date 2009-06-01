package fi.hut.soberit.agilefant.web;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Story;
import flexjson.JSONSerializer;

@Component("storyAction")
@Scope("prototype")
public class StoryAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -4289013472775815522L;

    // private HistoryBusiness historyBusiness;

    private int backlogId = 0;

    private int storyId;

    private State state;

    // private AFTime effortLeft;

    private Priority priority;

    private Story story;

    private Backlog backlog;

    private Set<Integer> userIds = new HashSet<Integer>();
    // private Map<Integer, String> userIds = new HashMap<Integer, String>();

    private Set<Integer> themeIds = new HashSet<Integer>();

    @Autowired
    private BacklogBusiness backlogBusiness;

    @Autowired
    private StoryBusiness storyBusiness;

    // private BusinessThemeBusiness businessThemeBusiness;

    private SettingBusiness settingBusiness;

    private HourEntryBusiness hourEntryBusiness;

    // private Map<Integer, State> todoStates = new HashMap<Integer, State>();
    //    
    // private Map<Integer, String> todoNames = new HashMap<Integer, String>();
    //    
    // private boolean todosToDone = false;

    private String spentEffort = null;

    private String spentEffortComment = null;

    private String storyListContext;

    // private List<BusinessTheme> bliActiveOrSelectedThemes;

    // private int fromTodoId = 0;

    // private TodoBusiness todoBusiness;

    private String jsonData = "";

    public String getJsonData() {
        return jsonData;
    }

    public String getStoryListContext() {
        return storyListContext;
    }

    public void setStoryListContext(String storyListContext) {
        this.storyListContext = storyListContext;
    }

    // public Map<Integer, State> getTodoStates() {
    // return todoStates;
    // }
    //
    // public void setTodoStates(Map<Integer, State> todoStates) {
    // this.todoStates = todoStates;
    // }

    public StoryBusiness getStoryBusiness() {
        return storyBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public String create() {
        // Id of newly created, not yet persisted backlog item is 0
        storyId = 0;

        story = new Story();
        if (backlogId != 0) {
            backlog = backlogBusiness.retrieve(backlogId);
            story.setBacklog(backlog);
        }
        return Action.SUCCESS;
    }

    public String delete() {
        try {
            Story story = storyBusiness.retrieve(storyId);
            backlogId = story.getBacklog().getId();
            storyBusiness.delete(storyId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return ERROR;
        }

        // If exception was not thrown from business method, return success.
        return SUCCESS;
    }

    public String ajaxDeleteBacklogItem() {
        try {
            storyBusiness.delete(storyId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return CRUDAction.AJAX_ERROR;
        }

        // If exception was not thrown from business method, return success.
        return CRUDAction.AJAX_SUCCESS;
    }

    public String edit() {
        story = storyBusiness.retrieveIfExists(storyId);
        if (story == null) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return Action.ERROR;
        }
        backlog = story.getBacklog();
        backlogId = backlog.getId();

        // historyBusiness.updateBacklogHistory(backlog.getId());
        // bliActiveOrSelectedThemes = businessThemeBusiness
        // .getBacklogItemActiveOrSelectedThemes(storyId);

        return Action.SUCCESS;
    }

    public String store() {
        if (this.storyStore() == false) {
            return ERROR;
        }
        return SUCCESS;
    }

    private void loadStoryJSON() {
        // if (this.settingBusiness.isHourReportingEnabled()) {
        // this.hourEntryBusiness
        // .setBacklogItemSpentEffortSum(this.story);
        // }
        // this.story.setUserData(getResponsiblesAsUserData());
        JSONSerializer ser = new JSONSerializer();
        // ser.include("businessThemes");
        // ser.include("todos");
        // ser.include("hourEntries");
        jsonData = ser.serialize(this.story);
    }

    public String getStoryJSON() {
        this.story = this.storyBusiness.retrieveIfExists(storyId);
        if (this.story == null) {
            return CRUDAction.AJAX_ERROR;
        }
        this.loadStoryJSON();
        return CRUDAction.AJAX_SUCCESS;
    }

    public String ajaxStoreStory() {
        if (this.storyStore() == false) {
            return CRUDAction.AJAX_ERROR;
        }
        this.loadStoryJSON();
        return CRUDAction.AJAX_SUCCESS;
    }

    // private List<BacklogItemResponsibleContainer> getResponsiblesAsUserData()
    // {
    // // TODO: Optimize this
    // List<BacklogItemResponsibleContainer> list = new
    // ArrayList<BacklogItemResponsibleContainer>();
    // Collection<User> assignees = backlogBusiness.getUsers(this.story
    // .getProject(), false);
    // for (User u : this.story.getResponsibles()) {
    // boolean inProject = true;
    // if (assignees.contains(u)) {
    // inProject = false;
    // }
    // list.add(new BacklogItemResponsibleContainer(u, inProject));
    // }
    // Collections.sort(list, new BacklogItemUserComparator());
    // return list;
    // }

    private boolean storyStore() {
        // validate original estimate, name and effort left
        if (this.story.getName() == null
                || this.story.getName().trim().equals("")) {
            return false;
        }
        // if (this.story.getEffortLeft() != null
        // && this.story.getEffortLeft().getTime() < 0) {
        // return false;
        // }
        // if (this.story.getOriginalEstimate() != null
        // && this.story.getOriginalEstimate().getTime() < 0) {
        // return false;
        // }

        // save backlog item, update todos and store backlog item themes
        try {
            story = storyBusiness.store(storyId, backlogId, story,
                    userIds);
//            businessThemeBusiness.setBacklogItemThemes(themeIds, story);
            storyId = story.getId();
        } catch (ObjectNotFoundException onfe) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

//    public String resetBliOrigEstAndEffortLeft() {
//        try {
//            backlogItemBusiness.resetBliOrigEstAndEffortLeft(backlogItemId);
//        } catch (ObjectNotFoundException e) {
//            addActionError(e.getMessage());
//            return Action.ERROR;
//        }
//        return Action.SUCCESS;
//    }
//
//    public String resetOriginalEstimate() {
//        try {
//            backlogItemBusiness.resetBliOrigEstAndEffortLeft(backlogItemId);
//            story = backlogItemBusiness.getBacklogItem(backlogItemId);
//        } catch (ObjectNotFoundException e) {
//            addActionError(e.getMessage());
//            return AJAX_ERROR;
//        }
//        this.loadStoryJSON();
//        return AJAX_SUCCESS;
//    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

   public Story getStory() {
    return story;
}
   public void setStory(Story story) {
    this.story = story;
}
   
    public String getStoryName() {
        return story.getName();
    }

    public void setStoryName(String backlogItemName) {
        story.setName(backlogItemName);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

}
