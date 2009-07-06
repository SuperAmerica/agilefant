package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.PersonalLoadBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IntervalLoadContainer;
import fi.hut.soberit.agilefant.transfer.IterationLoadContainer;
import static org.easymock.EasyMock.*;

public class PersonalLoadBusinessTest {

    private PersonalLoadBusinessImpl personalLoadBusiness;
    private UserBusiness userBusiness;
    private TaskDAO taskDAO;
    private StoryDAO storyDAO;
    private User user;
    private Task task1;
    private Task task2;
    private Task task3;
    private Iteration iter;
    private Story story;
    private Map<Integer, Integer> assigneeMap;

    @Before
    public void setUp() {
        personalLoadBusiness = new PersonalLoadBusinessImpl();
        userBusiness = createStrictMock(UserBusiness.class);
        taskDAO = createStrictMock(TaskDAO.class);
        storyDAO = createStrictMock(StoryDAO.class);
        personalLoadBusiness.setStoryDAO(storyDAO);
        personalLoadBusiness.setTaskDAO(taskDAO);
        personalLoadBusiness.setUserBusiness(userBusiness);
        user = new User();

    }

    private void initDataset() {
        task1 = new Task();
        task1.setEffortLeft(new ExactEstimate(500));
        task1.setId(1);
        task2 = new Task();
        task2.setEffortLeft(new ExactEstimate(5000));
        task2.setId(2);
        task3 = new Task();
        task3.setEffortLeft(null);
        task3.setId(3);

        iter = new Iteration();
        iter.setId(1);
        story = new Story();
        story.setId(1);
        story.setBacklog(iter);
        assigneeMap = new HashMap<Integer, Integer>();
        assigneeMap.put(1, 2);
        assigneeMap.put(2, 1);
        assigneeMap.put(3, 2);
    }

    private void replayAll() {
        replay(userBusiness, taskDAO, storyDAO);
    }

    private void verifyAll() {
        verify(userBusiness, taskDAO, storyDAO);
    }

    @Test
    public void testCalculateStoryAssignedTaskLoad() {
        initDataset();
        task1.setStory(story);
        task2.setStory(story);
        task3.setStory(story);
        List<Task> tasks = Arrays.asList(task1, task2, task3);
        Map<Integer, IterationLoadContainer> iterationEffortData = new HashMap<Integer, IterationLoadContainer>();

        expect(taskDAO.getStoryAssignedTasksWithEffortLeft(user, null))
                .andReturn(tasks);
        Capture<Set<Integer>> actualStoryIds = new Capture<Set<Integer>>();
        expect(
                storyDAO.getNumOfResponsiblesByStory(EasyMock
                        .capture(actualStoryIds))).andReturn(assigneeMap);
        replayAll();
        personalLoadBusiness.calculateStoryAssignedTaskLoad(
                iterationEffortData, user, null);
        assertEquals(1, actualStoryIds.getValue().size());
        assertEquals(2750L, iterationEffortData.get(1).getTotalAssignedLoad());
        verifyAll();
    }

    @Test
    public void testCalculateDirectlyAssignedTaskLoad() {
        initDataset();
        task1.setIteration(iter);
        task2.setStory(story);
        task3.setIteration(iter);
        List<Task> iterTasks = Arrays.asList(task1, task3);
        List<Task> storyTasks = Arrays.asList(task2);
        Map<Integer, IterationLoadContainer> iterationEffortData = new HashMap<Integer, IterationLoadContainer>();

        expect(taskDAO.getIterationTasksWithEffortLeft(user, null))
                .andReturn(iterTasks);
        expect(taskDAO.getStoryTasksWithEffortLeft(user, null)).andReturn(
                storyTasks);

        Capture<Set<Integer>> actualStoryIds = new Capture<Set<Integer>>();
        expect(
                taskDAO.getNumOfResponsiblesByTask(EasyMock
                        .capture(actualStoryIds))).andReturn(assigneeMap);
        replayAll();
        personalLoadBusiness.calculateDirectlyAssignedTaskLoad(
                iterationEffortData, user, null);
        assertEquals(3, actualStoryIds.getValue().size());
        assertEquals(5250L, iterationEffortData.get(1).getTotalAssignedLoad());
        verifyAll();
    }

