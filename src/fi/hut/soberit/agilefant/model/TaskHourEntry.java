package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.BatchSize;

@Entity
@BatchSize(size = 20)
public class TaskHourEntry extends HourEntry {

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    @ManyToOne(optional = false)
    public Task getTask() {
        return task;
    }

}
