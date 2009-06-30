package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;

import com.opensymphony.xwork2.Action;

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

    private void runTest(User returnedUser, String name, String email, String result) {
        expect(userBusiness.retrieveByLoginName(name)).andReturn(returnedUser);
        replay(passwordBusiness, userBusiness);

        action.setName(name);
        action.setEmail(email);

        assertEquals(result, action.generate());
        verify(passwordBusiness, userBusiness);
    }
    
    public void testGenerate() {
        passwordBusiness.generateAndMailPassword(TESTUSER_ID);
        runTest(createTestUser(), "keimolantio", "keimo.lantio@somewhere.com", Action.SUCCESS);
    }

    public void testGenerateWithUnknownName() {
        runTest(null, "leimokantio", "keimo.lantio@somewhere.com", Action.ERROR);
    }

    public void testGenerateWithWrongEmail() {
        runTest(createTestUser(), "keimolantio", "leimo.kantio@somewhere.com", Action.ERROR);
    }
}
