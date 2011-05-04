package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.SpentEffortStatisticsBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.DailyUserSpentEffortTO;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class SpentEffortStatisticsActionTest extends MockedTestCase {

    @TestedBean
    SpentEffortStatisticsAction testable;
    
    @Mock
    SpentEffortStatisticsBusiness spentEffortStatisticsBusiness;
    
    @Mock
    UserBusiness userBusiness;
    
    @DirtiesContext
    @Test
    public void testretrieveMonthlyStatisticsByUser() {
        testable.setUserId(1);
        User user = new User();
        DateTime start = new DateTime().minusMonths(1).toDateMidnight().toDateTime();
        List<DailyUserSpentEffortTO> ret = new ArrayList<DailyUserSpentEffortTO>();
        
        expect(userBusiness.retrieve(1)).andReturn(user);
        expect(this.spentEffortStatisticsBusiness.retrieveByUser(user, start, 30)).andReturn(ret);
        
        replayAll();
        testable.retrieveMonthlyStatisticsByUser();
        assertEquals(ret, testable.getEntries());
        verifyAll();
    }
    
}
