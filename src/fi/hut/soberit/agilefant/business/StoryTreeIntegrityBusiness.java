package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.exception.StoryTreeIntegrityViolationException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.MoveStoryNode;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

/**
 * Used for checking if moving a story is permitted by following rules:
 * 
 * 1. Disallow children for iteration stories
 * 2. Disallow situations, where parent story is deeper in backog hierarchy than
 *    its children
 * 3. Disallow situations, where child stories reside under a different branch
 * 
 * @author Reko Jokelainen, Pasi Pekkanen
 *
 */
public interface StoryTreeIntegrityBusiness {
    
    public List<StoryTreeIntegrityMessage> checkChangeBacklog(Story story, Backlog newBacklog);
    
    public boolean canStoryBeMovedToBacklog(Story story, Backlog newBacklog);
    
    public List<StoryTreeIntegrityMessage> checkChangeParentStory(Story story, Story newParent); 
    
    public MoveStoryNode generateChangedStoryTree(Story movedStory, List<StoryTreeIntegrityMessage> messages);
    
    public boolean hasParentStoryConflict(Story story, Backlog newBacklog);
    public void checkChangeParentStoryAndThrow(Story story, Story newParent) throws StoryTreeIntegrityViolationException;
}
