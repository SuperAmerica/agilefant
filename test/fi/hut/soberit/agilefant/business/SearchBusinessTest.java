package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.SearchBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.SearchResultRow;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class SearchBusinessTest extends MockedTestCase {
    
    @TestedBean
    private SearchBusinessImpl searchBusiness;
    @Mock
    private StoryDAO storyDAO;
    @Mock
    private BacklogDAO backlogDAO;
    @Mock
    private TaskDAO taskDAO;
    @Mock
    private UserDAO userDAO;
    
    @Test
    @DirtiesContext
    public void testSearchStoriesAndBacklogs() {
        String search = "foo";
        Story story = new Story();
        story.setBacklog(new Iteration());
        expect(backlogDAO.searchByName(search)).andReturn(Arrays.asList((Backlog)(new Iteration())));
        expect(storyDAO.searchByName(search)).andReturn(Arrays.asList(story));
        expect(taskDAO.searchByName(search)).andReturn(Arrays.asList((Task)(new Task())));
        replayAll();
        List<SearchResultRow> result = searchBusiness.searchStoriesAndBacklog(search);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getOriginalObject() instanceof Iteration);
        assertTrue(result.get(1).getOriginalObject() instanceof Story);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchStoriesAndBacklogs_reference() {
        String search = "story:123";
        expect(backlogDAO.searchByName(search)).andReturn(new ArrayList<Backlog>());
        expect(storyDAO.searchByName(search)).andReturn(new ArrayList<Story>());
        expect(storyDAO.get(123)).andReturn(new Story());
        expect(taskDAO.searchByName(search)).andReturn(Arrays.asList((Task)(new Task())));
        replayAll();
        List<SearchResultRow> result = searchBusiness.searchStoriesAndBacklog(search);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getOriginalObject() instanceof Story);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_story() {
        String term = "story:123";
        Story story = new Story();
        expect(storyDAO.get(123)).andReturn(story);
        replayAll();
        assertEquals(story, searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_storyNotFound() {
        String term = "story:123";
        expect(storyDAO.get(123)).andReturn(null);
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_backlog() {
        String term = "backlog:123";
        Backlog backlog = new Iteration();
        expect(backlogDAO.get(123)).andReturn(backlog);
        replayAll();
        assertEquals(backlog, searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_backlogNotFound() {
        String term = "backlog:123";
        expect(backlogDAO.get(123)).andReturn(null);
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_invalidTerm1() {
        String term = "foo:123";
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_invalidTerm2() {
        String term = "backlog:123:foo:faa";
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_invalidTerm3() {
        String term = "backlog:aaa";
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchByReference_invalidTermEmpty() {
        String term = "";
        replayAll();
        assertNull(searchBusiness.searchByReference(term));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchIterations() {
        String term = "";
        List<Backlog> res = Arrays.asList((Backlog)(new Iteration()));
        
        expect(backlogDAO.searchByName(term, Iteration.class)).andReturn(res);
        replayAll();
        List<SearchResultRow> actual = searchBusiness.searchIterations(term);
        verifyAll();
        assertSame(res.get(0), actual.get(0).getOriginalObject());
    }
    
    @Test
    @DirtiesContext
    public void testSearchProjects() {
        String term = "";
        List<Backlog> res = Arrays.asList((Backlog)(new Project()));
        
        expect(backlogDAO.searchByName(term, Project.class)).andReturn(res);
        replayAll();
        List<SearchResultRow> actual = searchBusiness.searchProjects(term);
        verifyAll();
        assertSame(res.get(0), actual.get(0).getOriginalObject());
    }
    
    @Test
    @DirtiesContext
    public void testSearchStories() {
        String term = "";
        Story story = new Story();
        story.setName("faa");
        story.setBacklog(new Iteration());
        story.getBacklog().setName("foo");
        
        List<Story> res = Arrays.asList(story);
        expect(storyDAO.searchByName(term)).andReturn(res);
        replayAll();
        List<SearchResultRow> actual = searchBusiness.searchStories(term);
        verifyAll();
        assertSame(story, actual.get(0).getOriginalObject());
        assertTrue(actual.get(0).getLabel().contains("foo"));
        assertTrue(actual.get(0).getLabel().contains("faa"));
    }
    
    @Test
    @DirtiesContext
    public void testSearchUsers() {
        String term = "";
        List<User> res = Arrays.asList(new User());
        
        expect(userDAO.searchByName(term)).andReturn(res);
        replayAll();
        List<SearchResultRow> actual = searchBusiness.searchUsers(term);
        verifyAll();
        assertSame(res.get(0), actual.get(0).getOriginalObject());
    }
}
