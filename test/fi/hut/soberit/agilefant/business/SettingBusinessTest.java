package fi.hut.soberit.agilefant.business;

import junit.framework.TestCase;
import org.easymock.EasyMock;
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
    
    /*
     * An argument matcher for EasyMock. See SettingEquals.java for more information.
     */
    public static Setting eqSetting(Setting in){
        EasyMock.reportMatcher(new SettingEquals(in));
        return null;
    }
    
    public void testSetHourReporting_SettingExists() {
        final String HourReportingName = "HourReporting";
        Setting setting = new Setting();
        Setting parameterSetting = new Setting();
        settingDAO = createMock(SettingDAO.class);
        testable = new SettingBusinessImpl();
        testable.setSettingDAO(settingDAO);
        
        setting.setName(HourReportingName);
        setting.setValue("false");
        
        parameterSetting.setName(HourReportingName);
        parameterSetting.setValue("true");
        
        expect(settingDAO.getSetting(HourReportingName)).andReturn(setting);
        settingDAO.store(eqSetting(parameterSetting));
        replay(settingDAO);
        testable.setHourReporting("true");
        verify();
    }
    
    public void testSetHourReporting_SettingDoesNotExist() {
        final String HourReportingName = "HourReporting";
        Setting parameterSetting = new Setting();
        settingDAO = createMock(SettingDAO.class);
        testable = new SettingBusinessImpl();
        testable.setSettingDAO(settingDAO);
    
        parameterSetting.setName(HourReportingName);
        parameterSetting.setValue("true");
        
        expect(settingDAO.getSetting(HourReportingName)).andReturn(null);
        expect(settingDAO.create(eqSetting(parameterSetting))).andReturn(1);
        replay(settingDAO);
        testable.setHourReporting("true");
        verify();
    }

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
