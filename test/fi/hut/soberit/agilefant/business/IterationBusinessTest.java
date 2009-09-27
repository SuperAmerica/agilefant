package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.IterationBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.IterationTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.Pair;

public class IterationBusinessTest {

    IterationBusinessImpl iterationBusiness = new IterationBusinessImpl();
    TransferObjectBusiness transferObjectBusiness;
    StoryBusiness storyBusiness;
    HourEntryBusiness hourEntryBusiness;
    IterationDAO iterationDAO;
    IterationHistoryEntryDAO iterationHistoryEntryDAO;
    IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    BacklogBusiness backlogBusiness;
    AssignmentBusiness assignmentBusiness;
    BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    
    Iteration iteration;
    Project project;
    Set<StoryTO> storiesList;
    Set<Task> tasksWithoutStoryList;
    Set<TaskTO> tasksTOsWithoutStoryList;
    IterationTO expectedIterationData;
    Task task;
    TaskTO taskTO;

    @Before
    public void setUp_Dependencies() {
        iterationDAO = createMock(IterationDAO.class);
        iterationBusiness.setIterationDAO(iterationDAO);

        storyBusiness = createMock(StoryBusiness.class);
        iterationBusiness.setStoryBusiness(storyBusiness);

        hourEntryBusiness = createMock(HourEntryBusiness.class);
        iterationBusiness.setHourEntryBusiness(hourEntryBusiness);

        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        iterationBusiness.setTransferObjectBusiness(transferObjectBusiness);

        iterationHistoryEntryBusiness = createMock(IterationHistoryEntryBusiness.class);
        iterationBusiness
                .setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);

        iterationHistoryEntryDAO = createMock(IterationHistoryEntryDAO.class);
        iterationBusiness.setIterationHistoryEntryDAO(iterationHistoryEntryDAO);

        backlogBusiness = createMock(BacklogBusiness.class);
        iterationBusiness.setBacklogBusiness(backlogBusiness);

        assignmentBusiness = createMock(AssignmentBusiness.class);
        iterationBusiness.setAssignmentBusiness(assignmentBusiness);
        
