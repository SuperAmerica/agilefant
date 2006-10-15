package fi.hut.soberit.agilefant.model;

public class PerformedWork extends TaskComment{
	
	private int amount; // Select unit!!
	private WorkType workType;
	private User worker;
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public User getWorker() {
		return worker;
	}
	
	public void setWorker(User worker) {
		this.worker = worker;
	}
	
	public WorkType getWorkType() {
		return workType;
	}
	
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
}
