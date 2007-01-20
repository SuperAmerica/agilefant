package fi.hut.soberit.agilefant.model;

public enum IterationGoalStatus {
	SKIPPED, LOOKS_BAD, NEEDS_ATTENTION, LOOKING_GOOD, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}
}