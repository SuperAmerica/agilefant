package fi.hut.soberit.agilefant.util;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;

public class BacklogConverterTest {
    BacklogConverter testable;
    BacklogBusiness backlogBusiness;
    private Backlog backlog;
    
    @Before
    public void setUp() {
        testable = new BacklogConverter();
        backlogBusiness = createStrictMock(BacklogBusiness.class);
        
        backlog = new Product();
        backlog.setId(5);
        
        testable.setBacklogBusiness(backlogBusiness);
    }
    
    private void replayAll() {
        replay(backlogBusiness);
    }

    private void verifyAll() {
        verify(backlogBusiness);
    }
    
    @Test
    public void testConvertFromString_withValidId() {
        expect(backlogBusiness.retrieve(5)).andReturn(backlog);
        replayAll();
        Backlog returned = (Backlog)testable.convertFromString(null, new String[] { "5" }, Backlog.class);
        
        verifyAll();
        assertSame(backlog, returned);
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertFromString_invalidId() {
        testable.convertFromString(null, new String[] { "5sadföwq3rio" }, Backlog.class);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testConvertFromString_notFound() {
        expect(backlogBusiness.retrieve(5)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.convertFromString(null, new String[] { "5" }, Backlog.class);
    }
    
    @Test
    public void testConvertToString() {
        String str = testable.convertToString(null, backlog);
        assertEquals(str, backlog.toString());
    }
}
