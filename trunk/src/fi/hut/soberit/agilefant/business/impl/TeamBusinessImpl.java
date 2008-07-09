package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;

public class TeamBusinessImpl implements TeamBusiness {

    TeamDAO teamDAO;
    
    public List<Team> getAllTeams() {
        return new ArrayList<Team>(teamDAO.getAll());
    }

    public Team getTeam(int teamId) {
        return teamDAO.get(teamId);
    }

    public TeamDAO getTeamDAO() {
        return teamDAO;
    }

    public void setTeamDAO(TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
    }

}
