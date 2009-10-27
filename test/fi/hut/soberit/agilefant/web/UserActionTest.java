package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.User;

public class UserActionTest {
    
    // Class under test
    UserAction userAction;
    
    // Dependencies
    UserBusiness userBusiness;
    
    // Test data
    User user;
    
    @SuppressWarnings("serial")
    @Before
    public void setUp_dependencies() {
        userAction = new UserAction() {
            @Override
            protected int getLoggedInUserId() {
                return 817;
            }
        };
        
        userBusiness = createStrictMock(UserBusiness.class);
        userAction.setUserBusiness(userBusiness);
    }
    
    private void replayAll() {
        replay(userBusiness);
    }
    
    private void verifyAll() {
        verify(userBusiness);
    }
    
    @Before
    public void setUp_data() {
        user = new User();
        user.setId(11);
    }
    
    @Test
    public void testExecute_defaultUser() {
        userAction.setUserId(0);
        assertEquals(Action.SUCCESS, userAction.execute());
        assertEquals(817, userAction.getUserId());
    }
    
    @Test
    public void testExecute_userSet() {
        userAction.setUserId(555);
        assertEquals(Action.SUCCESS, userAction.execute());
        assertEquals(555, userAction.getUserId());
    }
    
    @Test
    public void testRetrieve() {
        userAction.setUserId(user.getId());
        
        expect(userBusiness.retrieve(user.getId())).andReturn(user);
       
        replayAll();
        assertEquals(Action.SUCCESS, userAction.retrieve());
        assertEquals(user, userAction.getUser());
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
    
    @Test
    public void testStore() {
        User returned = new User();
        userAction.setUser(user);
        userAction.setTeamIds(new HashSet<Integer>(Arrays.asList(1,2,3)));
        userAction.setTeamsChanged(false);
        
        /* 
         * Should actually be same, or error is thrown.
         * Used for testing purposes.
         */
        userAction.setPassword1("new password");
        userAction.setPassword2("new password 2");
        expect(userBusiness.storeUser(user, null, "new password", "new password 2")).andReturn(returned);
        
        replayAll();
        assertEquals(Action.SUCCESS, userAction.store());
        verifyAll();
        
        assertEquals(returned, userAction.getUser());
    }
    
    @Test
    public void testStore_changeTeams() {
        User returned = new User();
        userAction.setUser(user);
        Set<Integer> teamIds = new HashSet<Integer>(Arrays.asList(1,2,3));
        userAction.setTeamIds(teamIds);
        userAction.setTeamsChanged(true);

        expect(userBusiness.storeUser(user, teamIds, null, null)).andReturn(returned);
        
        replayAll();
        assertEquals(Action.SUCCESS, userAction.store());
        verifyAll();
        
        assertEquals(returned, userAction.getUser());
    }
    
    @Test
    public void testDelete() {
        userAction.setUserId(user.getId());
        expect(userBusiness.retrieve(user.getId())).andReturn(user);
        replayAll();
        assertEquals(Action.SUCCESS, userAction.delete());
        verifyAll();
    }
    
    @Test
    public void testCheckUserName_userExists() {
        userAction.setLoginName("paavo");
        expect(userBusiness.isLoginNameUnique("paavo")).andReturn(false);
        replayAll();
        assertEquals(Action.SUCCESS, userAction.checkLoginName());
        verifyAll();
        assertFalse(userAction.isValid());
    }
    
    @Test
    public void testCheckUserName_userDoesNotExists() {
        userAction.setLoginName("minna");
        expect(userBusiness.isLoginNameUnique("minna")).andReturn(true);
        replayAll();
        assertEquals(Action.SUCCESS, userAction.checkLoginName());
        verifyAll();
        assertTrue(userAction.isValid());
    }
}