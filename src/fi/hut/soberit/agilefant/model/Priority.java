package fi.hut.soberit.agilefant.model;

public enum Priority {
	TRIVIAL, MINOR, MAJOR, CRITICAL, BLOCKER;
	
	public int getOrdinal(){
		return this.ordinal();
	}
	
	public String getName(){
		return this.name();
	}
}
