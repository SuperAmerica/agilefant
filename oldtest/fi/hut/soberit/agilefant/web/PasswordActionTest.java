package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.PasswordBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;

public class PasswordActionTest extends TestCase {
    // For simplicity this class does not extend SpringTestCase

    private final static int TESTUSER_ID = 100;

    private UserBusiness userBusiness;

    private PasswordBusiness passwordBusiness;

    private PasswordAction action;

    @Override
    public void setUp() {
        userBusiness = createMock(UserBusiness.class);
        passwordBusiness = createMock(PasswordBusiness.class);
        action = new PasswordAction();
        action.setUserBusiness(userBusiness);
        action.setPasswordBusiness(passwordBusiness);
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("keimo.lantio@somewhere.com");
        user.setLoginName("keimolantio");
        user.setId(TESTUSER_ID);
        return user;
    }

    public void testGenerate() {
        passwordBusiness.generateAndMailPassword(TESTUSER_ID);
        expect(userBusiness.getUser("keimolantio")).andReturn(createTestUser());
        replay(passwordBusiness, userBusiness);

        action.setName("keimolantio");
        action.setEmail("keimo.lantio@somewhere.com");

        assertEquals(Action.SUCCESS, action.generate());
        verify(passwordBusiness, userBusiness);
    }

    public void testGenerateWithUnknownName() {
        expect(userBusiness.getUser("leimokantio")).andReturn(null);
        replay(passwordBusiness, userBusiness);

        action.setName("leimokantio");
        action.setEmail("keimo.lantio@somewhere.com");

        assertEquals(Action.ERROR, action.generate());
        verify(passwordBusiness, userBusiness);
    }

    public void testGenerateWithWrongEmail() {
        expect(userBusiness.getUser("keimolantio")).andReturn(createTestUser());
        replay(passwordBusiness, userBusiness);

        action.setName("keimolantio");
        action.setEmail("leimo.kantio@somewhere.com");

        assertEquals(Action.ERROR, action.generate());
        verify(passwordBusiness, userBusiness);
    }

}
