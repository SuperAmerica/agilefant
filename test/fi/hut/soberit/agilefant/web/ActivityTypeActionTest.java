package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration testing class for testing class ActivityTypeAction. Heavily
 * under construction.
 * 
 * @author tvainiok
 */
public class ActivityTypeActionTest extends SpringTestCase {
    private static final String TEST_NAME1 = "Testi Act. Type no1";

    private static final String TEST_NAME2 = "Testi Act. Type no2";

    private static final String TEST_NAME3 = "Testi Act. Type no2";

    private static final String TEST_DESC1 = "Testi Activity Typen 1 kuvaus";

    private static final String TEST_DESC2 = "Testi Activity Typen 2 kuvaus";

    private static final String TEST_DESC3 = "Testi Activity Typen 3 kuvaus";

    private static final int INVALID_ID = -1;

    // The field and setter to be used by Spring
    private ActivityTypeAction activityTypeAction;

    public void setActivityTypeAction(ActivityTypeAction activityTypeAction) {
        this.activityTypeAction = activityTypeAction;
    }

    /*
     * Checks, if there are any given error countered.
     */
    private boolean errorFound(String e) {
        Collection<String> errors = activityTypeAction.getActionErrors();
        boolean found = false;
        for (String s : errors) {
            if (s.equals(e))
                found = true;
        }
        return found;
    }

    private void setContents(String name, String description) {
        ActivityType at = activityTypeAction.getActivityType();
        at.setName(name);
        at.setDescription(description);
    }

    /*
     * Method for calling activityTypeAction.create that is supposed to work
     * (and is not a target for testing) Actual testing for method create is
     * done in testCreate_XXX -methods
     */
    private void create() {
        String result = activityTypeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
    }

    /*
     * Method for calling activityTypeAction.store that is supposed to work (and
     * is not a target for testing) Actual testing for method store is done in
     * testStore_XXX -methods
     */
    private void store() {
        String result = activityTypeAction.store();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
    }

    /*
     * Get all stored Users. @return all users stored
     */
    private Collection<ActivityType> getAllActivityTypes() {
        activityTypeAction.getAll();
        return activityTypeAction.getActivityTypes();
    }

    /*
     * Get activity type based on details
     */
    private ActivityType getActivityType(String name, String desc) {
        for (ActivityType at : getAllActivityTypes()) {
            if (at.getDescription() == desc && at.getName() == name) {
                return at;
            }
        }
        return null;
    }

    /** * Actual test methods * */

    public void testCreate() {
        String result = activityTypeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
        super.assertEquals("New activity type had an invalid id", 0,
                activityTypeAction.getActivityTypeId());
    }

    public void testStore() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        int n = getAllActivityTypes().size();
        String result = activityTypeAction.store();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
        super
                .assertEquals(
                        "The total number of stored activity types didn't grow up with store().",
                        n + 1, getAllActivityTypes().size());
        ActivityType storedAT = this.getActivityType(TEST_NAME1, TEST_DESC1);
        super.assertNotNull("Activity wasn't stored properly (wasn't found)",
                storedAT);
        super.assertEquals("Stored activity type had invalid name", TEST_NAME1,
                storedAT.getName());
        super.assertEquals("Stored activity type had invalid description",
                TEST_DESC1, storedAT.getDescription());
        // super.assertNotSame("The Stored activity type should have a proper id
        // number after store()",
        // 0, activityTypeAction.getActivityType().getId());
    }

    public void testEdit() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        activityTypeAction.setActivityType(null);
        activityTypeAction.setActivityTypeId(this.getActivityType(TEST_NAME1,
                TEST_DESC1).getId());
        String result = activityTypeAction.edit();
        super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
        ActivityType fetchedAT = activityTypeAction.getActivityType();
        super.assertNotNull("Activity type fetched for editing was null",
                fetchedAT);
        super.assertEquals("Activity type for editing had invalid name",
                fetchedAT.getName(), TEST_NAME1);
        super.assertEquals("Activity type for editing had invalid description",
                fetchedAT.getDescription(), TEST_DESC1);

    }

    public void testEdit_withInvalidId() {
        activityTypeAction.setActivityTypeId(INVALID_ID);
        String result = activityTypeAction.edit();
        assertEquals("Invalid activity type  id didn't result an error.",
                Action.ERROR, result);
        assertTrue("activityType.notFound -error not found",
                errorFound(activityTypeAction.getText("activityType.notFound")));

    }

    /*
     * Change the description of previously stored activity type and update it.
     */
    public void testStore_withUpdate() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        ActivityType at = this.getActivityType(TEST_NAME1, TEST_DESC1);
        at.setName(TEST_NAME2);
        at.setDescription(TEST_DESC2);
        activityTypeAction.setActivityTypeId(at.getId());
        activityTypeAction.setActivityType(at);
        String result = activityTypeAction.store();
        super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);

        ActivityType updatedAT = this.getActivityType(TEST_NAME2, TEST_DESC2);
        super.assertNotNull(
                "Activity Type wasn't stored properly (wasn't found)",
                updatedAT);
        super.assertEquals("Activity type for editing had invalid name",
                updatedAT.getName(), TEST_NAME2);
        super.assertEquals("Activity type for editing had invalid description",
                updatedAT.getDescription(), TEST_DESC2);
    }

    /*
     * Create a new activityType and delete it. Then check that it is deleted.
     */
    public void testDelete() {
        this.create();
        this.setContents(TEST_NAME3, TEST_DESC3);
        this.store();
        ActivityType at = this.getActivityType(TEST_NAME3, TEST_DESC3);
        super.assertNotNull(
                "Activity Type wasn't stored properly (wasn't found)", at);

        activityTypeAction.setActivityTypeId(at.getId());
        String result = activityTypeAction.delete();
        assertEquals("delete() was unsuccessful", result, Action.SUCCESS);

        ActivityType test3 = this.getActivityType(TEST_NAME3, TEST_DESC3);
        super.assertNull("The deleted activity type wasn't properly deleted",
                test3);
    }
}
