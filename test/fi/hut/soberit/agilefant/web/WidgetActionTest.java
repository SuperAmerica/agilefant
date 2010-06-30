package fi.hut.soberit.agilefant.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.AgilefantWidgetBusiness;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class WidgetActionTest extends MockedTestCase {

    @TestedBean
    private WidgetAction testable;
    
    @Mock
    private AgilefantWidgetBusiness agilefantWidgetBusiness;
    
    @Test
    @DirtiesContext
    public void testCreate() {
        testable.setType("text");
        testable.setObjectId(122);
        testable.setCollectionId(5);
        testable.setPosition(2);
        testable.setListNumber(1);
        
        AgilefantWidget widget = new AgilefantWidget();
        expect(agilefantWidgetBusiness.create("text", 122, 5)).andReturn(widget);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.create());
        verifyAll();
        
        assertSame(widget, testable.getWidget());
    }
    
    @Test
    @DirtiesContext
    public void testDelete() {
        testable.setWidgetId(123);
        
        agilefantWidgetBusiness.delete(123);
        replayAll();
        assertEquals(Action.SUCCESS, testable.delete());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMove() {
        AgilefantWidget widget = new AgilefantWidget();
        testable.setWidgetId(123);
        testable.setPosition(0);
        testable.setListNumber(2);
        
        expect(agilefantWidgetBusiness.retrieve(123)).andReturn(widget);
        agilefantWidgetBusiness.move(widget, 0, 2);
        replayAll();
        assertEquals(Action.SUCCESS, testable.move());
        verifyAll();
    }
}
