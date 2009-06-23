package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.SettingBusiness;


public class SettingActionTest {
    public SettingAction testable;
    public SettingBusiness settingBusiness;
    
    @Before
    public void setUp() {
        settingBusiness = createMock(SettingBusiness.class);
        testable = new SettingAction();
        testable.setSettingBusiness(settingBusiness);
    }
    
    @Test
    public void testValidateLoadMeterValues() {
        testable.setRangeLow(50);
        testable.setOptimalLow(40);
        testable.setOptimalHigh(30);
        testable.setCriticalLow(20);
        testable.setRangeHigh(10);
        
        assertFalse(testable.validateLoadMeterValues());
        testable.setRangeLow(10);
        
        assertFalse(testable.validateLoadMeterValues());
        testable.setOptimalLow(20);
        
        assertFalse(testable.validateLoadMeterValues());
        testable.setOptimalHigh(30);
        
        assertFalse(testable.validateLoadMeterValues());
        testable.setCriticalLow(40);
        
        assertFalse(testable.validateLoadMeterValues());
        testable.setRangeHigh(50);
        
        assertTrue(testable.validateLoadMeterValues());
    }
    
    @Test
    public void testInitilizeEmptyLoadMeterValues_empty() {
        testable.initilizeEmptyLoadMeterValues();
        assertEquals(SettingBusiness.DEFAULT_CRITICAL_LOW, testable.getCriticalLow());
        assertEquals(SettingBusiness.DEFAULT_OPTIMAL_HIGH, testable.getOptimalHigh());
        assertEquals(SettingBusiness.DEFAULT_OPTIMAL_LOW, testable.getOptimalLow());
        assertEquals(SettingBusiness.DEFAULT_RANGE_HIGH, testable.getRangeHigh());
        assertEquals(SettingBusiness.DEFAULT_RANGE_LOW, testable.getRangeLow());
    }
    
    @Test
    public void testInitilizeEmptyLoadMeterValues() {
        testable.setRangeLow(5);
        testable.setOptimalLow(3);
        testable.setOptimalHigh(2);
        testable.setCriticalLow(1);
        testable.setRangeHigh(4);
        testable.initilizeEmptyLoadMeterValues();
        assertEquals(1, testable.getCriticalLow());
        assertEquals(2, testable.getOptimalHigh());
        assertEquals(3, testable.getOptimalLow());
        assertEquals(4, testable.getRangeHigh());
        assertEquals(5, testable.getRangeLow());
    }



}
