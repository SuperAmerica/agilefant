package fi.hut.soberit.agilefant.business.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;

@Service("teamBusiness")
@Transactional
public class TeamBusinessImpl extends GenericBusinessImpl<Team> implements
        TeamBusiness {

    private TeamDAO teamDAO;

    private UserBusiness userBusiness;
    
    public TeamBusinessImpl() {
        super(Team.class);
    }
    
    @Autowired
    public void setTeamDAO(TeamDAO teamDAO) {
        this.genericDAO = teamDAO;
        this.teamDAO = teamDAO;
    }
    
    @Autowired
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }


    /** {@inheritDoc} */
    @Transactional
    public Team storeTeam(Team team, Set<Integer> userIds) {
        if (team == null) {
            throw new IllegalArgumentException("Team must be supplied.");
        }
        
        // Get users
        Set<User> users = new HashSet<User>();
        if (userIds != null) {
            for (Integer uid : userIds) {
                users.add(userBusiness.retrieve(uid));
            }
            team.setUsers(users);
        }
        
        Team stored = null;
        if (team.getId() != 0) {
            stored = team;
            teamDAO.store(stored);
        }
        else {
            int newId = (Integer)teamDAO.create(team);
            stored = teamDAO.get(newId);
        }
        
        return stored;
    }


}
