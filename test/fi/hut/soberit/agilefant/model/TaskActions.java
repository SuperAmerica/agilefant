package fi.hut.soberit.agilefant.model;

import fit.Fixture;

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
        Todo todo = new Todo();
        todo.setName(taskName);
        this.bi.getTodos().add(todo);
    }

    public int taskAmount() {
        if (this.bi == null)
            return -1;
        else
            return this.bi.getTodos().size();
    }

    public int estimatedEffort() {
        return -1; // TODO
    }

    public int actualEffort() {
        return -1; // TODO
    }

    /*
     * public static void main(String[] args) { System.out.println("foobar1");
     * BacklogItem bi = new BacklogItem(); bi.setName("aarne");
     * System.out.println("foobar"); System.out.println(bi.getName()); }
     */
}
