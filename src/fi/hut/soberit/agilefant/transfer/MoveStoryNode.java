package fi.hut.soberit.agilefant.transfer;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

public class MoveStoryNode {
    private Story story;
    private Backlog newBacklog;
    private StoryTreeIntegrityMessage message;
    private List<MoveStoryNode> children;
    private boolean containsChanges;
    private boolean changed;
    
    
    
    public Story getStory() {
        return story;
    }
    public void setStory(Story story) {
        this.story = story;
    }
    public Backlog getNewBacklog() {
        return newBacklog;
    }
    public void setNewBacklog(Backlog newBacklog) {
        this.newBacklog = newBacklog;
    }
    public StoryTreeIntegrityMessage getMessage() {
        return message;
    }
    public void setMessage(StoryTreeIntegrityMessage message) {
        this.message = message;
    }
    public List<MoveStoryNode> getChildren() {
        return children;
    }
    public void setChildren(List<MoveStoryNode> children) {
        this.children = children;
    }
    public boolean isContainsChanges() {
        return containsChanges;
    }
    public void setContainsChanges(boolean containsChanges) {
        this.containsChanges = containsChanges;
    }
    public boolean isChanged() {
        return changed;
    }
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
