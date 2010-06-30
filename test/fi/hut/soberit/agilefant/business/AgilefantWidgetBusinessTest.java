package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.AgilefantWidgetBusinessImpl;
import fi.hut.soberit.agilefant.db.AgilefantWidgetDAO;
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
public class AgilefantWidgetBusinessTest extends MockedTestCase {

    @TestedBean
    AgilefantWidgetBusinessImpl testable;
    
    @Mock
    AgilefantWidgetDAO agilefantWidgetDAO;
    
    @Mock
    WidgetCollectionBusiness widgetCollectionBusiness;
    
    
    /*
     * CREATE
     */
    @Test
    @DirtiesContext
    public void testCreate() {
        AgilefantWidget returned = new AgilefantWidget();
        WidgetCollection collection = new WidgetCollection();
        Capture<AgilefantWidget> captured = new Capture<AgilefantWidget>();
        
        expect(widgetCollectionBusiness.retrieve(2)).andReturn(collection);
        
        widgetCollectionBusiness.insertWidgetToHead(EasyMock.isA(WidgetCollection.class), EasyMock.isA(AgilefantWidget.class));
        
        expect(agilefantWidgetDAO.create(EasyMock.capture(captured)))
            .andReturn(new Integer(15));
        
        expect(agilefantWidgetDAO.get(15)).andReturn(returned);
        
        
        replayAll();
        assertSame(returned, testable.create("text", 1, 2));
        verifyAll();
        
        AgilefantWidget actual = captured.getValue();
        
        assertEquals("text", actual.getType());
        assertEquals(1, actual.getObjectId().intValue());
        assertEquals(collection, actual.getWidgetCollection());
    }
    
    @Test
    @DirtiesContext
    public void testCreate_nullIds() {
        try {
            testable.create(null, 1, 2);
            fail();
        }
        catch (IllegalArgumentException e) {}
        try {
            testable.create("", null, 2);
            fail();
        }
        catch (IllegalArgumentException e) {}
        try {
            testable.create("", 2, null);
            fail();
        }
        catch (IllegalArgumentException e) {}
    }
    
    @Test
    @DirtiesContext
    public void testMove() {
        WidgetCollection collection = new WidgetCollection();
        AgilefantWidget widget = new AgilefantWidget(); 
        widget.setWidgetCollection(collection);
        
        widgetCollectionBusiness.insertWidgetToPosition(collection, widget, 1, 2);
        replayAll();
        testable.move(widget, 1, 2);
        verifyAll();
    }
    
    
    /*
     * GENERATE GRID
     */
    @Test
    @DirtiesContext
    public void testGenerateWidgetGrid_oneColumn() {
        WidgetCollection widgets = new WidgetCollection();
        AgilefantWidget widget = new AgilefantWidget();
        widget.setPosition(0);
        widget.setListNumber(0);
        widgets.getWidgets().add(widget);
        
        replayAll();
        List<List<AgilefantWidget>> grid = this.testable.generateWidgetGrid(widgets);
        verifyAll();
        assertEquals(1, grid.size());
        assertEquals(1, grid.get(0).size());
        assertSame(widget, grid.get(0).get(0));
    }
    
    @Test
    @DirtiesContext
    public void testGenerateWidgetGrid_empty() {
        WidgetCollection widgets = new WidgetCollection();
        replayAll();
        List<List<AgilefantWidget>> grid = this.testable.generateWidgetGrid(widgets);
        verifyAll();
        assertEquals(0, grid.size());
    }
    
    @Test
    @DirtiesContext
    public void testGenerateWidgetGrid_multipleColumns() {
        WidgetCollection widgets = new WidgetCollection();
        AgilefantWidget widget1 = new AgilefantWidget();
        widget1.setPosition(0);
        widget1.setListNumber(0);
        widgets.getWidgets().add(widget1);
        
        AgilefantWidget widget2 = new AgilefantWidget();
        widget2.setPosition(1);
        widget2.setListNumber(0);
        widgets.getWidgets().add(widget2);
        
        AgilefantWidget widget3 = new AgilefantWidget();
        widget3.setPosition(0);
        widget3.setListNumber(3);
        widgets.getWidgets().add(widget3);
        
        replayAll();
        List<List<AgilefantWidget>> grid = this.testable.generateWidgetGrid(widgets);
        verifyAll();
        
        assertEquals(4, grid.size());
        assertEquals(2, grid.get(0).size());
        assertSame(widget1, grid.get(0).get(0));
        assertSame(widget2, grid.get(0).get(1));
        assertSame(widget3, grid.get(3).get(0));
    }
}
