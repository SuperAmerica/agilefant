package fi.hut.soberit.agilefant.model;

import java.util.Collection;

public interface TaskContainer {
    public Collection<Task> getTasks();
    public void setTasks(Collection<Task> tasks);
    public String getDescription();
    public int getId();
}
