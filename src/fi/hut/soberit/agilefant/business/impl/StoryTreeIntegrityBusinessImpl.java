package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
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
            checkChildBacklogRule(story, newBacklog, messages, allowed);
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

    private void checkChildBacklogRule(Story parent, Backlog newBacklog,
            List<StoryTreeIntegrityMessage> messages, Set<Backlog> allowedBacklogs) {
        
        for (Story child : parent.getChildren()) {
            
            if (!allowedBacklogs.contains(child.getBacklog())) {
                messages.add(new StoryTreeIntegrityMessage(child, null, "story.constraint.childInWrongBranch"));
            }
            
            checkChildBacklogRule(child, newBacklog, messages, allowedBacklogs);
        }
        
    }
    
    private void checkMoveToIterationRule(Story story, Backlog newBacklog,
            List<StoryTreeIntegrityMessage> messages) {
        if (newBacklog instanceof Iteration) {
            messages.add(new StoryTreeIntegrityMessage(story, null,
                    "story.constraint.moveToIterationHasChildren"));
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
                        "story.constraint.parentDeeperInHierarchy"));
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
                        "story.constraint.parentInWrongBranch"));
           }
        }
        
    }
    
    
    /** {@inheritDoc} */
    public List<StoryTreeIntegrityMessage> checkChangeParentStory(
            Story story, Story newParent) {
        return null;
    }

}
