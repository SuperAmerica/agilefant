package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

public interface StoryHierarchyDAO {
    /**
     * Fetch root stories that are attached to the given project or any of the
     * iterations under the project. Root story stands for story that either has
     * no parent story or the parent story is in the product backlog.
     * 
     * @param project id
     * @return list of root stories
     */
    public List<Story> retrieveProjectRootStories(int projectId);

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
     * Story point sum of product leaf stories.
     * 
     * @param project
     * @return
     */
    public long totalLeafStoryPoints(Project project);

    /**
     * Story point sum of done product leaf stories.
     * 
     * @param project
     * @return
     */
    public long totalLeafDoneStoryPoints(Project project);

    /**
     * Story point sum of product root stories.
     * 
     * @param project
     * @return
     */
    public long totalRootStoryPoints(Project project);
    
    /**
     * Retrieve all stories within a given product that do not have 
     * a parent story.
     * 
     * @param product id
     * @return list of product root stories
     */
    public List<Story> retrieveProductRootStories(int productId);
    
    /**
     * Get a rank number for a new root story.
     * <p>
     * Will return the count of the root stories.
     * @param productId
     * @return
     */
    public int getMaximumTreeRank(int productId);
    
    
}
