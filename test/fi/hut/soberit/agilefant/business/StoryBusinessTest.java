package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

public class StoryBusinessTest {

    StoryBusinessImpl storyBusiness = new StoryBusinessImpl();
    StoryDAO storyDAO;
    IterationDAO iterationDAO;
    Backlog backlog;
    Iteration iteration;
    Story story1;
    Story story2;
    
    @Before
    public void setUp() {
        backlog = new Product();
        iteration = new Iteration();
        iteration.setId(5834);
        story1 = new Story();
        story1.setId(666);
        story2 = new Story();
        storyDAO = createMock(StoryDAO.class);
        storyBusiness.setStoryDAO(storyDAO);
        iterationDAO = createMock(IterationDAO.class);
        storyBusiness.setIterationDAO(iterationDAO);
    }
    
    @Test
    public void testGetStoriesByBacklog() {
        List<Story> storiesList = Arrays.asList(story1, story2);
        expect(storyDAO.getStoriesByBacklog(backlog)).andReturn(storiesList);
        replay(storyDAO);
        
        assertSame(storiesList, storyBusiness.getStoriesByBacklog(backlog));
        
        verify(storyDAO);
    }
    
    @Test
    public void testGetStoryResponsibles() {
        User user = new User();
        story1.getResponsibles().add(user);
        ResponsibleContainer respCont = new ResponsibleContainer(user, true);
        Collection<ResponsibleContainer> responsibles = Arrays.asList(respCont);
        
        assertEquals(responsibles.size(), storyBusiness.getStoryResponsibles(story1).size());
    }
    
    @Test
    public void testGetStoryContents_delegate() {
        expect(storyDAO.get(story1.getId())).andReturn(story1);
        expect(iterationDAO.get(iteration.getId()));
    }
    
    @Test
    public void testGetStoryContents() {
        Task task1 = new Task();
        Task task2 = new Task();
        task2.setStory(story1);
        story1.setBacklog(iteration);
        expect(iterationDAO.getAllTasksForIteration(iteration))
            .andReturn(Arrays.asList(task1, task2));
        replay(storyDAO, iterationDAO);
        assertTrue(storyBusiness.getStoryContents(story1, iteration)
                .contains(task2));
        verify(storyDAO, iterationDAO);
    }
    
    public void testUpdateStoryPriority_noUpdate() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        storyBusiness.updateStoryPriority(stories.get(4), 4);
    }
    
    public void testUpdateStoryPriority_oneUp() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        replay(storyDAO);
        storyBusiness.updateStoryPriority(stories.get(4), 3);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(3, stories.get(4).getPriority());
        assertSame(5, stories.get(5).getPriority());
        verify(storyDAO);
    }
    
    public void testUpdateStoryPriority_oneDown() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        replay(storyDAO);
        storyBusiness.updateStoryPriority(stories.get(4), 5);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(3, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(4, stories.get(5).getPriority());
        verify(storyDAO);
    }
    
    public void testUpdateStoryPriority_toTop() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);

        storyDAO.store(stories.get(0));
        storyDAO.store(stories.get(1));
        storyDAO.store(stories.get(2));
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        replay(storyDAO);
        storyBusiness.updateStoryPriority(stories.get(5), 0);
        assertSame(1, stories.get(0).getPriority());
        assertSame(2, stories.get(1).getPriority());
        assertSame(3, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(0, stories.get(5).getPriority());
        verify(storyDAO);
    }
    
    public void testUpdateStoryPriority_insertNew() {
        Iteration iter = new Iteration();
        List<Story> stories = createUpdatePrioData(iter);
        Story newStory = new Story();
        newStory.setBacklog(iter);
        newStory.setPriority(-1);
        
        storyDAO.store(stories.get(3));
        storyDAO.store(stories.get(4));
        storyDAO.store(stories.get(5));
        storyDAO.store(newStory);
        replay(storyDAO);
        storyBusiness.updateStoryPriority(newStory, 3);
        assertSame(0, stories.get(0).getPriority());
        assertSame(1, stories.get(1).getPriority());
        assertSame(2, stories.get(2).getPriority());
        assertSame(4, stories.get(3).getPriority());
        assertSame(5, stories.get(4).getPriority());
        assertSame(6, stories.get(5).getPriority());
        assertSame(3, newStory.getPriority());
        verify(storyDAO);
    }
    
    private List<Story> createUpdatePrioData(Iteration iter) {
        List<Story> stories = new ArrayList<Story>();
        for(int i = 0 ; i < 6; i++) {
            Story tmp = new Story();
            tmp.setPriority(i);
            tmp.setBacklog(iter);
            stories.add(tmp);
            iter.getStories().add(tmp);
        }
        return stories;
    }
}
