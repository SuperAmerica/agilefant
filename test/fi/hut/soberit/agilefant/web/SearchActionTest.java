package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.SearchBusiness;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.SearchResultRow;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class SearchActionTest extends MockedTestCase {

    @TestedBean
    private SearchAction searchAction;
    @Mock
    private SearchBusiness searchBusiness;
    
    @Test
    @DirtiesContext
    public void testExecute() {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        searchAction.setTerm("foo");
        expect(searchBusiness.searchStoriesAndBacklog("foo")).andReturn(result);
        replayAll();
        searchAction.execute();
        assertEquals(result, searchAction.getResults());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchIterations() {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        searchAction.setTerm("foo");
        expect(searchBusiness.searchIterations("foo")).andReturn(result);
        replayAll();
        searchAction.searchIterations();
        assertEquals(result, searchAction.getResults());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchProjects() {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        searchAction.setTerm("foo");
        expect(searchBusiness.searchProjects("foo")).andReturn(result);
        replayAll();
        searchAction.searchProjects();
        assertEquals(result, searchAction.getResults());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchStories() {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        searchAction.setTerm("foo");
        expect(searchBusiness.searchStories("foo")).andReturn(result);
        replayAll();
        searchAction.searchStories();
        assertEquals(result, searchAction.getResults());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSearchUsers() {
        List<SearchResultRow> result = new ArrayList<SearchResultRow>();
        searchAction.setTerm("foo");
        expect(searchBusiness.searchUsers("foo")).andReturn(result);
        replayAll();
        searchAction.searchUsers();
        assertEquals(result, searchAction.getResults());
        verifyAll();
    }
}
