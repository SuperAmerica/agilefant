package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.RankingBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.hibernate.ProjectDAOHibernate;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectBusinessRankingTest {

    private ProjectBusinessImpl projectBusiness;
    private SettingBusiness settingBusiness;
    private ProjectDAO projectDAO = new MockProjectDAO();
    private List<Project> projects;    
    private Map<Project, Integer> expectedRanks;
    private Project toBeRanked;
    private Project target;

    @Before
    public void setUp() {
        projectBusiness = new ProjectBusinessImpl();
        projectBusiness.setRankingBusiness(new RankingBusinessImpl());
        projectBusiness.setProjectDAO(projectDAO);
        settingBusiness = EasyMock.createMock(SettingBusiness.class); 
        projectBusiness.setSettingBusiness(settingBusiness);
    }

    @Test
    public void testRankUnderProject_noSpacesBetweenRanks() {
        setupProjects(
                project().withRank(1).expectedRank(1).underThis(),
                project().withRank(2).expectedRank(3),
                project().withRank(3).expectedRank(4),
                project().withRank(4).expectedRank(2).move()
        );
        projectBusiness.rankUnderProject(toBeRanked, target);
        verifyRanks();
    }

    @Test
    public void testRankUnderProject_spacesBetweenRanks() {
        setupProjects(
                project().withRank(100).expectedRank(100).underThis(),
                project().withRank(150).expectedRank(151),
                project().withRank(170).expectedRank(171),
                project().withRank(190).expectedRank(101).move()
        );        
        projectBusiness.rankUnderProject(toBeRanked, target);
        verifyRanks();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRankUnderProject_nullProject() {
        projectBusiness.rankUnderProject(null, null);
    }
    
    @Test
    public void testMoveToRanked() {
        setupProjects(
                project().withRank(1).expectedRank(1),
                project().withRank(2).expectedRank(2),
                project().withRank(3).expectedRank(3),
                project().withRank(0).expectedRank(4)
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(3);
        verifyMocks();
        verifyRanks();
        
    }
    
    @Test
    public void testMoveToRanked_emptyDatabase() {
        setupProjects(
                project().withRank(0).expectedRank(1)
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_noRankedProjects() {
        setupProjects(
                project().withRank(0).expectedRank(2),
                project().withRank(1).expectedRank(1).withStartDateOffset(Months.months(8).toPeriod())
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_spaceBetweenProjects() {
        setupProjects(
                project().withRank(0).expectedRank(4),
                project().withRank(1).expectedRank(1),
                project().withRank(3).expectedRank(3)
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_onlyUnrankedProjects() {
        setupProjects(
                project().withRank(0).expectedRank(1),
                project().withRank(0).expectedRank(0),
                project().withRank(0).expectedRank(0)
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_rankedProjectInThePast() {
        setupProjects(
                project().withRank(0).expectedRank(5),
                project().withRank(4).expectedRank(4).withEndDateOffset(Months.months(-8).toPeriod())
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_addBeforeRankedProjects() {
        setupProjects(
                project().withRank(0).expectedRank(2),
                project().withRank(1).expectedRank(1),
                project().withRank(4).expectedRank(5).withStartDateOffset(Months.months(8).toPeriod()),
                project().withRank(5).expectedRank(6).withStartDateOffset(Months.months(9).toPeriod())
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_projectsNotEntirelyInView() {
        setupProjects(
                project().withRank(0).expectedRank(10),
                project().withRank(7).expectedRank(7).withStartDateOffset(Months.months(-2).toPeriod()),
                project().withRank(9).expectedRank(9).withEndDateOffset(Months.months(2).toPeriod())
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    @Test
    public void testMoveToRanked_projectEndAndStartOutsideView() {
        setupProjects(
                project().withRank(0).expectedRank(8),
                project().withRank(7).expectedRank(7).withStartDateOffset(Months.months(-2).toPeriod()).withEndDateOffset(Months.months(2).toPeriod())
        );
        expect(settingBusiness.getPortfolioTimeSpan()).andReturn(Months.months(6).toPeriod());
        replayMocks();
        projectBusiness.moveToRanked(0);
        verifyMocks();
        verifyRanks();
    }
    
    
    
    
    
    

    /*
     * The rest of this file contains only infrastructure
     * that is needed for readable tests in this class. 
     */
    
    private void replayMocks() {
        EasyMock.replay(settingBusiness);
    }
    
    private void verifyMocks() {
        EasyMock.verify(settingBusiness);
    }
    
    private void verifyRanks() {
        for (Map.Entry<Project, Integer> entry : expectedRanks.entrySet()) {
            assertEquals(entry.getValue(), Integer.valueOf(entry.getKey().getRank()));
        }
    }
    
    private void setupProjects(ProjectDefinition... projectDefinitions) {
        projects = new ArrayList<Project>();
        expectedRanks = new HashMap<Project, Integer>();
        int id = 0;
        for (ProjectDefinition definition : projectDefinitions) {
            Project project = definition.build();
            project.setId(id);
            projects.add(project);
            id++;
        }
    }

    private ProjectDefinition project() {
        return new ProjectDefinition();
    }
    
    /**
     * A utility class for defining projects (builder-pattern style)
     */
    private class ProjectDefinition {
        
        private int rank;
        private int expectedRank;
        private boolean move;
        private boolean underThis;
        private ReadablePeriod startDateOffset = Days.days(0);
        private ReadablePeriod endDateOffset = Days.days(0);

        ProjectDefinition withStartDateOffset(ReadablePeriod period) {
            this.startDateOffset = period;
            return this;
        }
        ProjectDefinition withEndDateOffset(ReadablePeriod period) {
            this.endDateOffset = period;
            return this;
        }
        ProjectDefinition withRank(int rank) {
            this.rank = rank;
            return this;
        }
        ProjectDefinition expectedRank(int rank) {
            this.expectedRank = rank;
            return this;
        }
        ProjectDefinition move() {
            this.move = true;
            return this;
        }
        ProjectDefinition underThis() {
            this.underThis = true;
            return this;
        }

        Project build() {
            Project project = new Project();
            project.setRank(rank);
            LocalDate today = new LocalDate();
            project.setStartDate(today.plus(startDateOffset).toDateTimeAtStartOfDay());
            project.setEndDate(project.getStartDate().plus(endDateOffset));
            if (underThis) {
                target = project;
            }
            if (move) {
                toBeRanked = project;
            }            
            expectedRanks.put(project, expectedRank);
            return project;
        }

    }

    /**
     * We extend ProjectDAOHibernate in order to override a single method with a real implementation.
     * Doing this with EasyMock is tedious and probably requires a newer version than what Agilefant
     * currently has.
     * 
     * This might seem dangerous at first but probably isn't because all
     * "real" hibernate methods will throw a NullPointerException
     * because no sessionFactory has been injected.
     */
    private class MockProjectDAO extends ProjectDAOHibernate {
        
        @Override
        public Project get(int id) {
            return projects.get(id);
        }
        
        @Override
        public List<Project> getRankedProjects(LocalDate startDate, LocalDate endDate) {
            List<Project> result = new ArrayList<Project>();
            for (Project project : projects) {
                if (project.getEndDate().isBefore(startDate.toDateTimeAtStartOfDay())) continue;
                if (project.getStartDate().isAfter(endDate.toDateTimeAtStartOfDay())) continue;
                if (project.getRank() > 0) {
                    result.add(project);
                }
            }
            return result;
        }
        
        @Override
        public List<Project> getUnrankedProjects(LocalDate startDate, LocalDate endDate) {
            List<Project> result = new ArrayList<Project>();
            for (Project project : projects) {
                if (project.getEndDate().isBefore(startDate.toDateTimeAtStartOfDay())) continue;
                if (project.getStartDate().isAfter(endDate.toDateTimeAtStartOfDay())) continue;
                if (project.getRank() < 1) {
                    result.add(project);
                }
            }
            return result;
        }

        @Override
        public Project getMaxRankedProject() {
            Project result = null;
            for (Project project : projects) {
                if (project.getRank() < 1) continue;
                if (result == null || project.getRank() > result.getRank()) {
                    result = project;
                }
            }
            return result;
        }
        
        @Override
        public Collection<Project> getProjectsWithRankBetween(int lower,
                int upper) {
            List<Project> result = new ArrayList<Project>();
            for (Project project : projects) {
                if (project.getRank() >= lower && project.getRank() <= upper) {
                    result.add(project);
                }
            }
            return result;
        }
    }

}
