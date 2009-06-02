package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.IterationBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

public class IterationBusinessTest {

    IterationBusinessImpl iterationBusiness = new IterationBusinessImpl();
    IterationDAO iterationDAO;
    StoryBusiness storyBusiness;
    Iteration iteration;
    Story story1;
    Story story2;
    List<Story> storiesList;
    List<Task> tasksWithoutStoryList;
    IterationDataContainer expectedIterationData;
    
     
    @Before
    public void setUp() {
        iteration = new Iteration();
        iteration.setId(123);
        storiesList = Arrays.asList(new Story(), new Story());
        tasksWithoutStoryList = Arrays.asList(new Task(), new Task());
        iteration.setStories(storiesList);
        
        expectedIterationData = new IterationDataContainer();
        expectedIterationData.setStories(storiesList);
        expectedIterationData.setTasksWithoutStory(tasksWithoutStoryList);
        
        iterationDAO = createMock(IterationDAO.class);
        iterationBusiness.setIterationDAO(iterationDAO);
        storyBusiness = createMock(StoryBusiness.class);
        iterationBusiness.setStoryBusiness(storyBusiness);
    }

    @Test
    public void testGetIterationContents_doNotExcludeTasks() {       
        expect(iterationDAO.get(iteration.getId())).andReturn(iteration);
        expect(iterationDAO.getTasksWithoutStoryForIteration(iteration)).andReturn(tasksWithoutStoryList);
        replay(iterationDAO);
        
        IterationDataContainer actualIterationData =
            iterationBusiness.getIterationContents(iteration.getId(), false);
        
        assertEquals(expectedIterationData.getStories(), actualIterationData.getStories());
        assertEquals(expectedIterationData.getTasksWithoutStory(), actualIterationData.getTasksWithoutStory());
        
        verify(iterationDAO);
    }
    
    @Test
    public void testGetIterationContents_excludeTasks() {       
        assertTrue("Not implemented", false);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetIterationContents_nullBacklog() {
        expect(iterationDAO.get(0)).andReturn(null);
        replay(iterationDAO);
        assertNull(iterationBusiness.getIterationContents(0, false));
        verify(iterationDAO);
    }
}
