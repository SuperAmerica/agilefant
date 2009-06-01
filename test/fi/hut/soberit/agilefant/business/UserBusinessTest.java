package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.UserBusinessImpl;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

public class UserBusinessTest extends TestCase {

    UserBusinessImpl userBusiness = new UserBusinessImpl();
    UserDAO userDAO;
    
    @Before
    public void setUp() {
        userDAO = createMock(UserDAO.class);
        userBusiness.setUserDAO(userDAO);
    }
    
    @Test
    public void testGetEnabledUsers_interaction() {
        List<User> listOfEnabledUsers = Arrays.asList(new User());
        expect(userDAO.listUsersByEnabledStatus(true)).andReturn(listOfEnabledUsers);
        replay(userDAO);
        
        assertSame(listOfEnabledUsers, userBusiness.getEnabledUsers());
        
        verify(userDAO);
    }
    
    @Test
    public void testGetDisabledUsers_interaction() {
        List<User> listOfDisabledUsers = Arrays.asList(new User());
        expect(userDAO.listUsersByEnabledStatus(false)).andReturn(listOfDisabledUsers);
        replay(userDAO);
        
        assertSame(listOfDisabledUsers, userBusiness.getDisabledUsers());
        
        verify(userDAO);
    }
}
