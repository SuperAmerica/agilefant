package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IntervalLoadContainer;
import fi.hut.soberit.agilefant.transfer.IterationLoadContainer;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;
import static org.easymock.EasyMock.*;

public class PersonalLoadBusinessTest {

    private PersonalLoadBusinessImpl personalLoadBusiness;
    private UserBusiness userBusiness;
    private TaskDAO taskDAO;
    private StoryDAO storyDAO;
    private IterationDAO iterationDAO;
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
        iterationDAO = createStrictMock(IterationDAO.class);
        personalLoadBusiness.setStoryDAO(storyDAO);
        personalLoadBusiness.setTaskDAO(taskDAO);
        personalLoadBusiness.setUserBusiness(userBusiness);
        personalLoadBusiness.setIterationDAO(iterationDAO);
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
        replay(userBusiness, taskDAO, storyDAO, iterationDAO);
    }

    private void verifyAll() {
        verify(userBusiness, taskDAO, storyDAO, iterationDAO);
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
    
    @Test
    public void testLoadIterationDetails() {
        UnassignedLoadTO transfer1 = new UnassignedLoadTO(null, 1, (short)1);
        UnassignedLoadTO transfer2 = new UnassignedLoadTO(null, 2, (short)1);
        
        Iteration iter1 = new Iteration();
        iter1.setId(1);
        Iteration iter2 = new Iteration();
        iter2.setId(2);
        
        Map<Integer, Integer> availabilitySums = new HashMap<Integer, Integer>();
        availabilitySums.put(1, 5);
        availabilitySums.put(2, 42);
 
        Capture<Set<Integer>> iterationIds = new Capture<Set<Integer>>();
        Capture<Set<Integer>> iterationIdsAvailSum = new Capture<Set<Integer>>();
 
        expect(iterationDAO.retrieveIterationsByIds(EasyMock.capture(iterationIds))).andReturn(Arrays.asList(iter1, iter2));
        expect(iterationDAO.getTotalAvailability(EasyMock.capture(iterationIdsAvailSum))).andReturn(availabilitySums);
        
        replayAll();
        personalLoadBusiness.loadIterationDetails(Arrays.asList(transfer1, transfer2));
        assertEquals(2, iterationIds.getValue().size());
        assertEquals(2, iterationIdsAvailSum.getValue().size());
        assertEquals(iter1, transfer1.iteration);
        assertEquals(iter2, transfer2.iteration);
        assertEquals(5, transfer1.availabilitySum);
        assertEquals(42, transfer2.availabilitySum);
        verifyAll();   
    }
    
    @Test
    public void testCalculateUnassignedTaskLoad() {
        Interval interval = new Interval(500,600);
        UnassignedLoadTO transfer1 = new UnassignedLoadTO(new ExactEstimate(1000), 1, (short)1);
        UnassignedLoadTO transfer2 = new UnassignedLoadTO(new ExactEstimate(8000), 2, (short)1);
        
        Iteration iter1 = new Iteration();
        iter1.setId(1);
        Iteration iter2 = new Iteration();
        iter2.setId(2);
        
        Map<Integer, Integer> availabilitySums = new HashMap<Integer, Integer>();
        availabilitySums.put(1, 10);
        availabilitySums.put(2, 100);
        
        Map<Integer, IterationLoadContainer> dataPerIteration = new HashMap<Integer, IterationLoadContainer>();

        Set<Integer> iterationIds = new HashSet<Integer>(Arrays.asList(1,2));
        
        expect(taskDAO.getUnassignedIterationTasksWithEffortLeft(user, interval)).andReturn(Arrays.asList(transfer1));
        expect(taskDAO.getUnassignedStoryTasksWithEffortLeft(user, interval)).andReturn(Arrays.asList(transfer2));

        expect(iterationDAO.retrieveIterationsByIds(iterationIds)).andReturn(Arrays.asList(iter1, iter2));
        expect(iterationDAO.getTotalAvailability(iterationIds)).andReturn(availabilitySums);
        
        replayAll();
        personalLoadBusiness.calculateUnassignedTaskLoad(dataPerIteration, user, interval);
        assertEquals(iter1, dataPerIteration.get(1).getIteration());
        assertEquals(iter2, dataPerIteration.get(2).getIteration());
        assertEquals(100L, dataPerIteration.get(1).getTotalUnassignedLoad());
        assertEquals(80L, dataPerIteration.get(2).getTotalUnassignedLoad());
        verifyAll();   
    }
}
