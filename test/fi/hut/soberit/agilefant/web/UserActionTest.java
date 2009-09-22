package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

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
    
    @Before
    public void setUp_dependencies() {
        userAction = new UserAction();
        
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
        expect(userBusiness.storeUser(user, null, null)).andReturn(returned);
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
    public void testInitializePrefetchedData() {
        expect(userBusiness.retrieve(user.getId())).andReturn(user);
        replayAll();
        userAction.initializePrefetchedData(user.getId());
        verifyAll();
        assertEquals(user, userAction.getUser());
    }
}