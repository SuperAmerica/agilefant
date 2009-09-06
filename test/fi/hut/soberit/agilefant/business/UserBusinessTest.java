package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.impl.UserBusinessImpl;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

public class UserBusinessTest {

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
    
    @Test
    public void testCalculateWorktimePerPeriod() {
        User user = new User();
        LocalDate start = new LocalDate(2009,6,1);
        Duration expected = new Duration(start.toDateMidnight(), start.plusDays(4).toDateMidnight());
        Interval interval = new Interval(start.toDateMidnight(), start.plusDays(4).toDateMidnight());
        Duration actual = this.userBusiness.calculateWorktimePerPeriod(user, interval);
        assertEquals(expected.getMillis(), actual.getMillis());
    }
    
    @Test
    public void testCalculateWorktimePerPeriod_inWeekend() {
        User user = new User();
        DateTime start = new DateTime(2009,9,5, 14, 50, 0, 0);
        Duration expected = new Duration(0);
        Interval interval = new Interval(start, start.plusDays(1).toDateMidnight());
        Duration actual = this.userBusiness.calculateWorktimePerPeriod(user, interval);
        assertEquals(expected.getMillis(), actual.getMillis());
    }
    
    @Test
    public void testCalculateWorktimePerPeriod_withWeekend() {
        User user = new User();
        LocalDate start = new LocalDate(2009,6,1);
        Duration expected = new Duration(start.toDateMidnight(), start.plusDays(6).toDateMidnight());
        Interval interval = new Interval(start.toDateMidnight(), start.plusDays(8).toDateMidnight());
        Duration actual = this.userBusiness.calculateWorktimePerPeriod(user, interval);
        assertEquals(expected.getMillis(), actual.getMillis());
    }
    
    @Test
    public void testCalculateWorktimePerPeriod_withVacations() {
        User user = new User();
        LocalDate start = new LocalDate(2009,6,1);
        
        Holiday holiday = new Holiday();
        holiday.setStartDate(start.plusDays(1).toDateMidnight().toDate());
        holiday.setEndDate(start.plusDays(3).toDateMidnight().toDate());
        user.getHolidays().add(holiday);        
        
        Duration expected = new Duration(start.toDateMidnight(), start.plusDays(4).toDateMidnight());
        Interval interval = new Interval(start.toDateMidnight(), start.plusDays(8).toDateMidnight());
        Duration actual = this.userBusiness.calculateWorktimePerPeriod(user, interval);
        assertEquals(expected.getMillis(), actual.getMillis());        
    }
}
