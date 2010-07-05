package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.AgilefantWidgetBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class PortletActionTest extends MockedTestCase {

    @TestedBean
    private PortletAction testable;

    @Mock
    private AgilefantWidgetBusiness agilefantWidgetBusiness;

    @Mock
    private WidgetCollectionBusiness widgetCollectionBusiness;

    @Test
    @DirtiesContext
    public void testRetrieve() {
        WidgetCollection collection = new WidgetCollection();
        testable.setCollectionId(123);

        User user = new User();

        expect(widgetCollectionBusiness.getAllPublicCollections()).andReturn(
                new ArrayList<WidgetCollection>(Arrays.asList(collection,
                        new WidgetCollection())));
        this.setCurrentUser(user);
        expect(widgetCollectionBusiness.getCollectionsForUser(user)).andReturn(
                Arrays.asList(collection));

        expect(widgetCollectionBusiness.retrieve(123)).andReturn(collection);
        expect(agilefantWidgetBusiness.generateWidgetGrid(collection, 2))
                .andReturn(new ArrayList<List<AgilefantWidget>>());

        replayAll();
        assertEquals(Action.SUCCESS, testable.retrieve());
        verifyAll();

        assertEquals(2, testable.getAllCollections().size());
        assertSame(collection, testable.getContents());
    }

    @Test
    @DirtiesContext
    public void testStore() {
        WidgetCollection collection = new WidgetCollection();
        testable.setCollection(collection);
        widgetCollectionBusiness.store(collection);
        replayAll();
        assertEquals(Action.SUCCESS, testable.store());
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testCreate() {
        WidgetCollection collection = new WidgetCollection();
        collection.setId(558);
        expect(widgetCollectionBusiness.createPortfolio())
                .andReturn(collection);

        replayAll();
        assertEquals(Action.SUCCESS, testable.create());
        verifyAll();

        assertEquals(558, testable.getCollectionId());
        assertSame(collection, testable.getCollection());
    }

}
