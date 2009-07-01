package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;

public class BacklogActionTest {

    private BacklogAction backlogAction = new BacklogAction();
    private BacklogBusiness backlogBusiness;
    
    private Product product;
    private Collection<Story> stories;
    
    @Before
    public void setUp_dependencies() {
        backlogBusiness = createMock(BacklogBusiness.class);
        backlogAction.setBacklogBusiness(backlogBusiness);
    }
    
    @Before
    public void setUp() {
        product = new Product();
        product.setId(1235);
        
        stories = Arrays.asList(new Story(), new Story());
        product.setStories(stories);
    }
    
    @Test
    public void testRetrieveStories() {
        backlogAction.setBacklogId(product.getId());
        
        expect(backlogBusiness.retrieve(product.getId())).andReturn(product);
        replay(backlogBusiness);
        
        assertEquals(Action.SUCCESS, backlogAction.retrieveStories());
        assertEquals(stories, backlogAction.getStories());
        
        verify(backlogBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRetrieveStories_noSuchBacklog() {
        backlogAction.setBacklogId(-1);
        
        expect(backlogBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException("Not found"));
        replay(backlogBusiness);
        
        backlogAction.retrieveStories();
        
        verify(backlogBusiness);
    }
    
    @Test
    public void testGetSubBacklogsASJson() {
        backlogAction.setBacklogId(product.getId());
        Collection<Backlog> childBacklogs = new ArrayList<Backlog>();
        
        expect(backlogBusiness.retrieveIfExists(product.getId()))
            .andReturn(product);
        expect(backlogBusiness.getChildBacklogs(product)) 
            .andReturn(childBacklogs);
        replay(backlogBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, 
                backlogAction.getSubBacklogsAsJSON());
        assertNotNull(backlogAction.getJsonData());
        
        verify(backlogBusiness);
    }
    
}