        backlogHistoryEntryBusiness = createMock(BacklogHistoryEntryBusiness.class);
        iterationBusiness.setBacklogHistoryEntryBusiness(backlogHistoryEntryBusiness);
    }

    @Before
    public void setUp() {
        iteration = new Iteration();
        iteration.setId(123);

        project = new Project();
        project.setId(313);

        Story story1 = new Story();
        story1.setId(666);
        Story story2 = new Story();
        story2.setId(667);
        StoryTO storyTO1 = new StoryTO(story1);
        StoryTO storyTO2 = new StoryTO(story2);
        storiesList = new HashSet<StoryTO>(Arrays.asList(storyTO1, storyTO2));

        task = new Task();
        task.setId(1254);
        taskTO = new TaskTO(task);
        tasksWithoutStoryList = new HashSet<Task>(Arrays.asList(task));
        tasksTOsWithoutStoryList = new HashSet<TaskTO>(Arrays.asList(taskTO));

        expectedIterationData = new IterationTO(new Iteration());
        expectedIterationData.setStories(new HashSet<Story>());
        expectedIterationData.getStories().addAll(storiesList);
        expectedIterationData.setTasks(new HashSet<Task>());
        expectedIterationData.getTasks().addAll(
                tasksTOsWithoutStoryList);
    }

    private void verifyAll() {
        verify(iterationDAO, transferObjectBusiness,
                storyBusiness, hourEntryBusiness, backlogBusiness,
                iterationHistoryEntryBusiness, iterationHistoryEntryDAO,
                assignmentBusiness, backlogHistoryEntryBusiness);
    }

    private void replayAll() {
        replay(iterationDAO, transferObjectBusiness,
                storyBusiness, hourEntryBusiness, backlogBusiness,
                iterationHistoryEntryBusiness, iterationHistoryEntryDAO,
                assignmentBusiness, backlogHistoryEntryBusiness);
    }

    @Test
    public void testGetIterationContents_doNotExcludeTasks() {
        expect(iterationDAO.retrieveDeep(iteration.getId())).andReturn(iteration);

        expect(transferObjectBusiness.constructBacklogData(
                        iteration)).andReturn(storiesList);
        for (StoryTO storyTO : storiesList) {
            expect(storyBusiness.calculateMetrics(storyTO)).andReturn(null);
        }
        expect(iterationDAO.getTasksWithoutStoryForIteration(iteration))
                .andReturn(tasksWithoutStoryList);

        expect(transferObjectBusiness.constructTaskTO(task))
                .andReturn(taskTO);

        expect(hourEntryBusiness.calculateSum(taskTO.getHourEntries()))
                .andReturn(Long.valueOf(0));

        replayAll();

        IterationTO actualIterationData = iterationBusiness
                .getIterationContents(iteration.getId());

        assertEquals(expectedIterationData.getStories(), actualIterationData
                .getStories());
        assertEquals(expectedIterationData.getTasks(),
                actualIterationData.getTasks());

        assertEquals(1, actualIterationData.getTasks().size());
        assertEquals(2, actualIterationData.getStories().size());
        assertEquals(storiesList, actualIterationData.getStories());
        
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetIterationContents_nullBacklog() {
        expect(iterationDAO.retrieveDeep(0)).andReturn(null);
        replay(iterationDAO);
        assertNull(iterationBusiness.getIterationContents(0));
        verify(iterationDAO);
    }

    @Test
    public void testGetIterationMetrics() {
        IterationHistoryEntry latestEntry = new IterationHistoryEntry();
        latestEntry.setEffortLeftSum(112);
        latestEntry.setOriginalEstimateSum(257);

        BacklogHourEntry he1 = new BacklogHourEntry();
        BacklogHourEntry he2 = new BacklogHourEntry();
        he1.setMinutesSpent(39);
        he2.setMinutesSpent(88);

        List<BacklogHourEntry> hourEntries = new ArrayList<BacklogHourEntry>();
        hourEntries.addAll(Arrays.asList(he1, he2));

        int expectedStoryPoints = 68;
        long expectedSpentEffort = 127;
        Integer expectedPercentDoneTasks = 50;
        Integer expectedPercentDoneStories = 50;

        expect(iterationHistoryEntryBusiness.retrieveLatest(iteration))
                .andReturn(latestEntry);
        expect(storyBusiness.getStoryPointSumByBacklog(iteration)).andReturn(
                expectedStoryPoints);
        expect(hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration))
                .andReturn(expectedSpentEffort);
        expect(iterationDAO.getCountOfDoneAndAllTasks(iteration)).andReturn(
                Pair.create(2, 4));
        expect(iterationDAO.getCountOfDoneAndAllStories(iteration)).andReturn(
                Pair.create(1, 2));
        expect(
                iterationHistoryEntryDAO.retrieveByDate(iteration.getId(),
                        new LocalDate().minusDays(1))).andReturn(null);

        replayAll();

        IterationMetrics actualMetrics = iterationBusiness
                .getIterationMetrics(iteration);

        assertNotNull(actualMetrics);
        assertEquals(latestEntry.getEffortLeftSum(), actualMetrics
                .getEffortLeft().getMinorUnits().longValue());
        assertEquals(latestEntry.getOriginalEstimateSum(), actualMetrics
                .getOriginalEstimate().getMinorUnits().longValue());
        assertEquals(expectedStoryPoints, actualMetrics.getStoryPoints()
                .intValue());
        assertEquals(expectedSpentEffort, actualMetrics.getSpentEffort()
                .getMinorUnits().longValue());
        assertEquals(expectedPercentDoneTasks, actualMetrics
                .getPercentDoneTasks());
        assertEquals(expectedPercentDoneStories, actualMetrics
                .getPercentDoneStories());

        verifyAll();
    }

    @Test
    public void testGetIterationMetricsZeroTotals() {
        expect(iterationHistoryEntryBusiness.retrieveLatest(iteration))
                .andReturn(null);
        expect(iterationDAO.getCountOfDoneAndAllTasks(iteration)).andReturn(
                Pair.create(0, 0));
        expect(iterationDAO.getCountOfDoneAndAllStories(iteration)).andReturn(
                Pair.create(0, 0));

        expect(storyBusiness.getStoryPointSumByBacklog(iteration)).andReturn(0);
        expect(hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration))
                .andReturn(0L);
        expect(
                iterationHistoryEntryDAO.retrieveByDate(iteration.getId(),
                        new LocalDate().minusDays(1))).andReturn(null);

        replayAll();

        IterationMetrics actualMetrics = iterationBusiness
                .getIterationMetrics(iteration);

        assertEquals(0, actualMetrics.getPercentDoneStories().intValue());
        assertEquals(0, actualMetrics.getPercentDoneStories().intValue());

        verifyAll();
    }

    @Test
    public void testGetIterationMetrics_nullLatestHistoryEntry() {
        expect(iterationHistoryEntryBusiness.retrieveLatest(iteration))
                .andReturn(null);
        expect(iterationDAO.getCountOfDoneAndAllTasks(iteration)).andReturn(
                Pair.create(2, 4));
        expect(iterationDAO.getCountOfDoneAndAllStories(iteration)).andReturn(
                Pair.create(1, 3));
        expect(
                iterationHistoryEntryDAO.retrieveByDate(iteration.getId(),
                        new LocalDate().minusDays(1))).andReturn(null);

        expect(storyBusiness.getStoryPointSumByBacklog(iteration)).andReturn(0);
        expect(hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration))
                .andReturn(0L);

        replayAll();

        IterationMetrics actualMetrics = iterationBusiness
                .getIterationMetrics(iteration);

        assertEquals(0L, actualMetrics.getEffortLeft().getMinorUnits()
                .longValue());
        assertEquals(0L, actualMetrics.getOriginalEstimate().getMinorUnits()
                .longValue());

        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIterationMetrics_nullIteration() {
        iterationBusiness.getIterationMetrics(null);
    }

    @Test
    public void testStoreIteration() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(3);
        Iteration iter = new Iteration();
        iter.setStartDate(start);
        iter.setEndDate(end);
        iter.setParent(project);
        iteration.setParent(project);
        iter.setBacklogSize(new ExactEstimate(120L));
        iter.setName("foo");
        iter.setDescription("quu");
        iter.setBaselineLoad(new ExactEstimate(100L));

        expect(backlogBusiness.retrieve(11)).andReturn(project);
        expect(iterationDAO.get(10)).andReturn(iteration);
        iterationDAO.store(iteration);

        replayAll();

        Iteration actual = this.iterationBusiness.store(10, 11, iter);
        assertEquals(iter.getStartDate(), actual.getStartDate());
        assertEquals(iter.getEndDate(), actual.getEndDate());
        assertEquals(iter.getBacklogSize(), actual.getBacklogSize());
        assertEquals(iter.getBaselineLoad(), actual.getBaselineLoad());
        assertEquals(iter.getName(), actual.getName());
        assertEquals(iter.getDescription(), actual.getDescription());
        verifyAll();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreIteration_iterationParent() {
        expect(backlogBusiness.retrieve(11)).andReturn(iteration);
        replayAll();
        this.iterationBusiness.store(10, 11, this.iteration);
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testStoreIteration_nullParent() {
        expect(backlogBusiness.retrieve(11)).andThrow(new ObjectNotFoundException());
        replayAll();
        this.iterationBusiness.store(10, 11, this.iteration);
        verifyAll();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStoreIteration_invalidInterval() {
        DateTime start = new DateTime();
        DateTime end = start.minusDays(3);
        iteration.setStartDate(start);
        iteration.setEndDate(end);

        expect(backlogBusiness.retrieve(12)).andReturn(project);
        replayAll();
        this.iterationBusiness.store(11, 12, iteration);
        verifyAll();
    }

    @Test
    public void testCreateIteration() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(3);
        Iteration iter = new Iteration();
        iter.setStartDate(start);
        iter.setEndDate(end);
        iter.setParent(project);
        iteration.setParent(project);
        iter.setBacklogSize(new ExactEstimate(120L));
        iter.setName("foo");
        iter.setDescription("quu");
        iter.setBaselineLoad(new ExactEstimate(100L));
        User user = new User();
        user.setId(1);
        Assignment projectAssignment = new Assignment(user, project);
        project.getAssignments().add(projectAssignment);
        
        Capture<Set<Integer>> userIdCapture = new Capture<Set<Integer>>();
        expect(backlogBusiness.retrieve(11)).andReturn(project);
        expect(iterationDAO.create(EasyMock.isA(Iteration.class))).andReturn(new Integer(16));
        expect(iterationDAO.get(16)).andReturn(iteration);
        expect(assignmentBusiness.addMultiple(EasyMock.eq(iteration), 
                EasyMock.capture(userIdCapture), EasyMock.eq(SignedExactEstimate.ZERO), 
                EasyMock.eq(100))).andReturn(new HashSet<Assignment>(Arrays.asList(projectAssignment)));
        replayAll();
        
        this.iterationBusiness.store(0, 11, iter);
        assertEquals(1, userIdCapture.getValue().size());
        assertTrue(userIdCapture.getValue().contains(1));
        verifyAll();
    }

    @Test
    public void testCreateIteration_noProjectAssigments() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(3);
        Iteration iter = new Iteration();
        iter.setStartDate(start);
        iter.setEndDate(end);
        iter.setParent(project);
        iteration.setParent(project);        
        expect(backlogBusiness.retrieve(11)).andReturn(project);
        expect(iterationDAO.create(EasyMock.isA(Iteration.class))).andReturn(new Integer(16));
        expect(iterationDAO.get(16)).andReturn(iteration);
        replayAll();
        this.iterationBusiness.store(0, 11, iter);
        verifyAll();
    }
    @Test
    public void testMoveTo() {
        Project newParent = new Project();
        newParent.setId(911);
        iteration.setParent(project);
        iterationDAO.store(iteration);
        backlogHistoryEntryBusiness.updateHistory(911);
        backlogHistoryEntryBusiness.updateHistory(313);
        replayAll();
        this.iterationBusiness.moveTo(iteration, newParent);
        verifyAll();
    }
}
