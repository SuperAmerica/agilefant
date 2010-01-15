package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.impl.HolidayBusinessImpl;
import fi.hut.soberit.agilefant.db.HolidayDAO;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class HolidayBusinessTest extends MockedTestCase {

    @TestedBean
    private HolidayBusinessImpl holidayBusiness;
    
    @Mock(strict=true)
    private HolidayDAO holidayDAO;
    
    @Test
    @DirtiesContext
    public void testRetrieveFutureHolidaysByUser() {
        User user = new User();
        List<Holiday> holidays = new ArrayList<Holiday>();
        
        expect(holidayDAO.retrieveFutureHolidaysByUser(user)).andReturn(holidays);
        
        replayAll();
        assertEquals(holidays, holidayBusiness.retrieveFutureHolidaysByUser(user));
        verifyAll();
    }
}
