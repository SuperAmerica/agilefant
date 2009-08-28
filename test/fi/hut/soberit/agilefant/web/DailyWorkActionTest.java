package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class DailyWorkActionTest {
    private DailyWorkAction testable;

        
    private DailyWorkBusiness dailyWorkBusiness;
    private UserBusiness userBusiness;
    
    @Before
    public void setUp_dependencies() {
        testable = new DailyWorkAction();
        
        dailyWorkBusiness = createStrictMock(DailyWorkBusiness.class);
        testable.setDailyWorkBusiness(dailyWorkBusiness);

        userBusiness = createStrictMock(UserBusiness.class);
        testable.setUserBusiness(userBusiness);
    }
    
    private void replayAll() {
        replay(dailyWorkBusiness, userBusiness);
    }

    private void verifyAll() {
        verify(dailyWorkBusiness, userBusiness);
    }
    
    @Test
    public void testRetrieve() {
        User user = new User(); 
        testable.setUserId(1);
        
        Collection<Task> returnedList = Arrays.asList(new Task(), new Task());
        
        List<User> users = getUserList();
        User u1 = users.get(0);
        User u2 = users.get(1);
        
        expect(userBusiness.getEnabledUsers()).andReturn(users);
        expect(userBusiness.retrieve(1)).andReturn(user);
        expect(dailyWorkBusiness.getDailyTasksForUser(user))
            .andReturn(returnedList);       
        
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.retrieve());

        verifyAll();
        
        assertEquals(returnedList, testable.getAssignedTasks());
        
        Collection<User> usersReturned = testable.getEnabledUsers();
        assertEquals(usersReturned.size(), 2);
        assertTrue(usersReturned.contains(u1));
        assertTrue(usersReturned.contains(u2));
    }
    
    private List<User> getUserList() {
        List<User> users = new ArrayList<User>();
        User u1 = new User();
        u1.setId(5);
        u1.setFullName("Antti Haapala");

        User u2 = new User();
        u1.setId(9);
        u1.setFullName("Pentti Hirvonen");

        users.add(u1);
        users.add(u2);
        return users;
    }
    
    @Test(expected=ObjectNotFoundException.class)
    public void testRetrieve_UserNotFound() {
        int id = 123123123;
        testable.setUserId(id);
 
        expect(userBusiness.getEnabledUsers()).andReturn(getUserList());
        expect(userBusiness.retrieve(id)).andThrow(new ObjectNotFoundException());
        
        replayAll();

        testable.retrieve();
        
        verifyAll();
    }
}
