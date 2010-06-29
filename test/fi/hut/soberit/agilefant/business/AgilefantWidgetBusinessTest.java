package fi.hut.soberit.agilefant.business;

import java.util.List;

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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class AgilefantWidgetBusinessTest extends MockedTestCase {

    @TestedBean
    AgilefantWidgetBusinessImpl testable;
    
    @Mock
    AgilefantWidgetDAO agilefantWidgetDAO;
    
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
