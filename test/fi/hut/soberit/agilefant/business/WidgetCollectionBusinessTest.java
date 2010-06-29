package fi.hut.soberit.agilefant.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.WidgetCollectionBusinessImpl;
import fi.hut.soberit.agilefant.db.WidgetCollectionDAO;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

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
}
