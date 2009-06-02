package fi.hut.soberit.agilefant.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@Entity
@Table(name = "tasks_historyentries")
public class TaskHistoryEntry {

    private ExactEstimate estimate;

    private int id;

    private ExactEstimate originalEstimate;

    private Task task;

    private DateTime timestamp;
    
    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "estimate")))
    public ExactEstimate getEstimate() {
        return estimate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    @Embedded
    @AttributeOverrides(@AttributeOverride(name = "minorUnits", column = @Column(name = "originalestimate")))
    public ExactEstimate getOriginalEstimate() {
        return originalEstimate;
    }

    @ManyToOne(optional = false)
    public Task getTask() {
        return task;
    }

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setEstimate(ExactEstimate estimate) {
        this.estimate = estimate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOriginalEstimate(ExactEstimate originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

}
