package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryTreeIntegrityBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.MoveStoryNode;
import fi.hut.soberit.agilefant.util.StoryHierarchyIntegrityViolationType;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;


@Service("storyTreeIntegrityBusiness")
@Transactional(readOnly = true)
public class StoryTreeIntegrityBusinessImpl implements StoryTreeIntegrityBusiness {

    /** {@inheritDoc} */
    public List<StoryTreeIntegrityMessage> checkChangeBacklog(
            Story story, Backlog newBacklog) {
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>();
        
        // If story has children
        if (!story.getChildren().isEmpty()) {
            /*
             * Can't move to iteration, if the story has children.
             */
            checkMoveToIterationRule(story, newBacklog, messages);
            
            /*
             * Can't move to different branch, if the story has children.
             */
            Set<Backlog> allowed = getAllowedBacklogsForChildren(newBacklog); 
            checkChildBacklogRule(story, messages, allowed, StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH);
        }
        
        // If story has a parent
        if (story.getParent() != null) {
            /*
             * Can't move to product, if the story has parent in project.
             * Note!
             * Parents can't reside in iterations
             */
            checkParentDepthRule(story, newBacklog, messages);
            
            /*
             * Can't move to another branch, if the story's parent is not on 
             * product level.
             */
            checkParentDifferentProjectRule(story, newBacklog, messages);
        }
        
        return messages;
    }
    
    private Set<Backlog> getAllowedBacklogsForChildren(Backlog newBacklog) {
        Set<Backlog> allowed = new HashSet<Backlog>();
        
        allowed.add(newBacklog);
        
        for (Backlog child : newBacklog.getChildren()) {
            allowed.addAll(getAllowedBacklogsForChildren(child));
        }
        
        return allowed;
    }

    private void checkChildBacklogRule(Story parent, List<StoryTreeIntegrityMessage> messages,
            Set<Backlog> allowedBacklogs, StoryHierarchyIntegrityViolationType message) {
        
        for (Story child : parent.getChildren()) {
            
            if (!allowedBacklogs.contains(child.getBacklog())) {
                messages.add(new StoryTreeIntegrityMessage(parent, child, message));
            }
            
            checkChildBacklogRule(child, messages, allowedBacklogs, message);
        }
        
    }
    
    private void checkMoveToIterationRule(Story story, Backlog newBacklog,
            List<StoryTreeIntegrityMessage> messages) {
        if (newBacklog instanceof Iteration) {
            messages.add(new StoryTreeIntegrityMessage(story, null,
                    StoryHierarchyIntegrityViolationType.MOVE_TO_ITERATION_HAS_CHILDREN));
        }
    }
    
