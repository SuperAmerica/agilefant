package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.Setting;
import fi.hut.soberit.agilefant.util.SpringTestCase;

public class SettingActionTest extends SpringTestCase {

    private SettingDAO settingDAO = null;
    private SettingAction settingAction = null;
    private Setting setting = null;
    
    /**
     * Create test data.
     */
    public void onSetUpInTransaction() throws Exception {
        // Create a new setting.
        setting = new Setting();
        setting.setName("Test");
        setting.setDescription("Test Description");
        setting.setId((Integer) settingDAO.create(setting));
    }
    
    /**
     * Test create operation.
     */
    public void testCreate() {
        assertEquals("success", settingAction.create());
        assertNotNull(settingAction.getSetting());
    }
    
    /**
     * Test delete operation.
     */
    public void testDelete() {
        settingAction.setSettingId(setting.getId());
        assertEquals("success", settingAction.delete());
        assertNull(settingDAO.get(setting.getId()));
    }

    /**
     * Test delete operation with a wrong setting id.
     */
    public void testDelete_withWrongSettingId() {
        settingAction.setSettingId(-1);
        assertEquals("error", settingAction.delete());
        assertNotNull(settingDAO.get(setting.getId()));
    }

    
    /**
     * Test store operation.
     */
    public void testStore() {
        // execute edit operation
        settingAction.setSettingId(setting.getId());
        assertEquals("success", settingAction.edit());
        
        // update action fields
        settingAction.setValue("true");
        settingAction.setName("Rautaesirippu");

        // execute store operation
        assertEquals("success", settingAction.store());
        assertEquals("Rautaesirippu", settingDAO.get(setting.getId()).getName());
    }
    
    /**
     * Test store operation with wrong setting id.
     * 
     */
    public void testStore_withWrongSettingId() {
        // execute edit operation
        settingAction.setSettingId(-1);
        assertEquals("error", settingAction.edit());
        
        // update action fields
        settingAction.setValue("true");
        settingAction.setName("Rautaesirippu");

        // execute store operation
        assertEquals("error", settingAction.store());
        assertEquals("Test", settingDAO.get(setting.getId()).getName());
    }
    
    /**
     * Test edit operation.
     */
    public void testEdit() {
        settingAction.setSettingId(setting.getId());
        assertEquals("success", settingAction.edit());
        assertNotNull(settingAction.getSetting());
        assertEquals("Test", settingAction.getSetting().getName());
        assertEquals("Test Description", settingAction.getSetting()
                .getDescription());
    }
    
    public void setSettingDAO(SettingDAO settingDAO) {
        this.settingDAO = settingDAO;
    }

    public void setSettingAction(SettingAction settingAction) {
        this.settingAction = settingAction;
    }
}
