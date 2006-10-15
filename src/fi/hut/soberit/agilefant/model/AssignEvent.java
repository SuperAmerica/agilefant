package fi.hut.soberit.agilefant.model;

public class AssignEvent extends TaskEvent {

	private User oldAssignee;
	private User newAssignee;
	
	public User getNewAssignee() {
		return newAssignee;
	}
	
	public void setNewAssignee(User newAssignee) {
		this.newAssignee = newAssignee;
	}
	
	public User getOldAssignee() {
		return oldAssignee;
	}
	
	public void setOldAssignee(User oldAssignee) {
		this.oldAssignee = oldAssignee;
	}
}
