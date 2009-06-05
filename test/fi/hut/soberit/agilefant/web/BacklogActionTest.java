package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;

public class BacklogActionTest {

    private BacklogAction backlogAction = new BacklogAction();
    private BacklogBusiness backlogBusiness;
    private Product product;
    
    @Before
    public void setUp() {
        backlogBusiness = createMock(BacklogBusiness.class);
        backlogAction.setBacklogBusiness(backlogBusiness);
        
        product = new Product();
        product.setId(1235);
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
