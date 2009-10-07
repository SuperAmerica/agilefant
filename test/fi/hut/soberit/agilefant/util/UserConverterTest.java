package fi.hut.soberit.agilefant.util;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.User;

public class UserConverterTest {
    UserConverter testable;
    UserBusiness userBusiness;
    private User user;
    
    @Before
    public void setUp() {
        testable = new UserConverter();
        userBusiness = createStrictMock(UserBusiness.class);
        
        user = new User();
        user.setId(5);
        
        testable.setUserBusiness(userBusiness);
    }
    
    private void replayAll() {
        replay(userBusiness);
    }

    private void verifyAll() {
        verify(userBusiness);
    }
    
    @Test
    public void testConvertFromString_withValidId() {
        expect(userBusiness.retrieve(5)).andReturn(user);
        replayAll();
        User returned = (User)testable.convertFromString(null, new String[] { "5" }, User.class);
        
        verifyAll();
        assertSame(user, returned);
    }

    @Test(expected = NumberFormatException.class)
    public void testConvertFromString_invalidId() {
        testable.convertFromString(null, new String[] { "5sadföwq3rio" }, User.class);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testConvertFromString_notFound() {
        expect(userBusiness.retrieve(5)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.convertFromString(null, new String[] { "5" }, User.class);
    }
    
    @Test
    public void testConvertToString() {
        String str = testable.convertToString(null, user);
        assertEquals(str, user.toString());
    }
}
