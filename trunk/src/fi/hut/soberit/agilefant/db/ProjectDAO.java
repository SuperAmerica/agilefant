package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.BacklogThemeBinding;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.ProjectMetrics;

/**
 * Interface for a DAO of a Project.
 * 
 * @see GenericDAO
 */
public interface ProjectDAO extends GenericDAO<Project> {

    /**
     * Get all currently ongoing projects.
     */
    public Collection<Project> getOngoingProjects();

    /**
     * Get all projects that have rank.
     */
    public Collection<Project> getAllRankedProjects();

    /**
     * Get all currently ongoing projects that have rank by rank order.
     */
    public Collection<Project> getOngoingRankedProjects();

    /**
     * Get all Currently ongoing projects that are unranked.
     */
    public Collection<Project> getOngoingUnrankedProjects();

    /**
     * Finds the next project that is ongoing and has lower rank number than
     * the project given as parameter.
     * 
     * @param project
     * @return
     */
    public Project findFirstLowerRankedOngoingProject(
            Project project);

    /**
     * Finds the next project that is ongoing and has upper rank number than
     * the project given as parameter.
     * 
     * @param project
     * @return
     */
    public Project findFirstUpperRankedOngoingProject(
            Project project);

    /**
     * Raises the rank of all Projects between the set rank limits, by one.
     * If low limit is null, then all ranks lower than the upper limit are
     * affected. If upper limit is null, then all ranks higher than the low
     * limit are affected. Low limit rank is included in the raised ranks, but
     * upper limit rank is not. The range of raised ranks is
     * [lowLimitRank,upperLimitRank[
     * 
     * @param lowLimitRank
     * @param upperLimitRank
     */
    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank);

    /**
     * Finds the biggest Rank among all Projects.
     * 
     * @return
     */
    public List<Integer> findBiggestRank();
    
    public ProjectMetrics getProjectBLIMetrics(Project proj);
    
    public Integer getDoneBLIs(Project proj);
    
    public List<BacklogThemeBinding> getProjectThemeData(Project proj);
}
