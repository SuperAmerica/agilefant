package fi.hut.soberit.agilefant.model;

/**
 * A status enumeration, which represents the status of an iteration goal.
 * <p> 
 * Possible values are "skipped", "looks bad", "needs attention", "looking good" and
 * "done". 
 */
public enum IterationGoalStatus {
	SKIPPED, LOOKS_BAD, NEEDS_ATTENTION, LOOKING_GOOD, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}
}