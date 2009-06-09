package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.IterationBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HourEntryTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

public class IterationBusinessTest {

    IterationBusinessImpl iterationBusiness = new IterationBusinessImpl();
    TransferObjectBusiness transferObjectBusiness;
    ProjectBusiness projectBusiness;
    StoryBusiness storyBusiness;
    IterationDAO iterationDAO;
    Iteration iteration;
    List<StoryTO> storiesList;
    List<Task> tasksWithoutStoryList;
    List<TaskTO> tasksTOsWithoutStoryList;
    IterationDataContainer expectedIterationData;
    Task task;
    TaskTO taskTO;
    
     
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
        expectedIterationData.getTasksWithoutStory().addAll(tasksTOsWithoutStoryList);
        
        iterationDAO = createMock(IterationDAO.class);
        iterationBusiness.setIterationDAO(iterationDAO);
        
        storyBusiness = createMock(StoryBusiness.class);
        iterationBusiness.setStoryBusiness(storyBusiness);

        projectBusiness = createMock(ProjectBusiness.class);
        iterationBusiness.setProjectBusiness(projectBusiness);
        
        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        iterationBusiness.setTransferObjectBusiness(transferObjectBusiness);
    }

    @Test
    public void testGetIterationContents_doNotExcludeTasks() {
        Collection<User> assignedUsers = Arrays.asList(new User());
        
        expect(iterationDAO.get(iteration.getId())).andReturn(iteration);
        expect(projectBusiness.getAssignedUsers((Project)iteration.getParent()))
            .andReturn(assignedUsers);
        expect(transferObjectBusiness.constructIterationDataWithUserData(iteration, assignedUsers))
            .andReturn(storiesList);
        for (StoryTO storyTO: storiesList) {
            expect(storyBusiness.calculateMetrics(storyTO)).andReturn(null);
        }
        expect(iterationDAO.getTasksWithoutStoryForIteration(iteration))
            .andReturn(tasksWithoutStoryList);
        
        expect(transferObjectBusiness.constructTaskTO(task, assignedUsers))
            .andReturn(taskTO);
        
        replay(iterationDAO, transferObjectBusiness, projectBusiness, storyBusiness);
        
        IterationDataContainer actualIterationData =
            iterationBusiness.getIterationContents(iteration.getId());
        
        assertEquals(expectedIterationData.getStories(), actualIterationData.getStories());
        assertEquals(expectedIterationData.getTasksWithoutStory(), actualIterationData.getTasksWithoutStory());
        
        verify(iterationDAO, transferObjectBusiness, projectBusiness, storyBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testGetIterationContents_nullBacklog() {
        expect(iterationDAO.get(0)).andReturn(null);
        replay(iterationDAO);
        assertNull(iterationBusiness.getIterationContents(0));
        verify(iterationDAO);
    }
}
