package fi.hut.soberit.agilefant.business;

import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import fi.hut.soberit.agilefant.business.impl.SettingBusinessImpl;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;

public class SettingBusinessTest extends TestCase {

    private SettingDAO settingDAO;  
    private SettingBusinessImpl testable;
    

    /**
     * Test for hour reporting configuration entry
     */
    public void testIsHourReportingEnabled() {
        
        //setup
        Setting item = new Setting();
        item.setName("HourReporting");
        item.setValue("false");
        settingDAO = createMock(SettingDAO.class);
        //test when reporting is turned off
        expect(settingDAO.getSetting("HourReporting")).andReturn(item);
        testable = new SettingBusinessImpl();
        testable.setSettingDAO(settingDAO);
        replay(settingDAO);
        try {
            assertFalse(testable.isHourReportingEnabled());
            verify();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        reset(settingDAO);
        
        //test when reporting is on
        item.setValue("true");
        expect(settingDAO.getSetting("HourReporting")).andReturn(item);
        replay(settingDAO);
        try {
            assertTrue(testable.isHourReportingEnabled());
            verify();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        reset(settingDAO);
        
        // test when missing the configuration directive for hour reporting
        expect(settingDAO.getSetting("HourReporting")).andReturn(null);
        replay(settingDAO);
        try {
            assertFalse(testable.isHourReportingEnabled());
            verify();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
    
    
}
