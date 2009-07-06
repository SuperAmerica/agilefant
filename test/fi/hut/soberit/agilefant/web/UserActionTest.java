package fi.hut.soberit.agilefant.web;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;

public class UserActionTest {
    
    // Class under test
    UserAction userAction;
    
    // Dependencies
    UserBusiness userBusiness;
    TeamBusiness teamBusiness;
    
    // Test data
    User user;
    
    @Before
    public void setUp_dependencies() {
        userAction = new UserAction();
        
        userBusiness = createStrictMock(UserBusiness.class);
        userAction.setUserBusiness(userBusiness);
        
        teamBusiness = createStrictMock(TeamBusiness.class);
        userAction.setTeamBusiness(teamBusiness);
    }
    
    private void replayAll() {
        replay(userBusiness, teamBusiness);
    }
    
    private void verifyAll() {
        verify(userBusiness, teamBusiness);
    }
    
    @Before
    public void setUp_data() {
        user = new User();
        user.setId(11);
    }
    
    @Test
    public void testRetrieve() {
        userAction.setUserId(user.getId());
        Team team = new Team();
        team.setName("Tiimi");
        Collection<Team> teamList = Arrays.asList(team);
        
        expect(userBusiness.retrieve(user.getId())).andReturn(user);
        expect(teamBusiness.retrieveAll()).andReturn(teamList);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, userAction.retrieve());
        assertEquals(user, userAction.getUser());
        assertEquals(teamList, userAction.getTeamList());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRetrieve_noSuchUser() {
        userAction.setUserId(-1);
        
        expect(userBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        userAction.retrieve();
        
        verifyAll();
    }
    
    @Test
    public void testRetrieveAll() {
        Collection<User> userList = Arrays.asList(new User(), new User());
        expect(userBusiness.retrieveAll()).andReturn(userList);
        replayAll();
        
        assertEquals(Action.SUCCESS, userAction.retrieveAll());
        assertEquals(userList, userAction.getUsers());
        
        verifyAll();
    }
}