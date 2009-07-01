package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import flexjson.JSONSerializer;

@Component("storyAction")
@Scope("prototype")
public class StoryAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -4289013472775815522L;

    private int backlogId = 0;

    private int storyId;

    private StoryState state;
    
    private int iterationId;

    private int priority;

    private Story story;

    private Backlog backlog;

    private Set<Integer> userIds = new HashSet<Integer>();
    
    private String storyListContext;

    private String jsonData;
    
    private boolean moveTasks;

    
    @Autowired
    private BacklogBusiness backlogBusiness;

    @Autowired
    private StoryBusiness storyBusiness;

    public String create() {
        // Id of newly created, not yet persisted story is 0
        storyId = 0;

        story = new Story();
        story.setPriority(-1);
        if (backlogId != 0) {
            backlog = backlogBusiness.retrieve(backlogId);
            story.setBacklog(backlog);
        }
        return Action.SUCCESS;
    }

    public String delete() {
        try {
            storyBusiness.remove(storyId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText("story.notFound"));
            return ERROR;
        }

        // If exception was not thrown from business method, return success.
        return SUCCESS;
    }

    public String moveStory() {
        storyBusiness.attachStoryToBacklog(storyId, backlogId, moveTasks);
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String ajaxDeleteStory() {
        try {
            storyBusiness.remove(storyId);
        } catch (ConstraintViolationException e) {
            return CRUDAction.AJAX_FORBIDDEN;
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText("story.notFound"));
            return CRUDAction.AJAX_ERROR;
        }

        // If exception was not thrown from business method, return success.
        return CRUDAction.AJAX_SUCCESS;
    }

    public String edit() {
        story = storyBusiness.retrieveIfExists(storyId);
        if (story == null) {
            super.addActionError(super.getText("story.notFound"));
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
        StoryTO storyTO = new StoryTO(story);
        storyTO.setUserData(getResponsiblesAsUserData());
        // if (this.settingBusiness.isHourReportingEnabled()) {
        // this.hourEntryBusiness
        // .setBacklogItemSpentEffortSum(this.story);
        // }
        // this.story.setUserData(getResponsiblesAsUserData());
        JSONSerializer ser = new JSONSerializer();
        // ser.include("businessThemes");
        // ser.include("todos");
        // ser.include("hourEntries");
        jsonData = ser.serialize(storyTO);
    }
    
    private Collection<ResponsibleContainer> getResponsiblesAsUserData() {
        return storyBusiness.getStoryResponsibles(story);
    }
    
    public String getStoryContents() {
        Collection<Task> storyContents = storyBusiness.getStoryContents(storyId, iterationId);
        jsonData = new JSONSerializer().serialize(storyContents);
        return CRUDAction.AJAX_SUCCESS;
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

    public String getMetrics() {
        StoryMetrics metrics;
        try {
            if (storyId > 0) {
                metrics = storyBusiness.calculateMetrics(storyId);
            } else {
                metrics = storyBusiness.calculateMetricsWithoutStory(iterationId);
            }
            jsonData = new JSONSerializer().serialize(metrics);
        } catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }

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

        // save story and store backlog item themes
        try {
            story = storyBusiness.store(storyId, backlogId, story, userIds, priority);            
//            businessThemeBusiness.setBacklogItemThemes(themeIds, story);
            storyId = story.getId();
        } catch (ObjectNotFoundException onfe) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    
    
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
        this.storyId = story.getId();
    }
   
    public String getStoryName() {
        return story.getName();
    }

    public void setStoryName(String storyName) {
        story.setName(storyName);
    }

    public StoryState getState() {
        return state;
    }

    public void setState(StoryState state) {
        this.state = state;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }
    
    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }
    
    public int getIterationId() {
        return iterationId;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }
    
    public void setMoveTasks(boolean moveTasks) {
        this.moveTasks = moveTasks;
    }

    public String getJsonData() {
        return jsonData;
    }
    

    public String getStoryListContext() {
        return storyListContext;
    }

    public void setStoryListContext(String storyListContext) {
        this.storyListContext = storyListContext;
    }
    
    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
    
}
