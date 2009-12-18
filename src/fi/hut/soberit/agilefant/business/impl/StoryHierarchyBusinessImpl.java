package fi.hut.soberit.agilefant.business.impl;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Service("storyHierarchyBusiness")
public class StoryHierarchyBusinessImpl implements StoryHierarchyBusiness {

    @Autowired
    private StoryHierarchyDAO storyHierarchyDAO;

    @Autowired
    private StoryBusiness storyBusiness;

    @Autowired
    private BacklogBusiness backlogBusiness;

    @Transactional(readOnly = true)
    public List<Story> retrieveProjectLeafStories(Project project) {
        return storyHierarchyDAO.retrieveProjectLeafStories(project);
    }

    @Transactional(readOnly = true)
    public List<Story> retrieveProjectRootStories(Project project) {
        return storyHierarchyDAO.retrieveProjectRootStories(project);
    }

    public List<Story> retrieveProductRootStories(Product product) {
        return storyHierarchyDAO.retrieveProductRootStories(product);
    }

    @Transactional
    public void moveUnder(Story story, Story reference) {
        Story oldParent = story.getParent();

        if(oldParent != null) {
            oldParent.getChildren().remove(story);
        }
        reference.getChildren().add(story);
        story.setParent(reference);

        updateBacklogRanks(oldParent);
        updateBacklogRanks(reference);

        updateTreeRanks(reference.getChildren());
        if(oldParent != null) {
            updateTreeRanks(oldParent.getChildren());
        }

    }

    @Transactional
    public void moveAfter(Story story, Story reference) {
        Story oldParent = story.getParent();
        Story parent = reference.getParent();

        LinkedList<Story> tmpList = retrieveChildListAndMoveStory(story,
                oldParent, parent);

        if (tmpList.getLast() != reference) {
            tmpList.add(tmpList.indexOf(reference) + 1, story);
        } else {
            tmpList.addLast(story);
        }

        updateTreeRanks(tmpList);
        if (parent != null) {
            parent.setChildren(tmpList);
            if (parent != oldParent) {
                updateBacklogRanks(parent);
            }
        }

    }

    @Transactional
    public void moveBefore(Story story, Story reference) {
        Story oldParent = story.getParent();
        Story parent = reference.getParent();
        LinkedList<Story> tmpList = retrieveChildListAndMoveStory(story,
                oldParent, parent);

        tmpList.add(tmpList.indexOf(reference), story);

        updateTreeRanks(tmpList);
        if (parent != null) {
            parent.setChildren(tmpList);
            if (parent != oldParent) {
                updateBacklogRanks(parent);
            }
        }
    }

    private LinkedList<Story> retrieveChildListAndMoveStory(Story story,
            Story oldParent, Story parent) {
        LinkedList<Story> tmpList = new LinkedList<Story>();
        if (parent != oldParent) {
            story.setParent(parent);
            if (oldParent != null) {
                oldParent.getChildren().remove(story);
                updateBacklogRanks(oldParent);
                updateTreeRanks(oldParent.getChildren());
            }
        }
        if (parent != null) {
            tmpList.addAll(parent.getChildren());
        } else {
            Product product = backlogBusiness.getParentProduct(story
                    .getBacklog());
            tmpList.addAll(this.retrieveProductRootStories(product));
        }
        if (tmpList.contains(story)) {
            tmpList.remove(story);
        }
        return tmpList;
    }
    
    private void updateBacklogRanks(Story story) {
        if (story != null) {
            storyBusiness.updateStoryRanks(story);
        }

    }

    private void updateTreeRanks(List<Story> tmpList) {
        int currentRank = 0;
        for (Iterator<Story> iter = tmpList.iterator(); iter.hasNext(); currentRank++) {
            Story tmp = iter.next();
            if (tmp.getTreeRank() != currentRank) {
                tmp.setTreeRank(currentRank);
            }
        }
    }
}
