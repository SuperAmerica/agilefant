package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;

public interface HourEntryDAO extends GenericDAO<HourEntry> {

    long calculateSumByUserAndTimeInterval(User user, DateTime startDate,
            DateTime endDate);

    long calculateSumByStory(int storyId);

    long calculateSumFromTasksWithoutStory(int iterationId);
    
    /**
     * Recursive hour entry lookup. Will search from the given backlogs and all of their sub backlogs.
     * 
     * @param backlogIds Set of backlog ids. If argument is null, method will return an empty list.
     * @param startDate Beginning (or null) of the search interval.
     * @param endDate End (or null) of the search interval.
     * @param userIds Set of user ids (or null).
     * @return List of matched hour entries.
     */
    public List<BacklogHourEntry> getBacklogHourEntriesByFilter(
            Set<Integer> backlogIds, DateTime startDate, DateTime endDate, Set<Integer> userIds);

    /**
     * @see getBacklogHourEntriesByFilter
     */
    public List<TaskHourEntry> getTaskHourEntriesByFilter(Set<Integer> backlogIds,
            DateTime startDate, DateTime endDate, Set<Integer> userIds);

    /**
     * @see getBacklogHourEntriesByFilter
     */
    public List<StoryHourEntry> getStoryHourEntriesByFilter(Set<Integer> backlogIds,
            DateTime startDate, DateTime endDate, Set<Integer> userIds);
    
    /**
     * Gets the iterations backlog, story and task hour entries and
     * calculates the sum of their spent time.
     */
    public long calculateIterationHourEntriesSum(int iterationId);
}
