package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of a backlog item.
 * <p> 
 * Possible values are "not started", "started", "blocked", "implemented" and
 * "done". 
 */
public enum BacklogItemStatus {
	NOT_STARTED, STARTED, BLOCKED, IMPLEMENTED, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}

	public String getName(){
		return this.name();
	}
}
