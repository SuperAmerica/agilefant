package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.easymock.Capture;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.IterationBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IterationDataContainer;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.Pair;

public class IterationBusinessTest {

    IterationBusinessImpl iterationBusiness = new IterationBusinessImpl();
    TransferObjectBusiness transferObjectBusiness;
    ProjectBusiness projectBusiness;
    StoryBusiness storyBusiness;
    HourEntryBusiness hourEntryBusiness;
    IterationDAO iterationDAO;
    IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    
    Iteration iteration;
    List<StoryTO> storiesList;
    List<Task> tasksWithoutStoryList;
    List<TaskTO> tasksTOsWithoutStoryList;
    IterationDataContainer expectedIterationData;
    Task task;
    TaskTO taskTO;

    @Before
    public void setUp_Dependencies() {
        iterationDAO = createMock(IterationDAO.class);
        iterationBusiness.setIterationDAO(iterationDAO);

        storyBusiness = createMock(StoryBusiness.class);
        iterationBusiness.setStoryBusiness(storyBusiness);

        projectBusiness = createMock(ProjectBusiness.class);
        iterationBusiness.setProjectBusiness(projectBusiness);

        hourEntryBusiness = createMock(HourEntryBusiness.class);
        iterationBusiness.setHourEntryBusiness(hourEntryBusiness);

        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        iterationBusiness.setTransferObjectBusiness(transferObjectBusiness);
        
        iterationHistoryEntryBusiness = createMock(IterationHistoryEntryBusiness.class);
        iterationBusiness.setIterationHistoryEntryBusiness(iterationHistoryEntryBusiness);
    }
    
    @Before
    public void setUp() {
        iteration = new Iteration();
        iteration.setId(123);

        Story story1 = new Story();
        story1.setId(666);
        Story story2 = new Story();
        story2.setId(667);
        StoryTO storyTO1 = new StoryTO(story1);
        StoryTO storyTO2 = new StoryTO(story2);
        storiesList = Arrays.asList(storyTO1, storyTO2);

        task = new Task();
        task.setId(1254);
        taskTO = new TaskTO(task);
        tasksWithoutStoryList = Arrays.asList(task);
        tasksTOsWithoutStoryList = Arrays.asList(taskTO);

        expectedIterationData = new IterationDataContainer();
        expectedIterationData.getStories().addAll(storiesList);
        expectedIterationData.getTasksWithoutStory().addAll(
                tasksTOsWithoutStoryList);
    }

    @Test
    public void testGetIterationContents_doNotExcludeTasks() {
        Collection<User> assignedUsers = Arrays.asList(new User());

        expect(iterationDAO.get(iteration.getId())).andReturn(iteration);
        expect(projectBusiness.getAssignedUsers((Project) iteration
                        .getParent())).andReturn(assignedUsers);
        expect(transferObjectBusiness.constructBacklogDataWithUserData(
                        iteration, assignedUsers)).andReturn(storiesList);
        for (StoryTO storyTO : storiesList) {
            expect(storyBusiness.calculateMetrics(storyTO)).andReturn(null);
        }
        expect(iterationDAO.getTasksWithoutStoryForIteration(iteration))
                .andReturn(tasksWithoutStoryList);

        expect(transferObjectBusiness.constructTaskTO(task, assignedUsers))
                .andReturn(taskTO);

        expect(hourEntryBusiness.calculateSum(taskTO.getHourEntries()))
                .andReturn(Long.valueOf(0));

        replay(iterationDAO, transferObjectBusiness, projectBusiness,
                storyBusiness, hourEntryBusiness);

        IterationDataContainer actualIterationData = iterationBusiness
                .getIterationContents(iteration.getId());

        assertEquals(expectedIterationData.getStories(), actualIterationData
                .getStories());
        assertEquals(expectedIterationData.getTasksWithoutStory(),
                actualIterationData.getTasksWithoutStory());

        verify(iterationDAO, transferObjectBusiness, projectBusiness,
                storyBusiness, hourEntryBusiness);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetIterationContents_nullBacklog() {
        expect(iterationDAO.get(0)).andReturn(null);
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
        Integer expectedPercentDone = 50;
        
        expect(iterationHistoryEntryBusiness.retrieveLatest(iteration))
            .andReturn(latestEntry);
        expect(storyBusiness.getStoryPointSumByBacklog(iteration))
            .andReturn(expectedStoryPoints);
        expect(hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration))
            .andReturn(expectedSpentEffort);
        expect(iterationDAO.getCountOfDoneAndAllTasks(iteration))
            .andReturn(Pair.create(2,4));
        replay(iterationHistoryEntryBusiness, storyBusiness, hourEntryBusiness, iterationDAO);
        
        IterationMetrics actualMetrics = iterationBusiness.getIterationMetrics(iteration);
        
        assertNotNull(actualMetrics);
        assertEquals(latestEntry.getEffortLeftSum(), actualMetrics.getEffortLeft().getMinorUnits().longValue());
        assertEquals(latestEntry.getOriginalEstimateSum(), actualMetrics.getOriginalEstimate().getMinorUnits().longValue());
        assertEquals(expectedStoryPoints, actualMetrics.getStoryPoints().intValue());
        assertEquals(expectedSpentEffort, actualMetrics.getSpentEffort().getMinorUnits().longValue());
        assertEquals(expectedPercentDone,actualMetrics.getPercentDone());
        
        verify(iterationHistoryEntryBusiness, storyBusiness, hourEntryBusiness, iterationDAO);
    }
    
    
    @Test
    public void testGetIterationMetrics_nullLatestHistoryEntry() {
        expect(iterationHistoryEntryBusiness.retrieveLatest(iteration))
            .andReturn(null);
        expect(iterationDAO.getCountOfDoneAndAllTasks(iteration))
            .andReturn(Pair.create(2,4));
        replay(iterationHistoryEntryBusiness, iterationDAO);
        
        IterationMetrics actualMetrics = iterationBusiness.getIterationMetrics(iteration); 
        
        assertEquals(0L, actualMetrics.getEffortLeft().getMinorUnits().longValue());
        assertEquals(0L, actualMetrics.getOriginalEstimate().getMinorUnits().longValue());
        
        verify(iterationHistoryEntryBusiness, iterationDAO);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetIterationMetrics_nullIteration() {
        iterationBusiness.getIterationMetrics(null);
    }
}
