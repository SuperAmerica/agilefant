package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryFilterBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.StoryFilters;

@Service("storyHierarchyBusiness")
public class StoryHierarchyBusinessImpl implements StoryHierarchyBusiness {

    @Autowired
    private StoryHierarchyDAO storyHierarchyDAO;

    @Autowired
    private StoryBusiness storyBusiness;

    @Autowired
    private BacklogBusiness backlogBusiness;

    @Autowired
    private StoryFilterBusiness storyFilterBusiness;

    @Transactional(readOnly = true)
    public List<Story> retrieveProjectLeafStories(Project project) {
        return storyHierarchyDAO.retrieveProjectLeafStories(project);
    }

    @Transactional
    public void moveUnder(Story story, Story reference) {
        Story oldParent = story.getParent();

        if (oldParent != null) {
            oldParent.getChildren().remove(story);
        }
        reference.getChildren().add(story);
        story.setParent(reference);

        updateBacklogRanks(oldParent);
        updateBacklogRanks(reference);

        updateTreeRanks(reference.getChildren());
        if (oldParent != null) {
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
            tmpList.addAll(this.retrieveProductRootStories(product.getId(),
                    null));
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

    @Transactional(readOnly = true)
    public List<Story> retrieveProductRootStories(int productId,
            StoryFilters storyFilters) {
        List<Story> stories = storyHierarchyDAO
                .retrieveProductRootStories(productId);
        if (storyFilters != null) {
            return storyFilterBusiness.filterStories(stories, storyFilters);
        } else {
            return stories;
        }
    }

    @Transactional(readOnly = true)
    public List<Story> retrieveProjectRootStories(int projectId,
            StoryFilters storyFilters) {
        List<Story> stories = storyHierarchyDAO
                .retrieveProjectRootStories(projectId);
        if (storyFilters != null) {
            stories = storyFilterBusiness.filterStories(stories, storyFilters);
        }
        stories = replaceStoryNodesWithRoots(stories);
        return stories;
    }

    public List<Story> replaceStoryNodesWithRoots(List<Story> stories) {
        List<Story> results = new ArrayList<Story>(stories.size());
        for (Story story : stories) {
            Story result = story;
            while (result.getParent() != null) {
                StoryTO to = new StoryTO(result.getParent());
                to.setChildren(Arrays.asList(result));
                result = to;
            }
            results.add(result);
        }
        return results;
    }

}