    private void checkParentDepthRule(Story story, Backlog newBacklog,
            List<StoryTreeIntegrityMessage> messages) {
        
        if (!(newBacklog instanceof Product)) {
            return;
        }
        
        for (Story parent = story.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getBacklog() instanceof Project) {
                messages.add(new StoryTreeIntegrityMessage(story, parent,
                        StoryHierarchyIntegrityViolationType.PARENT_DEEPER_IN_HIERARCHY));
            }
        }
    }
    
    private void checkParentDifferentProjectRule(Story story, Backlog newBacklog,
            List<StoryTreeIntegrityMessage> messages) {
        if (story.getBacklog() instanceof Product || newBacklog instanceof Product) {
            return;
        }
        Set<Backlog> allowedBacklogsForParents = new HashSet<Backlog>();
        
        if (newBacklog instanceof Iteration) {
            allowedBacklogsForParents.add(newBacklog.getParent());
            allowedBacklogsForParents.add(newBacklog.getParent().getParent());
        }
        else if (newBacklog instanceof Project) {
            allowedBacklogsForParents.add(newBacklog);
            allowedBacklogsForParents.add(newBacklog.getParent());
        }
        
        for (Story parent = story.getParent(); parent != null; parent = parent.getParent()) {
           if (!allowedBacklogsForParents.contains(parent.getBacklog())) {
                messages.add(new StoryTreeIntegrityMessage(story, parent,
                        StoryHierarchyIntegrityViolationType.PARENT_IN_WRONG_BRANCH));
           }
        }
        
    }
    
    
    
    
    /*
     * CHANGING PARENT STORY
     */
    
    
    
    
    /** {@inheritDoc} */
    public List<StoryTreeIntegrityMessage> checkChangeParentStory(
            Story story, Story newParent) {
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>();
        
        /*
         * Target parent story can't reside in an iteration
         */
        checkTargetParentInIterationRule(story, newParent, messages);
        
        /*
         * Check that all the children are allowed to move to target branch.
         */
        if (newParent.getBacklog() instanceof Project) {
            Set<Backlog> allowedBacklogs = getAllowedBacklogsForChildren(newParent.getBacklog());
            
            checkTargetBacklogInWrongBranch(story, newParent, messages, allowedBacklogs);
            
            checkChildBacklogRule(story, messages, allowedBacklogs, StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH);
        }
        
        
        return messages;
    }

    private void checkTargetBacklogInWrongBranch(Story story, Story newParent,
            List<StoryTreeIntegrityMessage> messages,
            Set<Backlog> allowedBacklogs) {
        if (story.getBacklog() instanceof Product) {
            messages.add(new StoryTreeIntegrityMessage(story, newParent, StoryHierarchyIntegrityViolationType.TARGET_PARENT_DEEPER_IN_HIERARCHY));
        }
        else if (!(allowedBacklogs.contains(story.getBacklog()))) {
            messages.add(new StoryTreeIntegrityMessage(story, newParent, StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH));
        }
    }

    private void checkTargetParentInIterationRule(Story story, Story newParent,
            List<StoryTreeIntegrityMessage> messages) {
        if (newParent.getBacklog() instanceof Iteration) {
            messages.add(new StoryTreeIntegrityMessage(story, newParent,
                    StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_ITERATION));
        }
    }

    private MoveStoryNode recurseChangedStoryTreeChildren(Story movedStory, List<StoryTreeIntegrityMessage> messages) {
        List<MoveStoryNode> children = new ArrayList<MoveStoryNode>();
        boolean containsChanges = false;
        
        MoveStoryNode currentStory = new MoveStoryNode();
        currentStory.setStory(movedStory);
        
        for(Story story : movedStory.getChildren()) {
            MoveStoryNode child = recurseChangedStoryTreeChildren(story, messages);
            if(child.isContainsChanges()) {
                containsChanges = true;
            }
            children.add(child);
        }
        currentStory.setChildren(children);
        
        StoryTreeIntegrityMessage message = hasNodeChanged(movedStory, messages);
        if(message != null) {
            containsChanges = true;
            currentStory.setChanged(true);
            currentStory.setMessage(message);
        }
        currentStory.setContainsChanges(containsChanges);
        return currentStory;
    }

    private StoryTreeIntegrityMessage hasNodeChanged(Story movedStory,
            List<StoryTreeIntegrityMessage> messages) {
        for(StoryTreeIntegrityMessage message : messages) {
            if(message.getSource() == movedStory || message.getTarget() == movedStory) {
                return message;
            }
        }
        return null;
    }
    
    public MoveStoryNode generateChangedStoryTree(Story movedStory,
            List<StoryTreeIntegrityMessage> messages) {
        MoveStoryNode node;
        // 1. lookup children
        node = recurseChangedStoryTreeChildren(movedStory, messages);
        // 2. lookup parents
    
        MoveStoryNode lastChanged = node, previous = node;
        for (Story currentParent = movedStory.getParent(); currentParent != null;
            currentParent = currentParent.getParent()) {
            
            MoveStoryNode cnode = new MoveStoryNode();
            cnode.setStory(currentParent);
            
            StoryTreeIntegrityMessage message = hasNodeChanged(currentParent, messages);
            if(message != null) {
                cnode.setChanged(true);
                cnode.setMessage(message);
                lastChanged = cnode;
            }
            cnode.setContainsChanges(true);
            cnode.setChildren(Arrays.asList(previous));
            previous = cnode;
        }
        return lastChanged;
    }

    public boolean canStoryBeMovedToBacklog(Story story, Backlog newBacklog) {
        List<StoryTreeIntegrityMessage> messages = this.checkChangeBacklog(story, newBacklog);
        return messages.isEmpty();
    }
    
    public boolean hasParentStoryConflict(Story story, Backlog newBacklog) {
        /*
         * A conflict exists if the story being moved has a parent story in project backlog
         * and that project backlog is not the backlog where the story is being moved to. 
         */
        for(Story parent = story.getParent(); parent != null; parent = parent.getParent()) {
            if(parent.getBacklog() instanceof Project && parent.getBacklog() != newBacklog) {
                return true;
            }
        }
        return false;
    }

}
