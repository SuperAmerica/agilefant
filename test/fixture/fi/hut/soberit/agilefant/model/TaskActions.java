package fi.hut.soberit.agilefant.fixtures;

import fit.Fixture;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;

public class TaskActions extends Fixture {
	private BacklogItem bi;
	private String biName;
	private String taskName;
	
	public void backlogitemName(String backlogitemname) {
		this.biName = backlogitemname;
	}
	
	public void taskName(String taskName) {
		this.taskName = taskName;
	}
	
	public void createBacklogitem() {
		this.bi = new BacklogItem();
		bi.setName(biName);
	}

	public void createTask() {
		Task task = new Task();
		task.setName(taskName);
		this.bi.getTasks().add(task);
	}
	
	public int taskAmount() {
		if(this.bi == null)
			return -1;
		else
			return this.bi.getTasks().size();
	}
	
	public int estimatedEffort() {
		return -1; // TODO
	}
	
	public int actualEffort() {
		return -1; // TODO
	}

/*	public static void main(String[] args) {
		System.out.println("foobar1");
		BacklogItem bi = new BacklogItem();
		bi.setName("aarne");
		System.out.println("foobar");
		System.out.println(bi.getName());
	}*/
}
