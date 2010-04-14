package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class LoginContextActionTest extends MockedTestCase {

    @TestedBean
    private LoginContextAction testable;
    
    @Mock
    private SettingBusiness settingBusiness;
    
    @Mock
    private BacklogBusiness backlogBusiness;
    
    
    @Test
    @DirtiesContext
    public void testLoginContext_dailyWorkEnabled() {
        expect(backlogBusiness.countAll()).andReturn(154);
        expect(settingBusiness.isDailyWork()).andReturn(true);
        replayAll();
        assertEquals("dailyWork", testable.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testLoginContext_dailyWorkDisabled() {
        expect(backlogBusiness.countAll()).andReturn(13515);
        expect(settingBusiness.isDailyWork()).andReturn(false);
        replayAll();
        assertEquals("selectBacklog", testable.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testLoginContext_noBacklogs_dailyWorkDisabled() {
        expect(backlogBusiness.countAll()).andReturn(0);
        replayAll();
        assertEquals("help", testable.execute());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testLoginContext_noBacklogs_dailyWorkEnabled() {
        expect(backlogBusiness.countAll()).andReturn(0);
        replayAll();
        assertEquals("help", testable.execute());
        verifyAll();
    }
}
