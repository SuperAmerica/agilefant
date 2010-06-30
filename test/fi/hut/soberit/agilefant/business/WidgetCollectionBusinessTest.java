package fi.hut.soberit.agilefant.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.WidgetCollectionBusinessImpl;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class WidgetCollectionBusinessTest extends MockedTestCase {

    @TestedBean
    WidgetCollectionBusinessImpl testable;
    
    @Mock
    WidgetCollectionDAO widgetCollectionDAO;
    
    @Test
    @DirtiesContext
    public void testRetrieve() {
        expect(widgetCollectionDAO.get(123)).andReturn(new WidgetCollection());
        replayAll();
        testable.retrieve(123);
        verifyAll();
    }
    
    WidgetCollection collection;
    AgilefantWidget  widget1;
    AgilefantWidget  widget2;
    AgilefantWidget  widget3;
    
    private void setUp_data() {
        collection = new WidgetCollection();
        collection.setId(1);
        collection.setName("Test collection");
        
        widget1 = new AgilefantWidget();
        widget1.setType("text");
        
        widget2 = new AgilefantWidget();
        widget2.setType("text");
        
        widget3 = new AgilefantWidget();
        widget3.setType("text");
    }
    
    @Test
    @DirtiesContext
    public void testInsertWidgetToHead_emptyCollection() {
        setUp_data();
        assertNull(widget1.getPosition());
        assertNull(widget1.getListNumber());
        
        replayAll();
        testable.insertWidgetToHead(collection, widget1);
        verifyAll();
        
        assertEquals(0, widget1.getPosition().intValue());
        assertEquals(0, widget1.getListNumber().intValue());
    }
    
    @Test
    @DirtiesContext
    public void testInsertWidgetToHead_notEmpty() {
        setUp_data();
        widget2.setListNumber(0);
        widget2.setPosition(0);
        widget3.setListNumber(1);
        widget3.setPosition(0);
        collection.getWidgets().add(widget2);
        collection.getWidgets().add(widget3);
        
        assertNull(widget1.getPosition());
        assertNull(widget1.getListNumber());
        
        replayAll();
        testable.insertWidgetToHead(collection, widget1);
        verifyAll();
        
        assertEquals(0, widget1.getPosition().intValue());
        assertEquals(0, widget1.getListNumber().intValue());
        
        assertEquals(1, widget2.getPosition().intValue());
        assertEquals(0, widget2.getListNumber().intValue());
        
        assertEquals(0, widget3.getPosition().intValue());
        assertEquals(1, widget3.getListNumber().intValue());
    }
    
    @Test
    @DirtiesContext
    public void testInsertWidgetToPosition() {
        setUp_data();
        widget2.setListNumber(0);
        widget2.setPosition(0);
        widget3.setListNumber(1);
        widget3.setPosition(0);
        collection.getWidgets().add(widget2);
        collection.getWidgets().add(widget3);
        
        assertNull(widget1.getPosition());
        assertNull(widget1.getListNumber());
        
        replayAll();
        testable.insertWidgetToPosition(collection, widget1, 0, 1);
        verifyAll();
        
        assertEquals(0, widget1.getPosition().intValue());
        assertEquals(1, widget1.getListNumber().intValue());
        
        assertEquals(0, widget2.getPosition().intValue());
        assertEquals(0, widget2.getListNumber().intValue());
        
        assertEquals(1, widget3.getPosition().intValue());
        assertEquals(1, widget3.getListNumber().intValue());
    }
}
