package fi.hut.soberit.agilefant.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.AgilefantWidgetBusinessImpl;
import fi.hut.soberit.agilefant.db.AgilefantWidgetDAO;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class AgilefantWidgetBusinessTest extends MockedTestCase {

    @TestedBean
    AgilefantWidgetBusinessImpl testable;
    
    @Mock
    AgilefantWidgetDAO agilefantWidgetDAO;
    
    @Test
    @DirtiesContext
    public void testRetrieve() {
        expect(agilefantWidgetDAO.get(123)).andReturn(new AgilefantWidget());
        replayAll();
        testable.retrieve(123);
        verifyAll();
    }
}
