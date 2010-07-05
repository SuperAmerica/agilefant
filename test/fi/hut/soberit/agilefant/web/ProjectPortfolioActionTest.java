package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.SpringAssertions;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class ProjectPortfolioActionTest extends MockedTestCase {

    @TestedBean
    private ProjectPortfolioAction projectPortfolioAction;

    @Mock
    private PortfolioBusiness portfolioBusiness;
    
    @Mock
    private WidgetCollectionBusiness widgetCollectionBusiness;

    // ProjectPortfolioAction should
    // + have Scope annotation with "prototype" value
    @Test
    @DirtiesContext
    public void testSpringScope() {
        SpringAssertions.assertScopeAnnotation("prototype",
                ProjectPortfolioAction.class);
    }

//    // retrieve() should
//    // + return Action.SUCCESS
    @Test
    @DirtiesContext
    public void testRetrieve() {
        User user = new User();
        this.setCurrentUser(user);
        expect(widgetCollectionBusiness.getAllPublicCollections())
            .andReturn(new ArrayList<WidgetCollection>());
        
        expect(widgetCollectionBusiness.getCollectionsForUser(user))
            .andReturn(new ArrayList<WidgetCollection>());
        replayAll();
        assertEquals(Action.SUCCESS, projectPortfolioAction.retrieve());
        verifyAll();
    }

    // portfolioData() should
    // + retrieve a PortfolioTO from PortfolioBusiness
    // + set PortfolioTO field
    // + return Action.SUCCESS
    @Test
    @DirtiesContext
    public void testPortfolioData() {
        PortfolioTO portfolioTO = new PortfolioTO();
        
        expect(portfolioBusiness.getPortfolioData()).andReturn(portfolioTO);
        replayAll();

        assertEquals(Action.SUCCESS, projectPortfolioAction.portfolioData());
        assertSame(portfolioTO, projectPortfolioAction.getPortfolioData());
        verifyAll();
    }

}
