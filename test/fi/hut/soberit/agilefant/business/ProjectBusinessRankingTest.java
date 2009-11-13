package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.ProjectBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.RankingBusinessImpl;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.hibernate.ProjectDAOHibernate;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectBusinessRankingTest {

    private ProjectBusinessImpl projectBusiness;
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

    /*
     * The rest of this file contains only infrastructure
     * that is needed for readable tests in this class. 
     */
    
    private void verifyRanks() {
        for (Map.Entry<Project, Integer> entry : expectedRanks.entrySet()) {
            assertEquals(entry.getValue(), Integer.valueOf(entry.getKey().getRank()));
        }
    }
    
    private void setupProjects(ProjectDefinition... projectDefinitions) {
        projects = new ArrayList<Project>();
        expectedRanks = new HashMap<Project, Integer>();
        for (ProjectDefinition definition : projectDefinitions) {
            projects.add(definition.build());
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
