package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
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

	private static final String ACTIVITYTYPE_TEST_NAME = "ActivityType name";

	private static final String ACTIVITYTYPE_TEST_DESCRIPTION = "ActivityType name";

	private static final int ACTIVITYTYPE_TEST_PERCENTAGE = 23;

	WorkTypeDAO workTypeDAO;

	// The field and setter to be used by Spring
	private WorkTypeAction workTypeAction;

	private ActivityTypeDAO activityTypeDAO;

	public void setWorkTypeAction(WorkTypeAction workTypeAction) {
		this.workTypeAction = workTypeAction;
	}

	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}

	/*
	 * Checks, if there are any given error countered.
	 */
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
			ActivityType activityType) {
		workTypeAction.getWorkType().setName(name);
		workTypeAction.getWorkType().setDescription(description);
		workTypeAction.getWorkType().setActivityType(activityType);
		workTypeAction.setActivityTypeId(activityType.getId());
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
	 *            springed WorkTypeAction object
	 * @param numberchosen
	 *            test user (1 or 2)
	 * @return
	 */
	public ActivityType createAndStoreTestActivityType() {
		ActivityType a = new ActivityType();
		a.setName(ACTIVITYTYPE_TEST_NAME);
		a.setDescription(ACTIVITYTYPE_TEST_DESCRIPTION);
		a.setTargetSpendingPercentage(ACTIVITYTYPE_TEST_PERCENTAGE);

		activityTypeDAO.store(a);

		return a;
	}

	public void checkItems(WorkType storedWorkType,
			ActivityType storedActivityType, ActivityType test) {
		activityTypeDAO.refresh(storedActivityType);

		assertNotNull("worktype was null", storedWorkType);
		assertEquals("worktype had invalid name", storedWorkType.getName(),
				TEST_NAME);
		assertEquals("worktype had invalid description", storedWorkType
				.getDescription(), TEST_DESCRIPTION);
		assertEquals("worktype had an invalid activity type", storedWorkType
				.getActivityType().getId(), test.getId());

		super.assertNotNull("activitytype was null", storedActivityType);
		super.assertEquals("activitytype had invalid id", test.getId(),
				storedActivityType.getId());
		super.assertTrue("activitytype had invalid name", storedActivityType
				.getName().equals(ACTIVITYTYPE_TEST_NAME));
		super.assertTrue("activitytype had invalid description",
				storedActivityType.getName().equals(
						ACTIVITYTYPE_TEST_DESCRIPTION));
		super.assertTrue("activitytype had invalid worktype-collection",
				WorkTypeCollectionContainsId(storedActivityType.getWorkTypes(),
						storedWorkType.getId()));
	}

	public void checkStoreResult(ActivityType test) {
		WorkType storedWorkType = workTypeDAO.get(workTypeAction
				.getStoredWorkTypeId());
		ActivityType storedActivityType = activityTypeDAO.get(test.getId());

		checkItems(storedWorkType, storedActivityType, test);
	}

	void reset() {
		workTypeAction.setWorkTypeId(0);
		workTypeAction.setWorkType(null);
		workTypeAction.setActivityTypeId(0);
	}

	/** * Actual test methods * */

	public void testCreate() {
		reset();

		ActivityType test = createAndStoreTestActivityType();
		workTypeAction.setActivityTypeId(test.getId());

		create();

		super.assertEquals("New work type had an invalid id", 0, workTypeAction
				.getWorkTypeId());
		super.assertNotNull("new work type object was null ", workTypeAction
				.getWorkType());

		activityTypeDAO.remove(test);
	}

	public void testStore() {
		reset();

		ActivityType test = createAndStoreTestActivityType();
		workTypeAction.setActivityTypeId(test.getId());

		create();

		configWorkType(TEST_NAME, TEST_DESCRIPTION, test);

		int n = getAllWorkTypes().size();

		store();

		super
				.assertEquals(
						"The total number of stored worktypes didn't grow up with store().",
						n + 1, this.getAllWorkTypes().size());

		checkStoreResult(test);

		activityTypeDAO.remove(test);
	}

	public void testStore_withoutCreate() {
		reset();

		String result = workTypeAction.store();

		assertTrue("Store without create didn't fail",
				errorFound(workTypeAction.getText("workType.missingForm"))
						|| errorFound(workTypeAction
								.getText("workType.activityTypeNotFound"))
						|| errorFound(workTypeAction
								.getText("wotkType.notFound")));
	}

	public void testEdit() {
		reset();

		ActivityType test = createAndStoreTestActivityType();
		workTypeAction.setActivityTypeId(test.getId());

		create();
		configWorkType(TEST_NAME, TEST_DESCRIPTION, test);
		store();

		WorkType temp = workTypeAction.getWorkType();
		workTypeAction.setWorkType(null);

		workTypeAction.setActivityTypeId(test.getId());
		workTypeAction.setWorkTypeId(workTypeAction.getStoredWorkTypeId());

		edit();

		WorkType editedWorkType = workTypeAction.getWorkType();

		checkItems(editedWorkType, editedWorkType.getActivityType(), test);
	}

	public void testEdit_withInvalidId() {
		reset();

		workTypeAction.setActivityTypeId(-1);
		workTypeAction.setWorkTypeId(-1);

		String result = workTypeAction.edit();
		assertEquals("Invalid worktype id didn't result an error.",
				Action.ERROR, result);
		assertTrue("worktype.notFound -error not found",
				errorFound(workTypeAction.getText("wotkType.notFound"))
						|| errorFound(workTypeAction
								.getText("workType.notFound"))
						|| errorFound(workTypeAction
								.getText("workType.activityTypeNotFound"))
						|| errorFound(workTypeAction
								.getText("workType.activityTypeNotFound")));
	}

	/*
	 * Change the name of previously stored user and update the user.
	 */
	public void testStore_withUpdate() {
		reset();

		ActivityType test = createAndStoreTestActivityType();
		workTypeAction.setActivityTypeId(test.getId());

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
		super.assertEquals("Updated WorkType had invalid activitytype",
				updatedWorkType.getActivityType().getId(), test.getId());

		activityTypeDAO.remove(test);
	}

	public void testDelete() {
		reset();

		ActivityType test = createAndStoreTestActivityType();
		workTypeAction.setActivityTypeId(test.getId());

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

		activityTypeDAO.remove(test);
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

	public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
		this.activityTypeDAO = activityTypeDAO;
	}
}
