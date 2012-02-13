package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.matchers.Each;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
@SuppressWarnings("unused")
public class StoryBusinessCreateStoryTest extends MockedTestCase {
    
    @TestedBean
    private StoryBusinessImpl storyBusiness;
    
    @Mock
    private IterationHistoryEntryBusiness iheBusiness;
    @Mock
    private BacklogHistoryEntryBusiness blheBusiness;
    @Mock(strict=true)
    private StoryDAO storyDAO;
    @Mock
    private BacklogBusiness backlogBusiness;
    @Mock
    private IterationDAO iterationDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private HourEntryDAO hourEntryDAO;
    @Mock
    private ProjectBusiness projectBusiness;
    @Mock
    private StoryHistoryDAO storyHistoryDAO;
    @Mock
    private StoryRankBusiness storyRankBusiness;
    @Mock
    private TransferObjectBusiness transferObjectBusiness;
    @Mock
    private HourEntryBusiness hourEntryBusiness;
    @Mock
    private TaskBusiness taskBusiness;
    @Mock
    private StoryHierarchyBusiness storyHierarchyBusiness;
    @Mock
    private LabelBusiness labelBusiness;
    
    
    @Test
    @DirtiesContext
    public void testCreateStory_noResponsibles() {
        Story tmp = new Story();
        Iteration blog = new Iteration();
        Project proj = new Project();
        blog.setParent(proj);
        expect(backlogBusiness.retrieve(5)).andReturn(blog);
        
        Capture<Story> capturedStory = new Capture<Story>();
        
        expect(storyDAO.create(EasyMock.capture(capturedStory))).andReturn(88);
        expect(storyDAO.get(88)).andReturn(tmp);
        storyRankBusiness.rankToBottom(tmp, blog);
        storyRankBusiness.rankToBottom(tmp, proj);
        
        blheBusiness.updateHistory(blog.getId());
        iheBusiness.updateIterationHistory(blog.getId());
        
        Story returnedStory = new Story();
        returnedStory.setId(88);
        expect(storyDAO.get(88)).andReturn(returnedStory);
        
        Story dataItem = new Story();
        dataItem.setName("Foofaa");
        dataItem.setDescription("Foofaa");
        dataItem.setStoryPoints(22);
        dataItem.setState(StoryState.STARTED);
        
        storyHierarchyBusiness.moveToBottom(returnedStory);
        labelBusiness.createStoryLabels(null, 88);
        
        replayAll();
        Story actual = this.storyBusiness.create(dataItem, 5, null, null, null);
        verifyAll();
        
        assertEquals(actual.getClass(), Story.class);
        assertEquals(blog, capturedStory.getValue().getBacklog());
        
        assertEquals(dataItem.getName(), capturedStory.getValue().getName());
        assertEquals(dataItem.getDescription(), capturedStory.getValue().getDescription());
        assertEquals(dataItem.getStoryPoints(), capturedStory.getValue().getStoryPoints());
        assertEquals(dataItem.getState(), capturedStory.getValue().getState());
    }
    
    @Test
    @DirtiesContext
    public void testCreateStory_withResponsibles() {
        User user1 = new User();
        User user2 = new User();
        Story tmp = new Story();
        List<String> labels = new ArrayList<String>();
        Backlog blog = new Project();
        expect(backlogBusiness.retrieve(5)).andReturn(blog);
        expect(userDAO.get(2)).andReturn(user1);
        expect(userDAO.get(23)).andReturn(user2);
        
        Capture<Story> capturedStory = new Capture<Story>();
        
        expect(storyDAO.create(EasyMock.capture(capturedStory))).andReturn(88);
        expect(storyDAO.get(88)).andReturn(tmp);
        
        storyRankBusiness.rankToBottom(EasyMock.isA(Story.class), EasyMock.isA(Backlog.class));
        
        blheBusiness.updateHistory(blog.getId());
        
        Story returnedStory = new Story();
        returnedStory.setId(88);
        expect(storyDAO.get(88)).andReturn(returnedStory);
        
        storyHierarchyBusiness.moveToBottom(returnedStory);
        
        labelBusiness.createStoryLabels(labels, 88);
        
        replayAll();
        Story actual = this.storyBusiness.create(new Story(), 5, null,
                new HashSet<Integer>(Arrays.asList(2,23)), labels);
        verifyAll();
        
        assertSame(actual, returnedStory);
        assertTrue(capturedStory.getValue().getResponsibles().contains(user1));
        assertTrue(capturedStory.getValue().getResponsibles().contains(user2));
        assertEquals(blog, capturedStory.getValue().getBacklog());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateStory_nullDataItem() {
        this.storyBusiness.create(null, 123, null, new HashSet<Integer>(), null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateStory_nullBacklogId() {
        this.storyBusiness.create(new Story(), null, null, new HashSet<Integer>(), null);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testCreateStory_backlogNotFound() {
        expect(backlogBusiness.retrieve(5)).andThrow(new ObjectNotFoundException());
        this.storyBusiness.create(new Story(), 222, null, new HashSet<Integer>(), null);
    }
    
    @Test
    @DirtiesContext
    public void createStoryToIteration() {
        Project project = new Project();
        project.setId(1);
        
        Iteration iteration = new Iteration();
        iteration.setId(2);
        iteration.setParent(project);
        
        Story story = new Story();
        story.setBacklog(iteration);
        
        expect(storyDAO.create(story)).andReturn(new Integer(1));
        expect(storyDAO.get(1)).andReturn(story);
        
        storyRankBusiness.rankToBottom(story, iteration);
        storyRankBusiness.rankToBottom(story, project);
        
        iheBusiness.updateIterationHistory(2);
        blheBusiness.updateHistory(2);
        
        replayAll();
        storyBusiness.create(story);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void createStoryToProject() {
        Project project = new Project();
        project.setId(1);
        
        Story story = new Story();
        story.setBacklog(project);
        
        expect(storyDAO.create(story)).andReturn(new Integer(1));
        expect(storyDAO.get(1)).andReturn(story);
        
        storyRankBusiness.rankToBottom(story, project);
        
        blheBusiness.updateHistory(1);
        
        replayAll();
        storyBusiness.create(story);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testCreateStoryUnder() {
        Product product = new Product();
        product.setId(10);
        
        Story reference = new Story();
        reference.setBacklog(product);
        Story data = new Story();
        data.setId(2);
        
        expect(storyDAO.get(1)).andReturn(reference);
        
        expect(backlogBusiness.retrieve(10)).andReturn(product);
        expect(storyDAO.create(EasyMock.isA(Story.class))).andReturn(new Integer(2));
        expect(storyDAO.get(2)).andReturn(data).times(2);
        storyHierarchyBusiness.moveUnder(data, reference);
        labelBusiness.createStoryLabels(null, 2);
        
        replayAll();
        storyBusiness.createStoryUnder(1, data, null, null);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testCreateSibling() {
        Product product = new Product();
        product.setId(10);
        
        Story reference = new Story();
        reference.setBacklog(product);
        Story data = new Story();
        data.setId(2);
        
        expect(storyDAO.get(1)).andReturn(reference);
        
        expect(backlogBusiness.retrieve(10)).andReturn(product);
        expect(storyDAO.create(EasyMock.isA(Story.class))).andReturn(new Integer(2));
        expect(storyDAO.get(2)).andReturn(data).times(2);
        storyHierarchyBusiness.moveAfter(data, reference);
        labelBusiness.createStoryLabels(null, 2);
        replayAll();
        storyBusiness.createStorySibling(1, data, null, null);
        verifyAll();
    }
}