    @Test
    public void testUpdateUserLoadByInterval_iterationStarts() {
        DateTime baseDate = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        DateTime intervalStart = baseDate;
        DateTime intervalEnd = baseDate.plusDays(6);
        DateTime iterationStart = baseDate.plusDays(2);
        DateTime iterationEnd = baseDate.plusDays(9);

        // 3.6 - 10.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        IterationLoadContainer loadContainer = new IterationLoadContainer();
        loadContainer.setIteration(iter);

        // 1.6 - 7.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);

        // total intervals with vacations and weekends
        Interval actualInterval = new Interval(iterationStart, intervalEnd);
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        // iteration and period durations without vacations and weekends
        Duration worktimeInIteration = new Duration(1000 * 3600 * 24 * 5L); // 5
                                                                            // days
        Duration worktimeInPeriod = new Duration(1000 * 3600 * 24 * 3L); // 3
                                                                         // days
        // total assigned effort
        loadContainer.setTotalAssignedLoad(500L);

        expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval))
                .andReturn(worktimeInIteration);
        expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval))
                .andReturn(worktimeInPeriod);

        replayAll();
        personalLoadBusiness.updateUserLoadByInterval(container, loadContainer,
                user);
        verifyAll();
        assertEquals(300L, container.getAssignedLoad());

    }

    @Test
    public void testupdateUserLoadByInterval_iterationEnds() {
        DateTime baseDate = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        DateTime intervalStart = baseDate.plusDays(7);
        DateTime intervalEnd = baseDate.plusDays(11);
        DateTime iterationStart = baseDate;
        DateTime iterationEnd = baseDate.plusDays(9);

        // 1.6 - 9.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        IterationLoadContainer loadContainer = new IterationLoadContainer();
        loadContainer.setIteration(iter);

        // 8.6 - 12.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);

        // total intervals with vacations and weekends
        Interval actualInterval = new Interval(intervalStart, iterationEnd);
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        // iteration and period durations without vacations and weekends
        Duration worktimeInIteration = new Duration(1000 * 3600 * 24 * 5L); // 5
                                                                            // days
        Duration worktimeInPeriod = new Duration(1000 * 3600 * 24 * 2L); // 2
                                                                         // days
        // total assigned effort
        loadContainer.setTotalAssignedLoad(500L);

        expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval))
                .andReturn(worktimeInIteration);
        expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval))
                .andReturn(worktimeInPeriod);

        replayAll();
        personalLoadBusiness.updateUserLoadByInterval(container, loadContainer,
                user);
        verifyAll();
        assertEquals(200L, container.getAssignedLoad());
    }

    @Test
    public void testupdateUserLoadByInterval_iterationInBetween() {
        DateTime baseDate = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        DateTime intervalStart = baseDate;
        DateTime intervalEnd = baseDate.plusDays(4);
        DateTime iterationStart = baseDate.plusDays(1);
        DateTime iterationEnd = baseDate.plusDays(3);

        // 2.6 - 4.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        IterationLoadContainer loadContainer = new IterationLoadContainer();
        loadContainer.setIteration(iter);

        // 1.6 - 5.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);

        // total intervals with vacations and weekends
        Interval actualInterval = new Interval(iterationStart, iterationEnd);
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        // iteration and period durations without vacations and weekends
        Duration worktimeInIteration = new Duration(1000 * 3600 * 24 * 3L); // 3
                                                                            // days
        Duration worktimeInPeriod = new Duration(1000 * 3600 * 24 * 3L); // 3
                                                                         // days
        // total assigned effort
        loadContainer.setTotalAssignedLoad(500L);

        expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval))
                .andReturn(worktimeInIteration);
        expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval))
                .andReturn(worktimeInPeriod);

        replayAll();
        personalLoadBusiness.updateUserLoadByInterval(container, loadContainer,
                user);
        verifyAll();
        assertEquals(500L, container.getAssignedLoad());
    }

    @Test
    public void testupdateUserLoadByInterval_iterationNotOngoing() {
        DateTime baseDate = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        DateTime intervalStart = baseDate;
        DateTime intervalEnd = baseDate.plusDays(4);
        DateTime iterationStart = baseDate.plusDays(7);
        DateTime iterationEnd = baseDate.plusDays(15);

        // 1.6 - 4.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        IterationLoadContainer loadContainer = new IterationLoadContainer();
        loadContainer.setIteration(iter);
        loadContainer.setTotalAssignedLoad(500L);
        // 8.6 - 16.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);
        personalLoadBusiness.updateUserLoadByInterval(container, loadContainer,
                user);
        assertEquals(0L, container.getAssignedLoad());

    }

    @Test
    public void testInitializeLoadContainers() {
        DateTime start = new DateTime(2009, 6, 1, 0, 0, 0, 0);
        DateTime end = start.plusDays(29);
        Period period = new Period(0, 0, 1, 0, 0, 0, 0, 0);
        List<IntervalLoadContainer> actual = this.personalLoadBusiness
                .initializeLoadContainers(user, start, end, period);
        assertEquals(5, actual.size());
    }
}
