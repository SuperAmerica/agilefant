package fi.hut.soberit.agilefant.web;

import static org.junit.Assert.*;
import org.junit.Test;
import static org.easymock.EasyMock.*;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.PasswordBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;

public class PasswordActionTest {
    // For simplicity this class does not extend SpringTestCase

    private final static int TESTUSER_ID = 100;

    private User createTestUser() {
        User user = new User();
        user.setEmail("keimo.lantio@somewhere.com");
        user.setLoginName("keimolantio");
        user.setId(TESTUSER_ID);
        return user;
    }

    @Test
    public void testGenerate() {
        UserBusiness userBusiness = createMock(UserBusiness.class);
        PasswordBusiness passwordBusiness = createMock(PasswordBusiness.class);
        passwordBusiness.generateAndMailPassword(TESTUSER_ID);
        expect(userBusiness.getUser("keimolantio")).andReturn(createTestUser());
        replay(passwordBusiness, userBusiness);

        PasswordAction action = new PasswordAction();
        action.setUserBusiness(userBusiness);
        action.setPasswordBusiness(passwordBusiness);
        action.setName("keimolantio");
        action.setEmail("keimo.lantio@somewhere.com");

        assertEquals(Action.SUCCESS, action.generate());
        verify(passwordBusiness, userBusiness);
    }

    @Test
    public void testGenerateWithUnknownName() {
        UserBusiness userBusiness = createMock(UserBusiness.class);
        expect(userBusiness.getUser("leimokantio")).andReturn(null);
        replay(userBusiness);

        PasswordAction action = new PasswordAction();
        action.setUserBusiness(userBusiness);
        action.setName("leimokantio");
        action.setEmail("keimo.lantio@somewhere.com");

        assertEquals(Action.ERROR, action.generate());
        verify(userBusiness);
    }

    @Test
    public void testGenerateWithWrongEmail() {
        UserBusiness userBusiness = createMock(UserBusiness.class);
        expect(userBusiness.getUser("keimolantio")).andReturn(createTestUser());
        replay(userBusiness);

        PasswordAction action = new PasswordAction();
        action.setUserBusiness(userBusiness);
        action.setName("keimolantio");
        action.setEmail("leimo.kantio@somewhere.com");

        assertEquals(Action.ERROR, action.generate());
        verify(userBusiness);
    }

}
