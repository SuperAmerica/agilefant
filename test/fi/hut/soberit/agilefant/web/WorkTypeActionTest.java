package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.WorkType;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration testing class for testing class UserAction
 * 
 * @author tvainiok
 */
public class WorkTypeActionTest extends SpringTestCase {
    private static final String TEST_NAME = "WorkType name";

    private static final String TEST_DESCRIPTION = "WorkType description";

    private static final String PROJECTTYPE_TEST_NAME = "ProjectType name";

    private static final String PROJECTTYPE_TEST_DESCRIPTION = "ProjectType name";

    private static final int PROJECTTYPE_TEST_PERCENTAGE = 23;

    WorkTypeDAO workTypeDAO;

    // The field and setter to be used by Spring
    private WorkTypeAction workTypeAction;

    private ProjectTypeDAO projectTypeDAO;

    public void setWorkTypeAction(WorkTypeAction workTypeAction) {
        this.workTypeAction = workTypeAction;
    }

    public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
        this.workTypeDAO = workTypeDAO;
    }

    /*
     * Checks, if there are any given error countered.
     */
    @SuppressWarnings("unchecked")
    private boolean errorFound(String e) {
        Collection<String> errors = workTypeAction.getActionErrors();
        boolean found = false;
        for (String s : errors) {
            if (s.equals(e))
                found = true;
        }
        return found;
    }

    private void configWorkType(String name, String description,
            ProjectType projectType) {
        workTypeAction.getWorkType().setName(name);
        workTypeAction.getWorkType().setDescription(description);
        workTypeAction.getWorkType().setProjectType(projectType);
        workTypeAction.setProjectTypeId(projectType.getId());
    }

    /*
     * Method for calling workTypeAction.create that is supposed to work (and is
     * not a target for testing) Actual testing for method create is done in
     * testCreate_XXX -methods
     */
    private void create() {
        String result = workTypeAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
    }

    /*
     * Method for calling workTypeAction.store that is supposed to work (and is
     * not a target for testing) Actual testing for method store is done in
     * testStore_XXX -methods
     */
    private void store() {
        String result = workTypeAction.store();
        assertEquals("store() was unsuccessful", Action.SUCCESS, result);
    }

    private void edit() {
        String result = workTypeAction.edit();
        assertEquals("edit() was unsuccessful", Action.SUCCESS, result);
    }

    /*
     * Get all stored Users. @return all users stored
     */
    private Collection<WorkType> getAllWorkTypes() {
        return workTypeDAO.getAll();
    }

    private static boolean WorkTypeCollectionContainsId(
            Collection<WorkType> collection, int id) {
        for (WorkType wt : collection) {
            if (wt.getId() == id)
                return true;
        }
        return false;
    }

    /**
     * Generates a test user
     * 
     * @param workTypeAction
     *                springed WorkTypeAction object
     * @param numberchosen
     *                test user (1 or 2)
     * @return
     */
    public ProjectType createAndStoreTestProjectType() {
        ProjectType a = new ProjectType();
        a.setName(PROJECTTYPE_TEST_NAME);
        a.setDescription(PROJECTTYPE_TEST_DESCRIPTION);
        a.setTargetSpendingPercentage(PROJECTTYPE_TEST_PERCENTAGE);

        projectTypeDAO.store(a);

        return a;
    }

    public void checkItems(WorkType storedWorkType,
            ProjectType storedProjectType, ProjectType test) {
        projectTypeDAO.refresh(storedProjectType);

        assertNotNull("worktype was null", storedWorkType);
        assertEquals("worktype had invalid name", storedWorkType.getName(),
                TEST_NAME);
        assertEquals("worktype had invalid description", storedWorkType
                .getDescription(), TEST_DESCRIPTION);
        assertEquals("worktype had an invalid project type", storedWorkType
                .getProjectType().getId(), test.getId());

        super.assertNotNull("projecttype was null", storedProjectType);
        super.assertEquals("projecttype had invalid id", test.getId(),
                storedProjectType.getId());
        super.assertTrue("projecttype had invalid name", storedProjectType
                .getName().equals(PROJECTTYPE_TEST_NAME));
        super.assertTrue("projecttype had invalid description",
                storedProjectType.getName().equals(
                        PROJECTTYPE_TEST_DESCRIPTION));
        super.assertTrue("projecttype had invalid worktype-collection",
                WorkTypeCollectionContainsId(storedProjectType.getWorkTypes(),
                        storedWorkType.getId()));
    }

    public void checkStoreResult(ProjectType test) {
        WorkType storedWorkType = workTypeDAO.get(workTypeAction
                .getStoredWorkTypeId());
        ProjectType storedProjectType = projectTypeDAO.get(test.getId());

        checkItems(storedWorkType, storedProjectType, test);
    }

    void reset() {
        workTypeAction.setWorkTypeId(0);
        workTypeAction.setWorkType(null);
        workTypeAction.setProjectTypeId(0);
    }

    /** * Actual test methods * */

    public void testCreate() {
        reset();

        ProjectType test = createAndStoreTestProjectType();
        workTypeAction.setProjectTypeId(test.getId());

        create();

        super.assertEquals("New work type had an invalid id", 0, workTypeAction
                .getWorkTypeId());
        super.assertNotNull("new work type object was null ", workTypeAction
                .getWorkType());

        projectTypeDAO.remove(test);
    }

    public void testStore() {
        reset();

        ProjectType test = createAndStoreTestProjectType();
        workTypeAction.setProjectTypeId(test.getId());

        create();

        configWorkType(TEST_NAME, TEST_DESCRIPTION, test);

        int n = getAllWorkTypes().size();

        store();

        super
                .assertEquals(
                        "The total number of stored worktypes didn't grow up with store().",
                        n + 1, this.getAllWorkTypes().size());

        checkStoreResult(test);

        projectTypeDAO.remove(test);
    }

    public void testStore_withoutCreate() {
        reset();

        @SuppressWarnings("unused")
        String result = workTypeAction.store();

        assertTrue("Store without create didn't fail",
                errorFound(workTypeAction.getText("workType.missingForm"))
                        || errorFound(workTypeAction
                                .getText("workType.projectTypeNotFound"))
                        || errorFound(workTypeAction
                                .getText("wotkType.notFound")));
    }

    public void testEdit() {
        reset();

        ProjectType test = createAndStoreTestProjectType();
        workTypeAction.setProjectTypeId(test.getId());

        create();
        configWorkType(TEST_NAME, TEST_DESCRIPTION, test);
        store();

        workTypeAction.getWorkType();
        workTypeAction.setWorkType(null);

        workTypeAction.setProjectTypeId(test.getId());
        workTypeAction.setWorkTypeId(workTypeAction.getStoredWorkTypeId());

        edit();

        WorkType editedWorkType = workTypeAction.getWorkType();

        checkItems(editedWorkType, editedWorkType.getProjectType(), test);
    }

    public void testEdit_withInvalidId() {
        reset();

        workTypeAction.setProjectTypeId(-1);
        workTypeAction.setWorkTypeId(-1);

        String result = workTypeAction.edit();
        assertEquals("Invalid worktype id didn't result an error.",
                Action.ERROR, result);
        assertTrue("worktype.notFound -error not found",
                errorFound(workTypeAction.getText("wotkType.notFound"))
                        || errorFound(workTypeAction
                                .getText("workType.notFound"))
                        || errorFound(workTypeAction
                                .getText("workType.projectTypeNotFound"))
                        || errorFound(workTypeAction
                                .getText("workType.projectTypeNotFound")));
    }

    /*
     * Change the name of previously stored user and update the user.
     */
    public void testStore_withUpdate() {
        reset();

        ProjectType test = createAndStoreTestProjectType();
        workTypeAction.setProjectTypeId(test.getId());

        create();
        configWorkType(TEST_NAME, TEST_DESCRIPTION, test);
        store();

        WorkType storedWorkType = workTypeAction.getWorkType();
        storedWorkType.setName(TEST_NAME + "2");

        workTypeAction.setWorkTypeId(workTypeAction.getStoredWorkTypeId());
        workTypeAction.setWorkType(storedWorkType);

        workTypeAction.getWorkType().setDescription(TEST_DESCRIPTION + "2");

        store();

        WorkType updatedWorkType = workTypeDAO.get(workTypeAction
                .getStoredWorkTypeId());

        super.assertNotNull("WorkType wasn't stored properly (wasn't found)",
                updatedWorkType);
        super.assertEquals("Updated WorkType had invalid name", updatedWorkType
                .getName(), TEST_NAME + "2");
        super.assertEquals("Updated WorkType had invalid name", updatedWorkType
                .getDescription(), TEST_DESCRIPTION + "2");
        super.assertEquals("Updated WorkType had invalid projecttype",
                updatedWorkType.getProjectType().getId(), test.getId());

        projectTypeDAO.remove(test);
    }

    public void testDelete() {
        reset();

        ProjectType test = createAndStoreTestProjectType();
        workTypeAction.setProjectTypeId(test.getId());

        create();
        configWorkType(TEST_NAME, TEST_DESCRIPTION, test);
        store();

        int n = this.getAllWorkTypes().size();
        int id = workTypeAction.getStoredWorkTypeId();

        workTypeAction.setWorkTypeId(id);
        workTypeAction.delete();

        super.assertEquals(
                "The number of users didn't decrease with delete().", n - 1,
                getAllWorkTypes().size());

        WorkType worktype = workTypeDAO.get(id);
        super.assertNull("The deleted user wasn't properly deleted", worktype);

        projectTypeDAO.remove(test);
    }

    public void testDelete_withInvalidId() {
        reset();

        workTypeAction.setWorkTypeId(-1);
        try {
            String result = workTypeAction.delete();

            if (result.equals(Action.SUCCESS))
                fail("delete() with invalid id -1 was accepted.");
        } catch (IllegalArgumentException iae) {
        }
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }
}
