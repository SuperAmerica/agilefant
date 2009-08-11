package fi.hut.soberit.agilefant.web;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;
import fi.hut.soberit.agilefant.transfer.UserLoadLimits;

public class UserLoadActionTest {
    private UserLoadAction userLoadAction;
    private UserBusiness userBusiness;
    private PersonalLoadBusiness personalLoadBusiness;
    
    @Before
    public void setUp() {
        this.userLoadAction = new UserLoadAction();
        this.userBusiness = createStrictMock(UserBusiness.class);
        this.personalLoadBusiness = createStrictMock(PersonalLoadBusiness.class);
        userLoadAction.setPersonalLoadBusiness(personalLoadBusiness);
        userLoadAction.setUserBusiness(userBusiness);
    }
    private void replayAll() {
        replay(userBusiness, personalLoadBusiness);
    }
    private void verifyAll() {
        verify(userBusiness, personalLoadBusiness);
    }
    
    @Test
    public void testRetrieveUserLoad() {
        User user = new User();
        ComputedLoadData loadData = new ComputedLoadData();
        expect(userBusiness.retrieve(1)).andReturn(user);
        expect(personalLoadBusiness.retrieveUserLoad(user, UserLoadAction.DEFAULT_LOAD_INTERVAL_LENGTH)).andReturn(loadData);
        userLoadAction.setUserId(1);
        replayAll();
        assertEquals(Action.SUCCESS, userLoadAction.retrieveUserLoad());
        assertNotNull(userLoadAction.getUserLoadData());
        verifyAll();
    }
    
    @Test
    public void testDailyLoadLimits() {
        User user = new User();
        UserLoadLimits limits = new UserLoadLimits();
        expect(userBusiness.retrieve(1)).andReturn(user);
        expect(personalLoadBusiness.getDailyLoadLimitsByUser(user)).andReturn(limits);
        replayAll();
        userLoadAction.setUserId(1);
        userLoadAction.dailyLoadLimits();
        assertEquals(limits, userLoadAction.getLoadLimits());
        verifyAll();
    }
}
