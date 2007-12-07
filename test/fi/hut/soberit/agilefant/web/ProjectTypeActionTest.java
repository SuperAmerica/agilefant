package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration testing class for testing class ProjectTypeAction. Heavily
 * under construction.
 * 
 * @author tvainiok
 */
public class ProjectTypeActionTest extends SpringTestCase {
    private static final String TEST_NAME1 = "Testi Act. Type no1";

    private static final String TEST_NAME2 = "Testi Act. Type no2";

    private static final String TEST_NAME3 = "Testi Act. Type no2";

    private static final String TEST_DESC1 = "Testi Activity Typen 1 kuvaus";

    private static final String TEST_DESC2 = "Testi Activity Typen 2 kuvaus";

    private static final String TEST_DESC3 = "Testi Activity Typen 3 kuvaus";

    private static final int INVALID_ID = -1;

    // The field and setter to be used by Spring
    private ProjectTypeAction projectTypeAction;

    public void setProjectTypeAction(ProjectTypeAction projectTypeAction) {
        this.projectTypeAction = projectTypeAction;
    }

    /*
     * Checks, if there are any given error countered.
     */
    private boolean errorFound(String e) {
        Collection<String> errors = projectTypeAction.getActionErrors();
        boolean found = false;
        for (String s : errors) {
            if (s.equals(e))
                found = true;
        }
        return found;
    }

    private void setContents(String name, String description) {
        ProjectType at = projectTypeAction.getProjectType();
        at.setName(name);
        at.setDescription(description);
    }

    /*
     * Method for calling projectTypeAction.create that is supposed to work
     * (and is not a target for testing) Actual testing for method create is
     * done in testCreate_XXX -methods
     */
    private void create() {
        String result = projectTypeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
    }

    /*
     * Method for calling projectTypeAction.store that is supposed to work (and
     * is not a target for testing) Actual testing for method store is done in
     * testStore_XXX -methods
     */
    private void store() {
        String result = projectTypeAction.store();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
    }

    /*
     * Get all stored Users. @return all users stored
     */
    private Collection<ProjectType> getAllProjectTypes() {
        projectTypeAction.getAll();
        return projectTypeAction.getProjectTypes();
    }

    /*
     * Get activity type based on details
     */
    private ProjectType getProjectType(String name, String desc) {
        for (ProjectType at : getAllProjectTypes()) {
            if (at.getDescription() == desc && at.getName() == name) {
                return at;
            }
        }
        return null;
    }

    /** * Actual test methods * */

    public void testCreate() {
        String result = projectTypeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
        super.assertEquals("New activity type had an invalid id", 0,
                projectTypeAction.getProjectTypeId());
    }

    public void testStore() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        int n = getAllProjectTypes().size();
        String result = projectTypeAction.store();
        assertEquals("store() was unsuccessful", result, Action.SUCCESS);
        super
                .assertEquals(
                        "The total number of stored activity types didn't grow up with store().",
                        n + 1, getAllProjectTypes().size());
        ProjectType storedAT = this.getProjectType(TEST_NAME1, TEST_DESC1);
        super.assertNotNull("Activity wasn't stored properly (wasn't found)",
                storedAT);
        super.assertEquals("Stored activity type had invalid name", TEST_NAME1,
                storedAT.getName());
        super.assertEquals("Stored activity type had invalid description",
                TEST_DESC1, storedAT.getDescription());
        // super.assertNotSame("The Stored activity type should have a proper id
        // number after store()",
        // 0, projectTypeAction.getProjectType().getId());
    }

    public void testEdit() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        projectTypeAction.setProjectType(null);
        projectTypeAction.setProjectTypeId(this.getProjectType(TEST_NAME1,
                TEST_DESC1).getId());
        String result = projectTypeAction.edit();
        super.assertEquals("edit() was unsuccessful", result, Action.SUCCESS);
        ProjectType fetchedAT = projectTypeAction.getProjectType();
        super.assertNotNull("Activity type fetched for editing was null",
                fetchedAT);
        super.assertEquals("Activity type for editing had invalid name",
                fetchedAT.getName(), TEST_NAME1);
        super.assertEquals("Activity type for editing had invalid description",
                fetchedAT.getDescription(), TEST_DESC1);

    }

    public void testEdit_withInvalidId() {
        projectTypeAction.setProjectTypeId(INVALID_ID);
        String result = projectTypeAction.edit();
        assertEquals("Invalid activity type  id didn't result an error.",
                Action.ERROR, result);
        assertTrue("projectType.notFound -error not found",
                errorFound(projectTypeAction.getText("projectType.notFound")));

    }

    /*
     * Change the description of previously stored activity type and update it.
     */
    public void testStore_withUpdate() {
        this.create();
        this.setContents(TEST_NAME1, TEST_DESC1);
        this.store();

        ProjectType at = this.getProjectType(TEST_NAME1, TEST_DESC1);
        at.setName(TEST_NAME2);
        at.setDescription(TEST_DESC2);
        projectTypeAction.setProjectTypeId(at.getId());
        projectTypeAction.setProjectType(at);
        String result = projectTypeAction.store();
        super.assertEquals("store() was unsuccessful", result, Action.SUCCESS);

        ProjectType updatedAT = this.getProjectType(TEST_NAME2, TEST_DESC2);
        super.assertNotNull(
                "Activity Type wasn't stored properly (wasn't found)",
                updatedAT);
        super.assertEquals("Activity type for editing had invalid name",
                updatedAT.getName(), TEST_NAME2);
        super.assertEquals("Activity type for editing had invalid description",
                updatedAT.getDescription(), TEST_DESC2);
    }

    /*
     * Create a new projectType and delete it. Then check that it is deleted.
     */
    public void testDelete() {
        this.create();
        this.setContents(TEST_NAME3, TEST_DESC3);
        this.store();
        ProjectType at = this.getProjectType(TEST_NAME3, TEST_DESC3);
        super.assertNotNull(
                "Activity Type wasn't stored properly (wasn't found)", at);

        projectTypeAction.setProjectTypeId(at.getId());
        String result = projectTypeAction.delete();
        assertEquals("delete() was unsuccessful", result, Action.SUCCESS);

        ProjectType test3 = this.getProjectType(TEST_NAME3, TEST_DESC3);
        super.assertNull("The deleted activity type wasn't properly deleted",
                test3);
    }
}
