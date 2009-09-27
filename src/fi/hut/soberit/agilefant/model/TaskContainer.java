package fi.hut.soberit.agilefant.model;

import java.util.Set;

public interface TaskContainer {
    public Set<Task> getTasks();
    public void setTasks(Set<Task> tasks);
    public String getDescription();
    public int getId();
}
