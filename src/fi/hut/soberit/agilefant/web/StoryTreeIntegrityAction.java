package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryTreeIntegrityBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.MoveStoryNode;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityUtils;

@Component("storyTreeIntegrityAction")
@Scope("prototype")
public class StoryTreeIntegrityAction extends ActionSupport {

    private static final long serialVersionUID = 5026286059393178372L;

    public static final String FATAL_CONSTRAINT = "fatalConstraint"; 
    
    @Autowired
    private StoryTreeIntegrityBusiness storyTreeIntegrityBusiness;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private BacklogBusiness backlogBusiness;
    @Autowired
    private IterationBusiness iterationBusiness;
    
    private MoveStoryNode data;
    
    private List<StoryTreeIntegrityMessage> messages;
    
    private boolean parentStoryConflict = false;
    
    private Backlog backlog;
    private Story story;
    
    private Integer storyId;
    private Integer targetStoryId;
    private Integer backlogId;


    public String checkChangeBacklog() {
        story = storyBusiness.retrieve(storyId);
        backlog = backlogBusiness.retrieve(backlogId);
        
        messages = storyTreeIntegrityBusiness.checkChangeBacklog(story, backlog);

        data = storyTreeIntegrityBusiness.generateChangedStoryTree(story, messages);
        
        parentStoryConflict = storyTreeIntegrityBusiness.hasParentStoryConflict(story, backlog);
        
        if (StoryTreeIntegrityUtils.getFatalMessages(messages)) {
            return FATAL_CONSTRAINT; 
        }
        
        return Action.SUCCESS;
    }
    
    public String checkChangeParentStory() {
        Story story = storyBusiness.retrieve(storyId);
        Story target = storyBusiness.retrieve(targetStoryId);
        
        messages = storyTreeIntegrityBusiness.checkChangeParentStory(story, target);
        
        return Action.SUCCESS;
    }
    
    public void setStoryTreeIntegrityBusiness(
            StoryTreeIntegrityBusiness storyTreeIntegrityBusiness) {
        this.storyTreeIntegrityBusiness = storyTreeIntegrityBusiness;
    }

    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }

    public void setTargetStoryId(Integer targetStoryId) {
        this.targetStoryId = targetStoryId;
    }

    public List<StoryTreeIntegrityMessage> getMessages() {
        return messages;
    }

    public void setBacklogId(Integer backlogId) {
        this.backlogId = backlogId;
    }

    public MoveStoryNode getData() {
        return data;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public Story getStory() {
        return story;
    }

    public boolean isParentStoryConflict() {
        return parentStoryConflict;
    }

}
