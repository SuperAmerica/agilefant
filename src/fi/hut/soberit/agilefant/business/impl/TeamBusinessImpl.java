package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;

@Service("teamBusiness")
@Transactional
public class TeamBusinessImpl extends GenericBusinessImpl<Team> implements
        TeamBusiness {

    private TeamDAO teamDAO;

    @Autowired
    public void setTeamDAO(TeamDAO teamDAO) {
        this.genericDAO = teamDAO;
        this.teamDAO = teamDAO;
    }

}
