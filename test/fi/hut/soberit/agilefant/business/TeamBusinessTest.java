package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.TeamBusinessImpl;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;

public class TeamBusinessTest {
    
    private TeamBusinessImpl teamBusiness;
    
    private TeamDAO teamDAO;
    private UserBusiness userBusiness;
    
    @Before
    public void setUp_dependencies() {
        teamBusiness = new TeamBusinessImpl();
        
        teamDAO = createMock(TeamDAO.class);
        teamBusiness.setTeamDAO(teamDAO);
        
        userBusiness = createMock(UserBusiness.class);
        teamBusiness.setUserBusiness(userBusiness);
    }

    private void replayAll() {
        replay(teamDAO, userBusiness);
    }

    private void verifyAll() {
        verify(teamDAO, userBusiness);
    }

    
    @Test
    public void testStoreTeam_existing() {
        Team team = new Team();
        team.setId(123);
        
        User user = new User();      

        expect(userBusiness.retrieve(22)).andReturn(user);
        expect(userBusiness.retrieve(13)).andReturn(new User());
        teamDAO.store(team);
        
        replayAll();
        Team actual = teamBusiness.storeTeam(team, new HashSet<Integer>(Arrays.asList(22, 13)));
        verifyAll();
        
        assertEquals(team, actual);
        assertEquals(2, team.getUsers().size());
        assertTrue(team.getUsers().contains(user));
    }
    
    @Test
    public void testStoreTeam_newTeam() {
        Team team = new Team();
        expect(teamDAO.create(team)).andReturn(555);
        expect(teamDAO.get(555)).andReturn(team);
        
        replayAll();
        Team actual = teamBusiness.storeTeam(team, null);
        verifyAll();
        
        assertEquals(team, actual);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testStoreTeam_nullCheck() {
        replayAll();
        teamBusiness.storeTeam(null, null);
        verifyAll();
    }
    
}
