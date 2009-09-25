package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HistoryRowTO;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public interface StoryBusiness extends GenericBusiness<Story> {

    /**
     * Copies the dataItem's data to the persisted <code>Story</code> object.
     * @return the newly persisted story
     */
    Story store(Integer storyId, Story dataItem, Integer backlogId, Set<Integer> responsibleIds) throws ObjectNotFoundException, IllegalArgumentException;
    
    /**
     * Create and persist a new story.
     * @param dataItem the story's data
     * @param backlogId the story's parent backlog's id
     * @param responsibleIds the id's of the responsible users
     * @return the newly persisted story
     * @throws IllegalArgumentException TODO
     * @throws ObjectNotFoundException TODO
     */
    Story create(Story dataItem, Integer backlogId, Set<Integer> responsibleIds) throws IllegalArgumentException, ObjectNotFoundException;
  
    public List<Story> getStoriesByBacklog(Backlog backlog);

    /**
     * Get the story's tasks as <code>StoryData</code>.
     * <p>
     * If <code>story</code> is null, return tasks without story.
     */
    public Collection<Task> getStoryContents(Story story, Iteration iteration);
    public Collection<Task> getStoryContents(int storyId, int iterationId);

    StoryMetrics calculateMetrics(int storyId);

    StoryMetrics calculateMetricsWithoutStory(int iterationId);

    StoryMetrics calculateMetrics(Story story);
    
    public Collection<User> getStorysProjectResponsibles(Story story);
    
    /**
     * Moves a story to another backlog.
     * @param moveTasks whether the tasks should be moved with the story.
     */
    public void moveStoryToBacklog(Story story, Backlog backlog);

    public int getStoryPointSumByBacklog(Backlog backlog);
    
    public List<HistoryRowTO> retrieveStoryHistory(int id);
    
    
    /* RANKING */   
    /**
     * Rank the story to be under the given upperStory.
     * 
     * @param upperStory the story under which the other story should be ranked. null if topmost.
     * 
     * @throws IllegalArgumentException if the given story was null
     */
    public Story rankUnderStory(Story story, Story upperStory) throws IllegalArgumentException;
    
    /**
     * Ranks the story to bottom most item under given parent.
     * @throws IllegalArgumentException if story was null
     */
    public Story rankToBottom(Story story, Integer parentBacklogId) throws IllegalArgumentException;
    
    /**
     * Moves the story to a new backlog and calls rankUnderStory.
     */
    public Story rankAndMove(Story story, Story upperStory, Backlog newParent);
    
    public void storeBatch(Collection<Story> stories);
    
    public Collection<Story> retrieveMultiple(Collection<Story> stories);
}
