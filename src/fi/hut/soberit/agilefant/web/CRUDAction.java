package fi.hut.soberit.agilefant.web;

/**
 * An interface which should be implemented by actions which contain basic CRUD operations.
 * Using this class guarantees the same naming convention for methods.
 * 
 * @author khel
 */
public interface CRUDAction {
	
	public String list();
	public String create();
	public String delete();
	public String store();
	public String edit();
}
