package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.DailySpentEffort;

/**
 * Business interface for handling functionality related to Hour Entries
 * 
 * @author kjniiran
 * @author Pasi Pekkanen
 * 
 */
public interface HourEntryBusiness extends GenericBusiness<HourEntry> {

    /**
     * Create one effort entry for each given user under the given story.
     */
    public void logStoryEffort(int storyId, HourEntry effortEntry,
            Set<Integer> userIds);

    /**
     * Create one effort entry for each given user under the given task.
     */
    public void logTaskEffort(int taskId, HourEntry effortEntry,
            Set<Integer> userIds);

    /**
     * Create one effort entry for each given user under the given backlog.
     */
    public void logBacklogEffort(int backlogId, HourEntry effortEntry,
            Set<Integer> userIds);

    /**
     * Gets all task, story and backlog hour entries for an iteration and
     * calculates their sum.
     */
    long calculateSumOfIterationsHourEntries(Iteration iteration);

    List<BacklogHourEntry> retrieveByParent(Backlog item);

    long calculateSumByUserAndTimeInterval(int userId, DateTime startDate,
            DateTime endDate);

    long calculateSum(Collection<? extends HourEntry> hourEntries);

    public List<HourEntry> getEntriesByUserAndTimeInterval(int userId,
            DateTime startDate, DateTime endDate);

    public List<HourEntry> getEntriesByUserAndDay(LocalDate day, int userId);

    public List<DailySpentEffort> getDailySpentEffortByWeek(LocalDate week,
            int userId);

    public List<DailySpentEffort> getDailySpentEffortByInterval(DateTime start,
            DateTime end, int userId);

    public long calculateWeekSum(LocalDate week, int userId);

    List<HourEntry> retrieveBacklogHourEntries(int backlogId,
            boolean limited);

    List<HourEntry> retrieveStoryHourEntries(int storyId,
            boolean limited);

    List<HourEntry> retrieveTaskHourEntries(int taskId,
            boolean limited);

}
