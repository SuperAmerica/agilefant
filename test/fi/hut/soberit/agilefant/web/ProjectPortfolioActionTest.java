package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PortfolioBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.SpringAssertions;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

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
    public void testSpringScope() {
        SpringAssertions.assertScopeAnnotation("prototype",
                ProjectPortfolioAction.class);
    }

    // retrieve() should
    // + return Action.SUCCESS
    @Test
    @DirtiesContext
    public void testRetrieve() {
        expect(widgetCollectionBusiness.getAllCollections())
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
