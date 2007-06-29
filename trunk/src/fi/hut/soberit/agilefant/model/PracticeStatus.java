package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of a practice in a Task.
 * <p> 
 * What values should this have? 
 * <p>
 * Currently practices aren't implemented in the UI.
 * 
 * @see fi.hut.soberit.agilefant.model.PracticeAllocation
 * @see fi.hut.soberit.agilefant.model.Practice
 * @see fi.hut.soberit.agilefant.model.PracticeTemplate 
 */
public enum PracticeStatus {
	// TODO: mitkä ovat praktiikoiden tilat?
	NOT_STARTED, STARTED, BLOCKED, IMPLEMENTED, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}
}
