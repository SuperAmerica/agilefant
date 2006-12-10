package fi.hut.soberit.agilefant.model;

public enum PracticeStatus {
	// TODO: mitkä ovat praktiikoiden tilat?
	NOT_STARTED, STARTED, BLOCKED, IMPLEMENTED, DONE;
	
	public int getOrdinal(){
		return this.ordinal();
	}
}
