package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;


public interface StoryHierarchyBusiness {

    
    /**
     * Fetch root stories that are attached to the given project or any of the
     * iterations under the project. Root story stands for story that either has
     * no parent story or the parent story is in the product backlog.
     * 
     * @param project
     * @return list of root stories
     */
    public List<Story> retrieveProjectRootStories(Project project);

    /***
     * Fetch leaf stories that are attached to the given project or any of the
     * iterations under the project. Leaf story means a story which has no child
     * stories.
     * 
     * @param project
     * @return list of leaf stories
     */
    public List<Story> retrieveProjectLeafStories(Project project);

}
