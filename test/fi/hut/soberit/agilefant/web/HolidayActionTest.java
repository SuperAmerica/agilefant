package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.HolidayBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.SpringAssertions;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class HolidayActionTest extends MockedTestCase {

    @TestedBean
    private HolidayAction holidayAction;
    
    @Mock(strict=true)
    private UserBusiness userBusiness;
    
    @Mock(strict=true)
    private HolidayBusiness holidayBusiness;
    
    private Holiday holiday;
    
    @Before
    public void setUp() {
        this.holiday = new Holiday();
    }
    
    @Test
    @DirtiesContext
    public void testSpringScope() {
        SpringAssertions.assertScopeAnnotation("prototype",
                HolidayAction.class);
    }
    
    @Test
    @DirtiesContext
    public void testStore() {
        holidayBusiness.store(holiday);
        replayAll();
        holidayAction.setHoliday(this.holiday);
        assertEquals(Action.SUCCESS, holidayAction.store());
        verifyAll();
    }
    
    @Test 
    @DirtiesContext
    public void testRetrieve() {
        expect(holidayBusiness.retrieve(10)).andReturn(this.holiday);
        replayAll();
        holidayAction.setHolidayId(10);
        assertEquals(Action.SUCCESS, holidayAction.retrieve());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testFutureUserHolidays() {
        User user = new User();
        List<Holiday> holidays = new ArrayList<Holiday>();
        expect(userBusiness.retrieve(4)).andReturn(user);
        expect(holidayBusiness.retrieveFutureHolidaysByUser(user)).andReturn(holidays);
        replayAll();
        holidayAction.setUserId(4);
        assertEquals(Action.SUCCESS, holidayAction.futureUserHolidays());
        verifyAll();
        assertEquals(holidays, holidayAction.getUserHolidays());
    }
}
