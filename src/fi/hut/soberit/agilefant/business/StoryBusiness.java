package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public interface StoryBusiness extends GenericBusiness<Story> {

    Story store(int storyId, int backlogId, Story dataItem,
            Set<Integer> responsibles, int priority) throws ObjectNotFoundException;

    public void remove(int storyId) throws ObjectNotFoundException;
    
    public List<Story> getStoriesByBacklog(Backlog backlog);

    /**
     * Get the story's tasks as <code>StoryData</code>.
     * <p>
     * If <code>story</code> is null, return tasks without story.
     */
    public Collection<Task> getStoryContents(Story story, Iteration iteration);
    public Collection<Task> getStoryContents(int storyId, int iterationId);
    
    public Collection<ResponsibleContainer> getStoryResponsibles(Story story);

    StoryMetrics calculateMetrics(int storyId);

    StoryMetrics calculateMetricsWithoutStory(int iterationId);

    StoryMetrics calculateMetrics(Story story);
    
    /**
     * Moves a story to another iteration.
     * @param moveTasks whether the tasks should be moved with the story.
     */
    public void attachStoryToIteration(int storyId, int iterationId, boolean moveTasks);

    public int getStoryPointSumByBacklog(Backlog backlog);
}
