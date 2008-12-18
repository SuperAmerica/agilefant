package fi.hut.soberit.agilefant.web;

/**
 * An interface which should be implemented by actions which contain basic CRUD
 * operations. Using this class guarantees the same naming convention for
 * methods. Classes implementing CRUDAction should have also getter and setter
 * for xxxId. (eg. for UserAction handling User-objects should have getUserId()
 * and setUserId()
 * 
 * @author khel
 */
public interface CRUDAction {
    /** Return value to indicate ajax success. */
    public static final String AJAX_SUCCESS = "ajax_success";
    /** Return value to indicate ajax error. */
    public static final String AJAX_ERROR = "ajax_error";

    /**
     * Creates an object that can be manipulated and stored.
     * 
     * @return Result of the action. (See com.opensymphony.xwork.Action)
     */
    public String create();

    /**
     * Deletes an object.
     * 
     * @return Result of the action. (See com.opensymphony.xwork.Action)
     */
    public String delete();

    /**
     * Stores the object.
     * 
     * @return Result of the action. (See com.opensymphony.xwork.Action)
     */
    public String store();

    /**
     * Fetches the object for editing.
     * 
     * @return Result of the action. (See com.opensymphony.xwork.Action)
     */
    public String edit();
}
