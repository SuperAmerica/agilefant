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
        expect(agilefantWidgetBusiness.create("text", 122, 5, 2, 1)).andReturn(widget);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.create());
        verifyAll();
        
        assertSame(widget, testable.getWidget());
    }
    
}
