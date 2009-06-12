package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Setting;

public class SettingActionTest {

    private SettingAction settingAction;
    private SettingBusiness settingBusiness;
    private Setting setting;
    
    @Before
    public void setUp() {
        settingAction = new SettingAction();
        settingBusiness = createMock(SettingBusiness.class);
        settingAction.setSettingBusiness(settingBusiness);
        
        setting = new Setting();
        setting.setName("Test");
        setting.setDescription("Test Description");
        setting.setId(123);
    }
    
    
    /**
     * Test create operation.
     */
    @Test
    public void testCreate() {
        assertEquals("success", settingAction.create());
        assertNotNull(settingAction.getSetting());
    }
    
    /**
     * Test delete operation.
     */
    @Test
    public void testDelete() {
        settingAction.setSettingId(setting.getId());
        
        expect(settingBusiness.retrieve(setting.getId()))
            .andReturn(setting);
        settingBusiness.delete(setting.getId());
        replay(settingBusiness);
        
        assertEquals("success", settingAction.delete());
        
        verify(settingBusiness);
    }
    
    @Test
    public void testDelete_invalidSetting() {
        settingAction.setSettingId(-1);
        
        expect(settingBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replay(settingBusiness);
        
        assertEquals("error", settingAction.delete());
        
        verify(settingBusiness);
    }

    
    /**
     * Test store operation.
     */
//    @Test
//    public void testStore() {
//        // execute edit operation
//        settingAction.setSettingId(setting.getId());
//        assertEquals("success", settingAction.edit());
//        
//        // update action fields
//        settingAction.setValue("true");
//        settingAction.setName("Rautaesirippu");
//
//        // execute store operation
//        assertEquals("success", settingAction.store());
////        assertEquals("Rautaesirippu", settingDAO.get(setting.getId()).getName());
//    }
    
    @Test
    public void testStore_happyCase() {
        settingAction.setSettingId(setting.getId());
        
        expect(settingBusiness.retrieve(setting.getId())).andReturn(setting);
        settingBusiness.store(setting);
        replay(settingBusiness);
        
        settingAction.store();
        
        verify(settingBusiness);
    }
    
    @Test
    public void testStore_invalidSettingId() {
        settingAction.setSettingId(124214);
        expect(settingBusiness.retrieve(124214)).andThrow(new ObjectNotFoundException());
        replay(settingBusiness);
        
        assertEquals("error", settingAction.store());
        
        verify(settingBusiness);
    }
    
    @Test
    public void testStore_withName() {
        settingAction.setName("Setting name");
        expect(settingBusiness.retrieveByName("Setting name")).andReturn(setting);
        settingBusiness.store(setting);
        replay(settingBusiness);
        
        settingAction.store();
        
        verify(settingBusiness);
    }
    
    @Test
    public void testStore_withInvalidName() {
        settingAction.setName("Setting name");
        expect(settingBusiness.retrieveByName("Setting name")).andReturn(null);
        replay(settingBusiness);
        
        assertEquals("error", settingAction.store());
        
        verify(settingBusiness);
    }
    
    @Test
    public void testStore_withActionErrors() {
        settingAction.addActionError("Test error");
        assertEquals("error", settingAction.store());
    }
    
   
    /**
     * Test edit operation.
     */
    @Test
    public void testEdit() {
        settingAction.setSettingId(setting.getId());
        
        expect(settingBusiness.retrieve(setting.getId()))
            .andReturn(setting);
        replay(settingBusiness);
        
        assertEquals("success", settingAction.edit());
        assertNotNull(settingAction.getSetting());
        
        verify(settingBusiness);
    }

}
