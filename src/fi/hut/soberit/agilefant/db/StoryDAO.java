package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public interface StoryDAO extends GenericDAO<Story> {

    int countByCreator(User user);

    public List<Story> getStoriesByBacklog(Backlog backlog);
    
    /**
     * Get the story's tasks.
     * @param story
     * @return
     */
    public Collection<Task> getStoryTasks(Story story);

    StoryMetrics calculateMetrics(int storyId);

    StoryMetrics calculateMetricsWithoutStory(int iterationId);
    
}
