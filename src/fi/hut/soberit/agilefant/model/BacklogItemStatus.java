package fi.hut.soberit.agilefant.model;

public enum BacklogItemStatus {
	NOT_STARTED, STARTED, BLOCKED, IMPLEMENTED, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}

	public String getName(){
		return this.name();
	}
}
