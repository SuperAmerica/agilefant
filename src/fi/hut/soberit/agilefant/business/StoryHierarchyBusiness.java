package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.StoryTreeBranchMetrics;
import fi.hut.soberit.agilefant.util.StoryFilters;


public interface StoryHierarchyBusiness {

    
    /**
     * Fetch root stories that are attached to the given project or any of the
     * iterations under the project. Root story stands for story that either has
     * no parent story or the parent story is in the product backlog.
     * 
     * @param project id
     * @param storyFilters story filters (or null if no filtering is to be done)
     * @return list of root stories
     */
    public List<Story> retrieveProjectRootStories(int projectId, StoryFilters storyFilters);

    /***
     * Fetch leaf stories that are attached to the given project or any of the
     * iterations under the project. Leaf story means a story which has no child
     * stories.
     * 
     * @param project
     * @return list of leaf stories
     */
    public List<Story> retrieveProjectLeafStories(Project project);
    
    /**
     * Retrieve all stories that are in the given product and 
     * which have no parent story.
     * 
     * @param product id
     * @param storyFilters story filters (or null if no filtering is to be done)
     * @return
     */
    public List<Story> retrieveProductRootStories(int productId, StoryFilters storyFilters);
    
    public void moveUnder(Story story, Story refernece);
    
    public void moveAfter(Story story, Story reference);
    
    public void moveBefore(Story story, Story reference);
    
    public void moveToBottom(Story story);
    
    /**
     * Fixes the indexes of the treeRank-field.
     * Call this e.g. when deleting a story
     * @param story the story, whose children are to be reordered
     */
    public void updateChildrenTreeRanks(Story story);
    
    /**
     * Recurses hierarchy to topmost level.
     * <p>
     * Will not include any children except the straight hierarchy
     * from the given story to it's topmost ancestor.
     * @param story
     * @return
     */
    public StoryTO recurseHierarchy(Story story);

    public StoryTreeBranchMetrics calculateStoryTreeMetrics(Story story);
}
