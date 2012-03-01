package fi.hut.soberit.agilefant.business;

import java.util.Set;

import fi.hut.soberit.agilefant.model.Team;

/**
 * Interface for team business.
 * 
 * @author hhaataja
 * 
 */
public interface TeamBusiness extends GenericBusiness<Team> {

    /**
     * Store or create a team.
     */
    Team storeTeam(Team team, Set<Integer> userIds, Set<Integer> productIds);

}
