package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of a Task.
 * <p>
 * Possible values are "not started", "started", "blocked", "implemented" and
 * "done".
 * 
 * @see fi.hut.soberit.agilefant.model.Task
 */
public enum TaskStatus {
	NOT_STARTED, STARTED, BLOCKED, IMPLEMENTED, DONE;

	public int getOrdinal() {
		return this.ordinal();
	}

	public String getName() {
		return this.name();
	}

}
