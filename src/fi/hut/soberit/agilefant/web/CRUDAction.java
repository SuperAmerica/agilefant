package fi.hut.soberit.agilefant.web;

/**
 * An interface which should be implemented by actions which contain basic CRUD operations.
 * Using this class guarantees the same naming convention for methods.
 * Classes implementing CRUDAction should have also getter and setter for xxxId. (eg. 
 * for UserAction handling User-objects should have getUserId() and setUserId()
 * 
 * @author khel
 */
public interface CRUDAction {
	/**
	 * Creates an object, that can be manipulated and stored.
	 * 
	 * @return Result of the action. (See com.opensymphony.xwork.Action)
	 */
	public String create();

	/**
	 * Deletes an object, based on xxxId set.
	 * 
	 * @return Result of the action. (See com.opensymphony.xwork.Action)
	 */
	public String delete();

	/**
	 * Stores the object. The stored object is new, if the setXxxId() -method
	 * wasn't called since create() -method. If setXxxId() -method is called,
	 * old object with given id is stored with updated content.
	 * 
	 * @return Result of the action. (See com.opensymphony.xwork.Action)
	 */
	public String store();

	/**
	 * Fetches the object for editing, based on xxxId set.
	 * 
	 * @return Result of the action. (See com.opensymphony.xwork.Action)
	 */
	public String edit();
}
