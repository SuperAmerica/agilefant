package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Team;

/**
 * Interface for team business.
 * 
 * @author hhaataja
 * 
 */
public interface TeamBusiness {

    /**
     * Get list of all agilefant teams.
     * 
     * @return list of agilefant teams.
     */
    public List<Team> getAllTeams();

    /**
     * Get team by id.
     * 
     * @param userId
     *                id number of the user
     * @return the user with id userId
     */
    public Team getTeam(int teamId);

}
