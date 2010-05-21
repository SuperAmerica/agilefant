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
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.ChildHandlingChoice;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

public interface StoryBusiness extends GenericBusiness<Story> {

    /**
     * Copies the dataItem's data to the persisted <code>Story</code> object.
     * @param tasksToDone TODO
     * 
     * @return the newly persisted story
     */
    Story store(Integer storyId, Story dataItem, Integer backlogId,
            Set<Integer> responsibleIds, boolean tasksToDone) throws ObjectNotFoundException,
            IllegalArgumentException;

    /**
     * Create and persist a new story.
     * 
     * @param dataItem
     *            the story's data
     * @param backlogId
     *            the story's parent backlog's id
     * @param responsibleIds
     *            the id's of the responsible users
     * @return the newly persisted story
     * @throws IllegalArgumentException
     *             TODO
     * @throws ObjectNotFoundException
     *             TODO
     */
    Story create(Story dataItem, Integer backlogId, Set<Integer> responsibleIds)
            throws IllegalArgumentException, ObjectNotFoundException;

    int create(Story story);

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
     * 
     * @param moveTasks
     *            whether the tasks should be moved with the story.
     */
    public void moveStoryToBacklog(Story story, Backlog backlog);

    public int getStoryPointSumByBacklog(Backlog backlog);

    public List<HistoryRowTO> retrieveStoryHistory(int id);

    /**
     * Rank story under the give upperStory.
     */
    public Story rankStoryUnder(final Story story, final Story upperStory,
            Backlog backlog);

    /**
     * Rank story over the given lowerStory.
     */
    public Story rankStoryOver(final Story story, final Story lowerStory,
            Backlog backlog);

    public Story updateStoryRanks(Story story);

    public void storeBatch(Collection<Story> stories);

    public Collection<Story> retrieveMultiple(Collection<Story> stories);

    public StoryTO retrieveStoryWithMetrics(int storyId);

    void delete(Story story, TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice,
            ChildHandlingChoice childHandlingChoice);

    void deleteAndUpdateHistory(int id, TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice,
            ChildHandlingChoice childHandlingChoice);

    void forceDelete(Story story);
    
    public Story createStoryUnder(int referenceStoryId, Story data,
            Set<Integer> responsibleIds);

    public Story createStorySibling(int referenceStoryId, Story data,
            Set<Integer> responsibleIds);
    
    public void moveSingleStoryToBacklog(Story story, Backlog backlog);
    
    public void moveStoryAndChildren(Story story, Backlog backlog);
}
