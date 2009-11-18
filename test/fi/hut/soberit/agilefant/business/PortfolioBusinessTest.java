package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Period;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.PortfolioBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.SpringAssertions;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.PortfolioTO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class PortfolioBusinessTest {

    @TestedBean
    private PortfolioBusinessImpl portfolioBusiness;

    @Mock
    private SettingBusiness settingBusiness;
    @Mock
    private ProjectDAO projectDAO;

    protected void verifyAll() {
        verify(projectDAO, settingBusiness);
    }

    protected void replayAll() {
        replay(projectDAO, settingBusiness);
    }

    // PortfolioBusiness should
    // + have no scope annotation
    @Test
    public void testSpringScope() {
        SpringAssertions.assertNoScopeAnnotation(PortfolioBusiness.class);
    }

    // getPortfolioData() should
    // + retrieve portfolio time span setting from SettingBusiness
    // + retrieve ranked projects from ProjectDAO
    // + retrieve unranked projects from ProjectDAO
    // + return a non-null PortfolioTO that contains
    // a list of ranked projects and a list of unranked projects
    @Test
    @DirtiesContext
    public void testGetPortfolioData() {
        LocalDate today = new LocalDate();
        Period timeSpan = Months.months(6).toPeriod();
        LocalDate endDate = today.plus(timeSpan);
        List<Project> rankedProjects = new ArrayList<Project>();
        List<Project> unrankedProjects = new ArrayList<Project>();

        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(timeSpan);
        expect(projectDAO.getRankedProjects(today, endDate)).andReturn(
                rankedProjects);
        expect(projectDAO.getUnrankedProjects(today, endDate)).andReturn(
                unrankedProjects);
        replayAll();

        PortfolioTO result = portfolioBusiness.getPortfolioData();
        assertNotNull(result);
        assertSame(rankedProjects, result.getRankedProjects());
        assertSame(unrankedProjects, result.getUnrankedProjects());
        verifyAll();
    }

}
