package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.hibernate.annotations.BatchSize;

import flexjson.JSON;

@Entity
@BatchSize(size = 20)
@XmlAccessorType( XmlAccessType.NONE )
public class TaskHourEntry extends HourEntry {

    private Task task;

    public void setTask(Task task) {
        this.task = task;
    }

    @ManyToOne
    @JSON(include = false)
    public Task getTask() {
        return task;
    }

}
