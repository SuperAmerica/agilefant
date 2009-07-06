package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.model.Team;

public class TeamActionTest {
    
    // Class under test
    TeamAction teamAction;
    
    // Dependencies
    TeamBusiness teamBusiness;
    
    // Test data
    Team team1;
    Team team2;
    
    @Before
    public void setUp_dependencies() {
        teamAction = new TeamAction();
        
        teamBusiness = createStrictMock(TeamBusiness.class);
        teamAction.setTeamBusiness(teamBusiness);
    }
    
    private void replayAll() {
        replay(teamBusiness);
    }
    
    private void verifyAll() {
        verify(teamBusiness);
    }
    
    @Before
    public void setUp_data() {
        team1 = new Team();
        team1.setName("First team");
        team2 = new Team();
        team2.setName("Second team");
    }
    
    @Test
    public void testRetrieveAll() {
        Collection<Team> teamList = Arrays.asList(team1, team2);
        expect(teamBusiness.retrieveAll()).andReturn(teamList);
        replayAll();
        
        assertEquals(Action.SUCCESS, teamAction.retrieveAll());
        assertEquals(teamList, teamAction.getTeamList());
        
        verifyAll();
    }
}