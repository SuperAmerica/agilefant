package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.util.SpringTestCase;

public class BusinessThemeActionTest extends SpringTestCase {
    private static final String TEST_NAME1 = "test_1";

    private static final String TEST_NAME2 = "test_2";

    private static final String TEST_NAME3 = "test_3";

    private static final String TEST_DESC1 = "Testi1";

    private static final String TEST_DESC2 = "Testi2";

    private static final String TEST_DESC3 = "Testi3";
    
    private BusinessThemeAction businessThemeAction;

    public void setBusinessThemeAction(BusinessThemeAction businessThemeAction) {
        this.businessThemeAction = businessThemeAction;
    }
    
    private void setContents(String name, String description) {
        BusinessTheme theme = businessThemeAction.getBusinessTheme();
        theme.setName(name);
        theme.setDescription(description);
    }
    
    private void create() {
        String result = businessThemeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
    }
    
    private void store() {
        String result = businessThemeAction.store();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
    }
    
    private BusinessTheme getBusinessTheme(String name, String desc) {
        businessThemeAction.list();
        for (BusinessTheme bt : businessThemeAction.getBusinessThemes()) {
            if (bt.getDescription() == desc && bt.getName() == name) {
                return bt;
            }
        }
        return null;
    }
    
    public void testCreate() {
        String result = businessThemeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
        super.assertEquals("New activity type had an invalid id", 0,
                businessThemeAction.getBusinessThemeId());
    }
    
    public void testStore() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        businessThemeAction.list();
        int n = businessThemeAction.getBusinessThemes().size();
        String result = businessThemeAction.store();
        businessThemeAction.list();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
        super
                .assertEquals(
                        "The total number of stored activity types didn't grow up with store().",
                        n + 1, businessThemeAction.getBusinessThemes().size());
        BusinessTheme storedAT = this.getBusinessTheme(TEST_NAME1, TEST_DESC1);
        super.assertNotNull("Activity wasn't stored properly (wasn't found)",
                storedAT);
        super.assertEquals("Stored activity type had invalid name", TEST_NAME1,
                storedAT.getName());
        super.assertEquals("Stored activity type had invalid description",
                TEST_DESC1, storedAT.getDescription());       
    }
    
    public void testEdit() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        businessThemeAction.setBusinessTheme(null);
        businessThemeAction.setBusinessThemeId(this.getBusinessTheme(TEST_NAME1,
                TEST_DESC1).getId());
        String result = businessThemeAction.edit();
        super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
        BusinessTheme fetchedAT = businessThemeAction.getBusinessTheme();
        super.assertNotNull("Business theme fetched for editing was null",
                fetchedAT);
        super.assertEquals("Business theme for editing had invalid name",
                fetchedAT.getName(), TEST_NAME1);
        super.assertEquals("Business theme for editing had invalid description",
                fetchedAT.getDescription(), TEST_DESC1);

    }
    
    public void testStore_withUpdate() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        BusinessTheme at = this.getBusinessTheme(TEST_NAME1, TEST_DESC1);
        at.setName(TEST_NAME2);
        at.setDescription(TEST_DESC2);
        businessThemeAction.setBusinessThemeId(at.getId());
        businessThemeAction.setBusinessTheme(at);
        String result = businessThemeAction.store();
        super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);

        BusinessTheme updatedAT = this.getBusinessTheme(TEST_NAME2, TEST_DESC2);
        super.assertNotNull(
                "Business theme wasn't stored properly (wasn't found)",
                updatedAT);
        super.assertEquals("Business theme for editing had invalid name",
                updatedAT.getName(), TEST_NAME2);
        super.assertEquals("Business theme for editing had invalid description",
                updatedAT.getDescription(), TEST_DESC2);
    }
    
    public void testDelete() {        
        this.create();
        this.setContents(TEST_NAME3, TEST_DESC3);
        this.store();        
        BusinessTheme at = this.getBusinessTheme(TEST_NAME3, TEST_DESC3);
        super.assertNotNull(
                "Business theme wasn't stored properly (wasn't found)", at);

        businessThemeAction.setBusinessThemeId(at.getId());
        businessThemeAction.setBusinessTheme(at);
        String result = businessThemeAction.delete();
        assertEquals("delete() was unsuccessful", result, Action.SUCCESS);        
    }
    
}
